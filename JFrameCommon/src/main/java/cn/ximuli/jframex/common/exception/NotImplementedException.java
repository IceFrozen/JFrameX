package cn.ximuli.jframex.common.exception;

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