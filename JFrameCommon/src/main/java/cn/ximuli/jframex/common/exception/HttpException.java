package cn.ximuli.jframex.common.exception;

/**
 * HTTP Exception Class
 * A custom exception class for HTTP related errors
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class HttpException extends FormativeException {

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String format, Object... arguments) {
        super(format, arguments);
    }

    public HttpException(Throwable cause, String format, Object... arguments) {
        super(cause, format, arguments);
    }
}
