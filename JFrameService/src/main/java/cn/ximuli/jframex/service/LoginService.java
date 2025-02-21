package cn.ximuli.jframex.service;

import cn.ximuli.jframex.model.User;

public interface LoginService {

    User login(String userName, String password);
}
