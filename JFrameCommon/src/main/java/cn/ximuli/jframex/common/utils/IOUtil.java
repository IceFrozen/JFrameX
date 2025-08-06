package cn.ximuli.jframex.common.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * IO Utility Class
 * Extends Apache Commons IOUtils with additional functionality
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class IOUtil extends IOUtils {

    /**
     * Read String from Reader, does not close the Reader after reading
     *
     * @param reader Reader
     * @return String
     */
    public static String read(Reader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final CharBuffer buffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);

        while (-1 != reader.read(buffer)) {
            builder.append(buffer.flip().toString());
        }

        return builder.toString();
    }
}
