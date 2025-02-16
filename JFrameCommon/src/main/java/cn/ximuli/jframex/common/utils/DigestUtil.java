package cn.ximuli.jframex.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtil {
/**
     * 获取MessageDigest的加密结果, 默认使用sha256加密算法
     * @param sourceStr 待加密文本
     * @param encryptName 加密算法名称：sha256,md5
     * @return 加密后的结果
     * @throws NoSuchAlgorithmException 不存在该算法
     */
    public static String encrypt(String sourceStr, String encryptName) throws NoSuchAlgorithmException {
        byte[] bt = sourceStr.getBytes();
        if (encryptName == null || "".equals(encryptName)) {
            encryptName = "SHA-256";
        }
        MessageDigest md = MessageDigest.getInstance(encryptName);
        md.update(bt);
        return bytes2Hex(md.digest());
    }

    public static String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp;
        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

     /**
     * URL encode
     */
     public static String urlEncode(String str, String charset) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, charset);
    }

    /**
     * url
     */
    public static String urlDecode(String str, String charset) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, charset);
    }

    /**
     * MD5转换
     *
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String string) throws NoSuchAlgorithmException {

        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        byte[] btInput = string.getBytes();
        /** 获得MD5摘要算法的 MessageDigest 对象 */
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        /** 使用指定的字节更新摘要 */
        mdInst.update(btInput);
        /** 获得密文 */
        byte[] md = mdInst.digest();
        /** 把密文转换成十六进制的字符串形式 */
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
