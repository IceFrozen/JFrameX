package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.constants.CharConstants;
import cn.ximuli.jframex.common.utils.FileUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resource loader manager responsible for loading and caching application image resources.
 * This class scans and loads image files (including SVG and standard images) under the specified style path
 * using Spring's resource loading mechanism, and updates the UI progress bar via progress events.
 */
@Slf4j
@Component
@Getter
public class ResourceLoaderManager implements InitializingBean {

    /** Total progress value for tracking resource loading progress */
    private final AtomicInteger totalProgress = new AtomicInteger(90);

    /** Image cache storing path-to-ImageIcon mappings */
    private final Map<String, ImageIcon> imageCache = new HashMap<>();

    private final ApplicationEventPublisher eventPublisher;
    private final ResourceLoader resourceLoader;
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * Constructor with Spring dependency injection.
     *
     * @param eventPublisher Event publisher for dispatching progress events
     * @param resourceLoader Resource loader for loading resource files
     * @param resourcePatternResolver Resource pattern resolver for scanning matching resources
     */
    @Autowired
    public ResourceLoaderManager(ApplicationEventPublisher eventPublisher, ResourceLoader resourceLoader,
                                 ResourcePatternResolver resourcePatternResolver) {
        this.eventPublisher = eventPublisher;
        this.resourceLoader = resourceLoader;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * Executes after Spring Bean initialization, loading all image resources for the specified style.
     */
    @Override
    public void afterPropertiesSet() {
        String style = System.getProperty(Application.APP_STYLE_NAME, Application.APP_STYLE_NAME_DEFAULT);
        log.info("Loading images for style: {}", style);
        loadAllImages(style);
    }

    /**
     * Asynchronously completes resource loading and updates the progress bar with a final event.
     */
    public void completeLoading() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                AppSplashScreen.setProgressBarValue(new ProgressEvent(totalProgress.get(), "app.resource.loading.complete"));
                return null;
            }
        }.execute();
    }

    /**
     * Retrieves the image icon for the specified path.
     *
     * @param path Image resource path
     * @return Corresponding ImageIcon, or null if not found
     */
    public ImageIcon getIcon(String path) {
        return imageCache.get(path);
    }

    public ImageIcon getIcon(String iconPath, int width, int height) {
        String styleName = System.getProperty(Application.APP_STYLE_NAME, Application.APP_STYLE_NAME_DEFAULT);
        String resourcePath = StringUtil.joinWith(CharConstants.STR_SLASH, "style", styleName, iconPath);
        return loadIcon(resourcePath, width, height);
    }


    /**
     * Retrieves the image for the specified path.
     *
     * @param path Image resource path
     * @return Corresponding Image, or null if not found
     */
    public Image getImage(String path) {
        ImageIcon imageIcon = imageCache.get(path);
        return imageIcon != null ? imageIcon.getImage() : null;
    }

    /**
     * Loads all image resources under the specified style path and caches them in imageCache.
     *
     * @param styleName Style name
     * @throws RuntimeException If resource loading fails
     */
    public void loadAllImages(String styleName) {
        try {
            String scanPattern = StringUtil.joinWith(CharConstants.STR_SLASH, "classpath*:style", styleName, "**", "*");
            AppSplashScreen.setProgressBarValue(new ProgressEvent(5, I18nHelper.getMessage("app.resource.scan.start")));
            totalProgress.addAndGet(-1);
            Resource[] resources = resourcePatternResolver.getResources(scanPattern);
            AppSplashScreen.setProgressBarValue(new ProgressEvent(1, I18nHelper.getMessage("app.resource.scan.end", resources.length)));
            totalProgress.addAndGet(-1);

            log.debug("Total progress: {}, resource count: {}", totalProgress.get(), resources.length);

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                String path = extractResourcePath(resource, styleName);
                if (FileUtil.isImageFile(Objects.requireNonNull(resource.getFilename()))) {
                    ImageIcon imageIcon = loadIcon(resource);
                    AppSplashScreen.setProgressBarValue(new ProgressEvent(1, I18nHelper.getMessage("app.resource.scan.loading", i + 1, resources.length)));
                    totalProgress.decrementAndGet();
                    imageCache.put(path, imageIcon);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load resources for style: {}", styleName, e);
            throw new RuntimeException("Failed to load resources: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the path relative to the style name from the resource.
     *
     * @param resource Resource object
     * @param styleName Style name
     * @return Extracted path
     * @throws IOException If retrieving the resource path fails
     */
    private String extractResourcePath(Resource resource, String styleName) throws IOException {
        String path = StringUtil.substringAfter(resource.getURL().getPath(), styleName);
        return StringUtil.substringBefore(path.substring(1), CharConstants.DOT);
    }

    /**
     * Loads a single image resource, supporting SVG and standard image formats.
     *
     * @param resource Resource object
     * @return Loaded ImageIcon
     * @throws RuntimeException If resource loading fails
     */
    private ImageIcon loadIcon(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            String extension = FileUtil.extName(resource.getFilename());
            if ("svg".equalsIgnoreCase(extension)) {
                return new FlatSVGIcon(inputStream);
            }
            return new ImageIcon(ImageIO.read(inputStream));
        } catch (IOException e) {
            log.error("Failed to load icon: {}", resource.getFilename(), e);
            throw new RuntimeException("Failed to load resource: " + resource.getFilename(), e);
        }
    }

    private ImageIcon loadIcon(String path, int with, int height) {
        try {
            String extension = FileUtil.extName(path);
            if ("svg".equalsIgnoreCase(extension)) {
                return new FlatSVGIcon(path, with, height);
            }
            return new ImageIcon(ImageIO.read(new File(path)));
        } catch (IOException e) {
            log.error("Failed to load icon: {}", path, e);
            throw new RuntimeException("Failed to load resource: " + path, e);
        }
    }
}