package cn.ximuli.jframex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoggedInUser {
    private String username;
    private String token;
}
