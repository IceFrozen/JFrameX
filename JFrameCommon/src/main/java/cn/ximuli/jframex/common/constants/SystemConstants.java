package cn.ximuli.jframex.common.constants;

/**
 * System Constants
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class SystemConstants {
    /**
     * Pseudo-protocol prefix for ClassPath paths (Spring compatible): "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * URL prefix indicating file: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";

    /** Current directory: dot {@code '.'} */
    public static final String CUR_DIR = ".";

    /** Parent directory: dot {@code '..'} */
    public static final String PRE_DIR = "..";

    /** User home directory */
    public static final String USER_HOME = System.getProperty("user.home");

    /** User home directory shorthand */
    public static final String USER_HOME_SHORT = "~";

    /** Root directory */
    public static final String ROOT_DIR = "/";

    /** Temporary directory */
    public static final String TMP_DIR = "/tmp";
}
