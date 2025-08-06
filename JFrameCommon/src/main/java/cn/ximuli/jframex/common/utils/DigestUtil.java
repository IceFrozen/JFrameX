package cn.ximuli.jframex.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Digest Utility Class
 * Provides various encryption and encoding methods
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class DigestUtil {
    /**
     * Get the encryption result of MessageDigest, default using SHA-256 encryption algorithm
     *
     * @param sourceStr   Text to be encrypted
     * @param encryptName Encryption algorithm name: SHA-256, MD5
     * @return Encrypted result
     * @throws NoSuchAlgorithmException If the algorithm does not exist
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

    /**
     * Convert bytes to hexadecimal string
     */
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
     * URL decode
     */
    public static String urlDecode(String str, String charset) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, charset);
    }

    /**
     * MD5 conversion
     *
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String string) throws NoSuchAlgorithmException {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes();
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(btInput);
        byte[] md = mdInst.digest();

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
