package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.ui.manager.AppSplashScreen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = {"cn.ximuli.jframex"})
@Slf4j
public class Application {
    public static void main(String[] args) {
        AppSplashScreen appSplashScreen = AppSplashScreen.getInstance();
        // TODO 这里reading configuration file in personal folder
        System.setProperty("app.style.name", "default");
        // System.setProperty("user.language","en");

        appSplashScreen.setVisible(true);
        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);
    }

}
