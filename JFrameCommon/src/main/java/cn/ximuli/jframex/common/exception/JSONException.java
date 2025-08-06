package cn.ximuli.jframex.common.exception;

/**
 * JSON Exception Class
 * A custom exception class for JSON related errors
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class JSONException extends FormativeException {
    public JSONException() {
        super();
    }

    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable cause) {
        super(cause);
    }

    public JSONException(String format, Object... arguments) {
        super(format, arguments);
    }

    public JSONException(Throwable cause, String format, Object... arguments) {
        super(cause, format, arguments);
    }
}
