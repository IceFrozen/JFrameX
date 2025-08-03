package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.service.LoginService;
import cn.ximuli.jframex.service.PermissionService;
import cn.ximuli.jframex.service.mock.MockClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements LoginService {
    
    @Autowired
    private PermissionService permissionService;
    
    public LoggedInUser login(String userName, String password) {
        if (StringUtil.isBlank(userName) || StringUtil.isBlank(password)) {
            return null;
        }

        User user = MockClass.allUser.get(0);
        
        // Set user role
        user.setUserRole(permissionService.getUserRole(user.getId()));
        
        return new LoggedInUser(userName, password, LocalDateTime.now().plusDays(1), user);
    }

}
