package cn.ximuli.jframex.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 用于支付相关的分与元的转换。
 * 金额相关不允许用float和double类型，只能用BigDecimal。
 */
public class MoneyUtil {
    private static final String YUAN = "元";
    private  static final int DEFAULT_SCALE = 2;
    private static final int DEFAULT_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    /**
     * Long分转BigDecimal元
     *
     * @param fen Long表示的分
     * @return BigDecimal表示的元
     */
    public static BigDecimal fenToYuan(Long fen) {
        return fenToYuan(fen, DEFAULT_SCALE);
    }

    /**
     * Long分转BigDecimal元，指定精确位数。要求指定的位数不会丢失精度
     * @param fen Long表示的分
     * @param scale 小数点后精确位数
     * @return Long表示的分
     */
    public static BigDecimal fenToYuan(Long fen, int scale) {
        if (fen == null) {
            return null;
        }
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100), scale, BigDecimal.ROUND_UNNECESSARY);
    }

    /**
     * Integer分转BigDecimal元
     *
     * @param fen Integer表示的分
     * @return BigDecimal表示的元
     */
    public static BigDecimal fenToYuan(Integer fen) {
        return fenToYuan(fen, DEFAULT_SCALE);
    }

    /**
     * Integer分转BigDecimal元，指定精确位数。要求指定的位数不会丢失精度
     *
     * @param fen Integer表示的分
     * @param scale 小数点后精确位数
     * @return BigDecimal表示的元
     */
    public static BigDecimal fenToYuan(Integer fen, int scale) {
        if (fen == null) {
            return null;
        }
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100), scale, RoundingMode.UNNECESSARY);
    }

    /**
     * Integer分转字符串元
     *
     * @param fen 分
     * @param withUnit 是否带单位“元”
     * @return 字符串的元
     */
    public static String fenToYuanString(Integer fen, boolean withUnit) {
        return fenToYuanString(fen, DEFAULT_SCALE, withUnit);
    }

    /**
     * Integer分转字符串元，指定精确位数
     *
     * @param fen 分
     * @param scale 小数点后精确位数
     * @param withUnit 是否带单位“元”
     * @return 字符串的元
     */
    public static String fenToYuanString(Integer fen, int scale, boolean withUnit) {
        BigDecimal yuan = fenToYuan(fen, scale);
        if (yuan == null) {
            return null;
        }
        if (withUnit) {
            return yuan.toPlainString() + YUAN;
        } else {
            return yuan.toPlainString();
        }
    }

    /**
     * BigDecimal分转字符串元
     *
     * @param fen 分
     * @param withUnit 是否带单位“元”
     * @return 字符串的元
     */
    public static String fenToYuanString(BigDecimal fen, boolean withUnit) {
        return fenToYuanString(fen, DEFAULT_SCALE, withUnit);
    }

    /**
     * BigDecimal分转字符串元，指定精确位数
     *
     * @param fen 分
     * @param scale 小数点后精确位数
     * @param withUnit 是否带单位“元”
     * @return 字符串的元
     */
    public static String fenToYuanString(BigDecimal fen, int scale, boolean withUnit) {
        if (fen == null) {
            return null;
        }
        String yuan = fen.divide(new BigDecimal(100), scale, BigDecimal.ROUND_UNNECESSARY).toPlainString();
        if (withUnit) {
            return yuan + YUAN;
        } else {
            return yuan;
        }
    }

    /**
     * Long 分转元字符串
     *
     * @param fen Long表示的分
     * @param withUnit 是否带单位“元”
     * @return 字符串的元
     */
    public static String fenToYuanString(Long fen, boolean withUnit) {
        return fenToYuanString(fen, DEFAULT_SCALE, withUnit);
    }

    /**
     * Long 分转元字符串，指定精确位数
     *
     * @param fen Long表示的分
     * @param scale 小数点后精确位数
     * @param withUnit 是否带单位“元”
     * @return 字符串的元
     */
    public static String fenToYuanString(Long fen, int scale, boolean withUnit) {
        BigDecimal yuan = fenToYuan(fen, scale);
        if (yuan == null) {
            return null;
        }
        if (withUnit) {
            return yuan.toPlainString() + YUAN;
        } else {
            return yuan.toPlainString();
        }
    }

    /**
     * String的元转Long的分。若有比分小的部分，默认将会四舍五入
     * @param yuan 元
     * @return Long表示的fen
     */
    public static Long yuanStringToFen(String yuan) {
        return yuanStringToFen(yuan, DEFAULT_ROUNDING_MODE);
    }

    /**
     * String的元转Long的分，指定取整模式
     * @param yuan 元
     * @param roundingMode 取整模式
     * @return Long表示的fen
     */
    public static Long yuanStringToFen(String yuan, int roundingMode) {
        BigDecimal bigDecimal = new BigDecimal(yuan);
        // 保留两位小数
        bigDecimal = bigDecimal.setScale(2, roundingMode);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        return multiply.longValue();
    }
}
