package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.constants.CharConstants;
import cn.ximuli.jframex.common.constants.SystemConstants;
import cn.ximuli.jframex.common.utils.FileUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Getter
public class ResourceLoaderManager {
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    private Map<String, ImageIcon> imageCache = new HashMap<>();

    @Value("${app.style.name}")
    private String defaultStyle;

    public void loading() {
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws InterruptedException {
                // 预加载字体/图片等资源
                UIManager.getLookAndFeelDefaults();
                loadAllImages(defaultStyle);
                FrameManager.publishEvent(new ProgressEvent(90,"images init"));
                return null;
            }
        }.execute();
    }

    public ImageIcon getIcon(String path) {
        return imageCache.get(path);
    }

    public void loadAllImages(String styleName) {
        try {
            String scanRoot = StringUtil.joinWith(CharConstants.STR_SLASH, "classpath*:style", styleName, "*.*");
            Resource[] resources = resourcePatternResolver.getResources(scanRoot);
            for (Resource resource : resources) {
                // 将资源转换为ImageIcon
                String pathStr = StringUtil.substringAfter(resource.getURL().getPath(), styleName).substring(1);
                pathStr = StringUtil.substringBefore(pathStr, CharConstants.DOT);
                if(isImageFile(resource.getFilename())) {
                    ImageIcon imageIcon = loadIcon(resource);
                    imageCache.put(pathStr, imageIcon);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isImageFile(String fileName) {
        return fileName.endsWith(".png")  ||
                fileName.endsWith(".jpg")  ||
                fileName.endsWith(".jpeg")  ||
                fileName.endsWith(".gif");
    }

    private ImageIcon loadIcon(Resource resource) {
        try(InputStream is = resource.getInputStream()) {
            return new ImageIcon(ImageIO.read(is));
        } catch (IOException e) {
            throw new RuntimeException("resource not exist!");
        }
    }
}
