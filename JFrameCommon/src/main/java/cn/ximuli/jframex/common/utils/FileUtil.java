package cn.ximuli.jframex.common.utils;
import cn.ximuli.jframex.common.constants.CharConstants;
import cn.ximuli.jframex.common.constants.SystemConstants;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;


/**
 * 文件工具
 *
 * @author lizhipeng
 */
public class FileUtil {
    /**
     * 类Unix路径分隔符
     */
    public static final char UNIX_SEPARATOR = CharConstants.SLASH;
    /**
     * Windows路径分隔符
     */
    public static final char WINDOWS_SEPARATOR = CharConstants.BACKSLASH;

    /**
     * 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL 前缀表示文件: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";

    /** 字符常量：点 {@code '..'} */
    public static final String DOUBLE_DOT = "..";


    /**
     * Windows下文件名中的无效字符
     */
    private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");

    /**
     * 返回文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getName(File file) {
        return (null != file) ? file.getName() : null;
    }

    /**
     * 返回文件名
     *
     * @param filePath 文件
     * @return 文件名
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
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 获取文件后缀名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     * @see #extName(File)
     */
    public static String getSuffix(File file) {
        return extName(file);
    }

    /**
     * 获得文件后缀名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     * @see #extName(String)
     */
    public static String getSuffix(String fileName) {
        return extName(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     * @see #mainName(File)
     */
    public static String getPrefix(File file) {
        return mainName(file);
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     * @see #mainName(String)
     */
    public static String getPrefix(String fileName) {
        return mainName(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     */
    public static String mainName(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        return mainName(file.getName());
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
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
                // 查找最后一个文件名和扩展名的分隔符：.
                end = i;
            }
            // 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
            if (isFileSeparator(c)) {
                begin = i + 1;
                break;
            }
        }

        return fileName.substring(begin, end);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return CharConstants.SLASH == c || CharConstants.BACKSLASH == c;
    }

    /**
     * 获取文件扩展名（后缀名），扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
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
     * 获得文件的扩展名（后缀名），扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
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
            // 扩展名中不能包含路径相关的符号
            return StringUtil.containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? "" : ext;
        }
    }

    /**
     * 修复路径<br>
     * 如果原路径尾部有分隔符，则保留为标准分隔符（/），否则不保留
     *
     * @param path 原路径
     * @return 修复后的路径
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }
        String pathToUse = path;
        if (path.startsWith(SystemConstants.CLASSPATH_URL_PREFIX)) {
            // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
             pathToUse = StringUtil.removePrefixIgnoreCase(path, SystemConstants.CLASSPATH_URL_PREFIX);
            // 如果path 去掉 classpath 之后只剩下 '/' 则 替换为 ""
            if (pathToUse.trim().equalsIgnoreCase(String.valueOf(CharConstants.SLASH))) {
                pathToUse = "";
            }
        }

        if (path.startsWith(SystemConstants.FILE_URL_PREFIX)) {
            // 去除file:前缀
            pathToUse = StringUtil.removePrefixIgnoreCase(pathToUse, SystemConstants.FILE_URL_PREFIX);
            if (StringUtil.isEmpty(pathToUse)) {
                // 去除file:如果为空 则为根目录
                pathToUse = CharConstants.SLASH + "";
            }

            // 识别home目录形式，并转换为绝对路径
            if (pathToUse.startsWith(SystemConstants.USER_HOME_SHORT)) {
                pathToUse = pathToUse.replace(SystemConstants.USER_HOME_SHORT, SystemConstants.USER_HOME);
            }
        }

        return pathToUse;
    }

    /**
     * 创建File对象，相当于调用new File()，不做任何处理
     *
     * @param path 文件路径
     * @return File
     */
    public static File newFile(String path) {
        return new File(path);
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     */
    public static URL getURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error occured when get URL!", e);
        }
    }

    public static boolean isImageFile(String fileName) {
        return fileName.endsWith(".png")  ||
                fileName.endsWith(".jpg")  ||
                fileName.endsWith(".jpeg")  ||
                fileName.endsWith(".svg")  ||
                fileName.endsWith(".gif");
    }

}
