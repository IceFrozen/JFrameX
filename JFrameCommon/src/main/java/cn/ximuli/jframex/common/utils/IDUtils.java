package cn.ximuli.jframex.common.utils;

import java.util.UUID;

/**
 * ID Generator Utility Class
 * This class's UUID generation is relatively slow. Providing service first, will optimize later.
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class IDUtils {

	/**
	 * Get random UUID
	 *
	 * @return Random UUID
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Simplified UUID, with hyphens removed
	 *
	 * @return Simplified UUID, with hyphens removed
	 */
	public static String simpleUUID() {
		return randomUUID().replace("-", "");
	}

}
