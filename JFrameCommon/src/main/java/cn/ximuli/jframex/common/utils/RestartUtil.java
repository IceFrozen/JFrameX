package cn.ximuli.jframex.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for restarting the Java Swing application.
 */
public class RestartUtil {

    private static final Logger log = LoggerFactory.getLogger(RestartUtil.class);

    /**
     * Restarts the current Java Swing application by launching a new JVM process
     * and exiting the current one.
     *
     * @throws IOException If an error occurs while building or executing the restart command
     */
    public static void restartApplication() throws IOException {
        // Get the current JVM's runtime information
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        String currentJarPath = getCurrentJarPath();

        // Build the command to restart the application
        List<String> command = new ArrayList<>();
        command.add(java);
        command.addAll(jvmArgs);
        command.add("-jar");
        command.add(currentJarPath);

        // Log the restart command for debugging
        log.info("Restarting application with command: {}", String.join(" ", command));

        // Start the new process
        try {
            new ProcessBuilder(command).start();
        } catch (IOException e) {
            log.error("Failed to restart application", e);
            throw new IOException("Unable to restart application: " + e.getMessage(), e);
        }

        // Exit the current process
        System.exit(0);
    }

    /**
     * Retrieves the path to the current application's JAR file.
     *
     * @return The absolute path to the JAR file
     * @throws IOException If the JAR path cannot be determined
     */
    private static String getCurrentJarPath() throws IOException {
        String jarPath = RestartUtil.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        File jarFile = new File(jarPath);
        if (!jarFile.exists() || !jarPath.endsWith(".jar")) {
            throw new IOException("Application is not running from a JAR file: " + jarPath);
        }
        return jarFile.getAbsolutePath();
    }
}