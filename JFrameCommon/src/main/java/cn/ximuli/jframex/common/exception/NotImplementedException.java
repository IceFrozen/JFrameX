package cn.ximuli.jframex.common.exception;

/**
 * Not Implemented Exception Class
 * A custom exception class for methods or features that are not yet implemented
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class NotImplementedException extends FormativeException {
    public NotImplementedException() {
        super();
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String format, Object... arguments) {
        super(format, arguments);
    }

    public NotImplementedException(Throwable cause, String format, Object... arguments) {
        super(cause, format, arguments);
    }
}
