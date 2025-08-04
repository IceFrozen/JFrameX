package cn.ximuli.jframex.service.impl;

import cn.ximuli.jframex.common.constants.PermissionConstants;
import cn.ximuli.jframex.model.Role;
import cn.ximuli.jframex.model.UserRole;
import cn.ximuli.jframex.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Override
    public UserRole getUserRole(int userId) {
        // Should get user role from database or cache
        // Currently return default admin role for demo
        UserRole userRole = new UserRole();
        userRole.setId(1);
        userRole.setName("Admin");
        userRole.setDescription("System Administrator");
        userRole.setEnabled(true);
        userRole.setRoles(Arrays.asList(createAdminRole()));
        return userRole;
    }
    
    @Override
    public List<Role> createDefaultRoles() {
        return Arrays.asList(
                createAdminRole(),
                createUserRole(),
                createGuestRole()
        );
    }
    
    @Override
    public Role createAdminRole() {
        Role adminRole = new Role();
        adminRole.setId(1);
        adminRole.setName(PermissionConstants.ADMIN);
        adminRole.setDescription("System Administrator with all permissions");
        adminRole.setEnabled(true);
        adminRole.setPermissionIds(Arrays.asList(

        ));
        return adminRole;
    }
    
    @Override
    public Role createUserRole() {
        Role userRole = new Role();
        userRole.setId(2);
        userRole.setName("User");
        userRole.setDescription("Regular user with basic functionality permissions");
        userRole.setEnabled(true);
        userRole.setPermissionIds(Arrays.asList(

        ));
        return userRole;
    }
    
    @Override
    public Role createGuestRole() {
        Role guestRole = new Role();
        guestRole.setId(3);
        guestRole.setName("Guest");
        guestRole.setDescription("Guest with view-only permissions");
        guestRole.setEnabled(true);
        guestRole.setPermissionIds(Arrays.asList(

        ));
        return guestRole;
    }
} 