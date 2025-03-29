package cn.ximuli.jframex.service;

import cn.ximuli.jframex.model.Page;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.model.UserType;

import java.util.List;

public interface UserService {

    List<UserType> queryAllUserType();


    List<User> queryAllUserByPage(int page, int pageSize);



    Page<User> queryUserByPage(int page, int pageSize);


    Page<User> searchUserByPage(String searchInput, int page, int pageSize);


    Page<User> searchUserByPage(String dpPath, String searchInput, int page, int pageSize);
}
