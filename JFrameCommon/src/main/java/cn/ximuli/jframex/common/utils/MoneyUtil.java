package cn.ximuli.jframex.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for payment-related conversions between fen and yuan.
 * For monetary calculations, float and double types are not allowed, only BigDecimal should be used.
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class MoneyUtil {
    private static final String YUAN = "元";
    private static final int DEFAULT_SCALE = 2;
    private static final int DEFAULT_ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    /**
     * Convert Long fen to BigDecimal yuan
     *
     * @param fen Long value representing fen
     * @return BigDecimal value representing yuan
     */
    public static BigDecimal fenToYuan(Long fen) {
        return fenToYuan(fen, DEFAULT_SCALE);
    }

    /**
     * Convert Long fen to BigDecimal yuan with specified precision scale. The specified scale must not lose precision.
     * @param fen Long value representing fen
     * @param scale Precision scale after decimal point
     * @return Long value representing fen
     */
    public static BigDecimal fenToYuan(Long fen, int scale) {
        if (fen == null) {
            return null;
        }
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100), scale, BigDecimal.ROUND_UNNECESSARY);
    }

    /**
     * Convert Integer fen to BigDecimal yuan
     *
     * @param fen Integer value representing fen
     * @return BigDecimal value representing yuan
     */
    public static BigDecimal fenToYuan(Integer fen) {
        return fenToYuan(fen, DEFAULT_SCALE);
    }

    /**
     * Convert Integer fen to BigDecimal yuan with specified precision scale. The specified scale must not lose precision.
     *
     * @param fen Integer value representing fen
     * @param scale Precision scale after decimal point
     * @return BigDecimal value representing yuan
     */
    public static BigDecimal fenToYuan(Integer fen, int scale) {
        if (fen == null) {
            return null;
        }
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100), scale, RoundingMode.UNNECESSARY);
    }

    /**
     * Convert Integer fen to String yuan
     *
     * @param fen fen value
     * @param withUnit whether to include "yuan" unit
     * @return String representation of yuan
     */
    public static String fenToYuanString(Integer fen, boolean withUnit) {
        return fenToYuanString(fen, DEFAULT_SCALE, withUnit);
    }

    /**
     * Convert Integer fen to String yuan with specified precision scale
     *
     * @param fen fen value
     * @param scale Precision scale after decimal point
     * @param withUnit whether to include "yuan" unit
     * @return String representation of yuan
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
     * Convert BigDecimal fen to String yuan
     *
     * @param fen fen value
     * @param withUnit whether to include "yuan" unit
     * @return String representation of yuan
     */
    public static String fenToYuanString(BigDecimal fen, boolean withUnit) {
        return fenToYuanString(fen, DEFAULT_SCALE, withUnit);
    }

    /**
     * Convert BigDecimal fen to String yuan with specified precision scale
     *
     * @param fen fen value
     * @param scale Precision scale after decimal point
     * @param withUnit whether to include "yuan" unit
     * @return String representation of yuan
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
     * Convert Long fen to String yuan
     *
     * @param fen Long value representing fen
     * @param withUnit whether to include "yuan" unit
     * @return String representation of yuan
     */
    public static String fenToYuanString(Long fen, boolean withUnit) {
        return fenToYuanString(fen, DEFAULT_SCALE, withUnit);
    }

    /**
     * Convert Long fen to String yuan with specified precision scale
     *
     * @param fen Long value representing fen
     * @param scale Precision scale after decimal point
     * @param withUnit whether to include "yuan" unit
     * @return String representation of yuan
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
     * Convert String yuan to Long fen. If there are fractional parts smaller than fen, they will be rounded by default.
     * @param yuan yuan value
     * @return Long value representing fen
     */
    public static Long yuanStringToFen(String yuan) {
        return yuanStringToFen(yuan, DEFAULT_ROUNDING_MODE);
    }

    /**
     * Convert String yuan to Long fen with specified rounding mode
     * @param yuan yuan value
     * @param roundingMode rounding mode
     * @return Long value representing fen
     */
    public static Long yuanStringToFen(String yuan, int roundingMode) {
        BigDecimal bigDecimal = new BigDecimal(yuan);
        // Keep two decimal places
        bigDecimal = bigDecimal.setScale(2, roundingMode);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        return multiply.longValue();
    }
}
