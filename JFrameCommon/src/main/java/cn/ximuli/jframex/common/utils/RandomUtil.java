package cn.ximuli.jframex.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random Utility Class
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class RandomUtil {

	/**
	 * Numbers used for random selection
	 */
	public static final String BASE_NUMBER = "0123456789";
	/**
	 * Characters used for random selection
	 */
	public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
	/**
	 * Characters and numbers used for random selection
	 */
	public static final String BASE_CHAR_NUMBER = BASE_CHAR + BASE_NUMBER;

	/**
	 * Get random number generator object<br>
	 * ThreadLocalRandom is provided in JDK 7 for concurrent random number generation,
	 * which can solve the competition contention occurring in multiple threads.
	 * Note: The returned {@link ThreadLocalRandom} object cannot be shared in a multi-threaded
	 * environment, otherwise there will be duplicate random numbers.
	 */
	public static ThreadLocalRandom getRandom() {
		return ThreadLocalRandom.current();
	}

	/**
	 * Get random number within specified range
	 *
	 * @param min Minimum number (inclusive)
	 * @param max Maximum number (exclusive)
	 * @return Random number
	 */
	public static int randomInt(int min, int max) {
		return getRandom().nextInt(min, max);
	}

	/**
	 * Get random int value
	 *
	 * @return Random number
	 */
	public static int randomInt() {
		return getRandom().nextInt();
	}

	/**
	 * Randomly get element from list
	 *
	 * @param <T>  Element type
	 * @param list List
	 * @return Random element
	 */
	public static <T> T randomEle(List<T> list) {
		return list.get(randomInt());
	}

	/**
	 * Randomly get elements from list
	 *
	 * @param <T>    Element type
	 * @param list   List
	 * @param length Length
	 * @return Random elements
	 */
	public static <T> List<T> randomEle(List<T> list, int length) {
		List<T> result = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			 result.add(list.get(randomInt()));
		}
		return result;
	}

	/**
	 * Get a random string
	 *
	 * @param baseString Sample characters for random selection
	 * @param length     String length
	 * @return Random string
	 */
	public static String randomString(String baseString, int length) {
		if (StringUtil.isEmpty(baseString)) {
			return StringUtil.EMPTY;
		}
		final StringBuilder sb = new StringBuilder(length);

		if (length < 1) {
			length = 1;
		}
		int baseLength = baseString.length();
		for (int i = 0; i < length; i++) {
			int number = randomInt(0, baseLength);
			sb.append(baseString.charAt(number));
		}
		return sb.toString();
	}

}
