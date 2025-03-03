package cn.ximuli.jframex.ui.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ProgressEvent extends ApplicationEvent {
    private int value;
    private String message;

    public ProgressEvent(int progress, String message) {
        super(progress);
        this.value = progress;
        this.message = message;
    }
}
