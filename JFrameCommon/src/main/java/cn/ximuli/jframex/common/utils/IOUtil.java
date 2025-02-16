package cn.ximuli.jframex.common.utils;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class IOUtil extends IOUtils {

    /**
     * 从Reader中读取String，读取完毕后并不关闭Reader
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
