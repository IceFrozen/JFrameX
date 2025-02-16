package cn.ximuli.jframex.ui.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ResourceReadyEvent extends ApplicationEvent {
    public ResourceReadyEvent(Object source) {
        super(source);
    }
}
