package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.model.Page;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.model.UserType;
import cn.ximuli.jframex.service.UserService;
import cn.ximuli.jframex.service.mock.MockClass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<UserType> queryAllUserType() {
        return MockClass.userTypes;
    }

    @Override
    public List<User> queryAllUserByPage(int page, int pageSize) {
        return queryUserByPage(page, pageSize).getData();
    }

    @Override
    public Page<User> queryUserByPage(int page, int pageSize) {
        return MockClass.getUserByPage(page, pageSize);
    }

    @Override
    public Page<User> searchUserByPage(String searchInput, int page, int pageSize) {
        return null;
    }

    @Override
    public Page<User> searchUserByPage(String departmentId, String searchInput, int page, int pageSize) {
        return MockClass.searchUserByPage(departmentId, searchInput, page, pageSize);
    }


}
