package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Role {
    private int id;
    private String name;
    private String description;
    private List<String> permissionIds; // UI component permission ID list
    private boolean enabled;
} 