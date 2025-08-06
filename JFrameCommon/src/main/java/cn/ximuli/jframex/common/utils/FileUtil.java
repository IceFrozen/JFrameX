package cn.ximuli.jframex.common.utils;
import cn.ximuli.jframex.common.constants.CharConstants;
import cn.ximuli.jframex.common.constants.SystemConstants;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * File Utility Class
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class FileUtil {
    /**
     * Unix-like path separator
     */
    public static final char UNIX_SEPARATOR = CharConstants.SLASH;
    /**
     * Windows path separator
     */
    public static final char WINDOWS_SEPARATOR = CharConstants.BACKSLASH;

    /**
     * Pseudo-protocol prefix for ClassPath paths (Spring compatible): "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL prefix indicating file: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";

    /** Character constant: dot {@code '..'} */
    public static final String DOUBLE_DOT = "..";

    /**
     * Invalid characters in Windows file names
     */
    private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");

    /**
     * Return file name
     *
     * @param file File
     * @return File name
     */
    public static String getName(File file) {
        return (null != file) ? file.getName() : null;
    }

    /**
     * Return file name
     *
     * @param filePath File path
     * @return File name
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }

        if (isFileSeparator(filePath.charAt(len - 1))) {
            // Remove trailing separator
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // Find the last path separator (/ or \)
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * Get file extension, extension without "."
     *
     * @param file File
     * @return Extension
     * @see #extName(File)
     */
    public static String getSuffix(File file) {
        return extName(file);
    }

    /**
     * Get file extension, extension without "."
     *
     * @param fileName File name
     * @return Extension
     * @see #extName(String)
     */
    public static String getSuffix(String fileName) {
        return extName(fileName);
    }

    /**
     * Return main file name
     *
     * @param file File
     * @return Main file name
     * @see #mainName(File)
     */
    public static String getPrefix(File file) {
        return mainName(file);
    }

    /**
     * Return main file name
     *
     * @param fileName Full file name
     * @return Main file name
     * @see #mainName(String)
     */
    public static String getPrefix(String fileName) {
        return mainName(fileName);
    }

    /**
     * Return main file name
     *
     * @param file File
     */
    public static String mainName(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        return mainName(file.getName());
    }

    /**
     * Return main file name
     *
     * @param fileName Full file name
     */
    public static String mainName(String fileName) {
        if (null == fileName) {
            return null;
        }
        int len = fileName.length();
        if (0 == len) {
            return fileName;
        }
        if (isFileSeparator(fileName.charAt(len - 1))) {
            len--;
        }

        int begin = 0;
        int end = len;
        char c;
        for (int i = len - 1; i >= 0; i--) {
            c = fileName.charAt(i);
            if (len == end && CharConstants.DOT == c) {
                // Find the last separator between file name and extension: .
                end = i;
            }
            // Find the last path separator (/ or \), if this separator is after ., continue searching, otherwise stop
            if (isFileSeparator(c)) {
                begin = i + 1;
                break;
            }
        }

        return fileName.substring(begin, end);
    }

    /**
     * Whether it is a Windows or Linux (Unix) file separator<br>
     * Windows platform separator is \, Linux (Unix) is /
     *
     * @param c Character
     * @return Whether it is a Windows or Linux (Unix) file separator
     */
    public static boolean isFileSeparator(char c) {
        return CharConstants.SLASH == c || CharConstants.BACKSLASH == c;
    }

    /**
     * Get file extension (suffix), extension without "."
     *
     * @param file File
     * @return Extension
     */
    public static String extName(File file) {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return extName(file.getName());
    }

    /**
     * Get file extension (suffix), extension without "."
     *
     * @param fileName File name
     * @return Extension
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(CharConstants.DOT);
        if (index == -1) {
            return "";
        } else {
            String ext = fileName.substring(index + 1);
            // Extension cannot contain path-related symbols
            return StringUtil.containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? "" : ext;
        }
    }

    /**
     * Normalize path<br>
     * If the original path ends with a separator, keep it as a standard separator (/), otherwise don't keep
     *
     * @param path Original path
     * @return Normalized path
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }
        String pathToUse = path;
        if (path.startsWith(SystemConstants.CLASSPATH_URL_PREFIX)) {
            // Compatible with Spring-style ClassPath paths, remove prefix, case insensitive
             pathToUse = StringUtil.removePrefixIgnoreCase(path, SystemConstants.CLASSPATH_URL_PREFIX);
            // If path after removing classpath is only '/' then replace with ""
            if (pathToUse.trim().equalsIgnoreCase(String.valueOf(CharConstants.SLASH))) {
                pathToUse = "";
            }
        }

        if (path.startsWith(SystemConstants.FILE_URL_PREFIX)) {
            // Remove file: prefix
            pathToUse = StringUtil.removePrefixIgnoreCase(pathToUse, SystemConstants.FILE_URL_PREFIX);
            if (StringUtil.isEmpty(pathToUse)) {
                // Remove file: if empty then it's root directory
                pathToUse = CharConstants.SLASH + "";
            }

            // Recognize home directory format and convert to absolute path
            if (pathToUse.startsWith(SystemConstants.USER_HOME_SHORT)) {
                pathToUse = pathToUse.replace(SystemConstants.USER_HOME_SHORT, SystemConstants.USER_HOME);
            }
        }

        return pathToUse;
    }

    /**
     * Create File object, equivalent to calling new File(), no processing
     *
     * @param path File path
     * @return File
     */
    public static File newFile(String path) {
        return new File(path);
    }

    /**
     * Get URL, commonly used when using absolute paths
     *
     * @param file File object corresponding to URL
     * @return URL
     */
    public static URL getURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error occurred when get URL!", e);
        }
    }

    /**
     * Check if file is an image file
     *
     * @param fileName File name
     * @return true if it's an image file, false otherwise
     */
    public static boolean isImageFile(String fileName) {
        return fileName.endsWith(".png")  ||
                fileName.endsWith(".jpg")  ||
                fileName.endsWith(".jpeg")  ||
                fileName.endsWith(".svg")  ||
                fileName.endsWith(".gif");
    }
}
