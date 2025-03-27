package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class Department {

    private String id;

    private String name;

    private String parentId;

    public String toString() {
        return this.name;
    }
}
