package cn.ximuli.jframex.common.exception;

public class JSONException  extends FormativeException {
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

