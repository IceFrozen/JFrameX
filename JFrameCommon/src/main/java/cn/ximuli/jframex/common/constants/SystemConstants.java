package cn.ximuli.jframex.common.constants;

public class SystemConstants {
    /**
     * 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL 前缀表示文件: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";

    /** 当前：点 {@code '..'} */
    public static final String CUR_DIR = ".";

    /** 上一层：点 {@code '..'} */
    public static final String PRE_DIR = "..";

    /**用户家目录**/
    public static final String USER_HOME =  System.getProperty("user.home");

    /**用户家目录**/
    public static final String USER_HOME_SHORT =  "~";

    /**根目录**/
    public static final String ROOT_DIR =  "/";

    /**临时目录**/
    public static final String TMP_DIR =  "/tmp";


}
