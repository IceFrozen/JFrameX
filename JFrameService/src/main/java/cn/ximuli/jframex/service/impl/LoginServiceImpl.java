package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    public User login(String userName, String password) {
        if (StringUtil.isBlank(userName) || StringUtil.isBlank(password)) {
            return null;
        }
        return new User(userName, password);
    }
}
