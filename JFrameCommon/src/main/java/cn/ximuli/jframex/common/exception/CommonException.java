package cn.ximuli.jframex.common.exception;

public class CommonException extends FormativeException {
    public CommonException() {
        super();
    }
    public CommonException(String message) {
        super(message);
    }

    public CommonException(Throwable cause) {
        super(cause);
    }

    public CommonException(String format, Object... arguments) {
        super(format, arguments);
    }

    public CommonException(Throwable cause, String format, Object... arguments) {
        super(cause, format, arguments);
    }
}
