package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.service.LoginService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements LoginService {
    public LoggedInUser login(String userName, String password) {
        if (StringUtil.isBlank(userName) || StringUtil.isBlank(password)) {
            return null;
        }
        return new LoggedInUser(userName, password, LocalDateTime.now().plusDays(1));
    }
}
