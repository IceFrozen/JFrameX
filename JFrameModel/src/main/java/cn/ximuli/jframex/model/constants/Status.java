package cn.ximuli.jframex.model.constants;

import lombok.Getter;

@Getter
public enum Status {

    NORMALLY(0, "app.message.status.normally"),

    DISABLED(-1, "app.message.status.disabled");

    private final String messageCode;

    private final int value;

    Status(int value, String messageCode) {
        this.value = value;
        this.messageCode = messageCode;
    }
}
