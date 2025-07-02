package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.service.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = {"cn.ximuli.jframex"})
@Slf4j
public class Application implements CommandLineRunner {
    public static void main(String[] args) {

        ApplicationInitializer.init(args);

        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        Environment env = SpringUtils.getEnv();
    }
}
