package cn.ximuli.jframex.common.utils;

import java.util.UUID;

/**
 * ID生成器工具类
 * 这类的uuid 比较慢  先提供服务，在看后续安排
 * @author lizhipeng
 */
public class IDUtils {

	/**
	 * 获取随机UUID
	 *
	 * @return 随机UUID
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 简化的UUID，去掉了横线
	 *
	 * @return 简化的UUID，去掉了横线
	 */
	public static String simpleUUID() {
		return randomUUID().replace("-", "");
	}

}