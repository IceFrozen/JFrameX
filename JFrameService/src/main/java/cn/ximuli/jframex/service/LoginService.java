package cn.ximuli.jframex.service;

import cn.ximuli.jframex.model.LoggedInUser;

public interface LoginService {

    LoggedInUser login(String userName, String password);
}
