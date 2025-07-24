package cn.ximuli.jframex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoggedInUser {
    private String username;
    private String token;
    private LocalDateTime expireTime;


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
