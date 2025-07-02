package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.constants.CharConstants;
import cn.ximuli.jframex.common.utils.FileUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Getter
public class ResourceLoaderManager implements InitializingBean {
    private AtomicInteger total = new AtomicInteger(90);
    private final ApplicationEventPublisher publisher;
    private final ResourceLoader resourceLoader;
    private final ResourcePatternResolver resourcePatternResolver;
    private final Map<String, ImageIcon> imageCache = new HashMap<>();

    @Value("${app.style.name:default}")
    private String defaultStyle;

    @Autowired
    public ResourceLoaderManager(ApplicationEventPublisher publisher, ResourceLoader resourceLoader, ResourcePatternResolver resourcePatternResolver) {
        this.publisher = publisher;
        this.resourceLoader = resourceLoader;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public void afterPropertiesSet() {
        UIManager.getLookAndFeelDefaults();
        loadAllImages(defaultStyle);
    }

    public void loading() {
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                //Do other resource loading
                AppSplashScreen.setProgressBarValue(new ProgressEvent(total.get(), "finish"));
                return null;
            }
        }.execute();
    }

    public ImageIcon getIcon(String path) {
        return imageCache.get(path);
    }

    public Image getImage(String path) {
        ImageIcon imageIcon = imageCache.get(path);
        if (imageIcon != null) {
            return imageIcon.getImage();
        }
        return null;
    }

    public void loadAllImages(String styleName) {
        try {
            String scanRoot = StringUtil.joinWith(CharConstants.STR_SLASH, "classpath*:style", styleName, "**", "*");
            AppSplashScreen.setProgressBarValue(new ProgressEvent(10, I18nHelper.getMessage("app.resource.scan.start")));
            total.addAndGet(-10);
            Resource[] resources = resourcePatternResolver.getResources(scanRoot);
            AppSplashScreen.setProgressBarValue(new ProgressEvent(10, I18nHelper.getMessage("app.resource.scan.end", resources.length)));
            total.addAndGet(-10);
            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                String pathStr = StringUtil.substringAfter(resource.getURL().getPath(), styleName).substring(1);
                pathStr = StringUtil.substringBefore(pathStr, CharConstants.DOT);
                if(FileUtil.isImageFile(Objects.requireNonNull(resource.getFilename()))) {
                    ImageIcon imageIcon = loadIcon(resource);
                    AppSplashScreen.setProgressBarValue(new ProgressEvent(1, I18nHelper.getMessage("app.resource.scan.loading", i, resources.length)));
                    total.getAndDecrement();
                    imageCache.put(pathStr, imageIcon);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ImageIcon loadIcon(Resource resource) {
        try(InputStream is = resource.getInputStream()) {
            if (Objects.equals(FileUtil.extName(resource.getFilename()), "svg")) {
                return new FlatSVGIcon(is);
            }
            return new ImageIcon(ImageIO.read(is));
        } catch (IOException e) {
            throw new RuntimeException("resource not exist!");
        }
    }
}
