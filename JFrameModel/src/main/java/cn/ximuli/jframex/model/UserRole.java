package cn.ximuli.jframex.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserRole {
    private int id;
    private String name;
    private String description;
    private List<Role> roles;
    private boolean enabled;
} 