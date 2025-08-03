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
        adminRole.setName("Admin");
        adminRole.setDescription("System Administrator with all permissions");
        adminRole.setEnabled(true);
        adminRole.setPermissionIds(Arrays.asList(
                PermissionConstants.ADMIN,
                PermissionConstants.BASE_PERMISSION,
                PermissionConstants.MENU_FILE,
                PermissionConstants.MENU_EDIT,
                PermissionConstants.MENU_VIEW,
                PermissionConstants.MENU_USER,
                PermissionConstants.MENU_OPTIONS,
                PermissionConstants.MENU_HELP,
                PermissionConstants.FRAME_BASIC_COMPONENTS,
                PermissionConstants.FRAME_CONTAINER_COMPONENTS,
                PermissionConstants.FRAME_DATA_COMPONENTS,
                PermissionConstants.FRAME_EXTRAS_COMPONENTS,
                PermissionConstants.FRAME_TAB_COMPONENTS,
                PermissionConstants.FRAME_OPTION_COMPONENTS,
                PermissionConstants.FRAME_CREATE_NEW,
                PermissionConstants.FRAME_USER_DETAIL,
                PermissionConstants.FRAME_CONTACT_DETAILS,
                PermissionConstants.PANEL_BASIC_COMPONENTS,
                PermissionConstants.PANEL_CONTAINER_COMPONENTS,
                PermissionConstants.PANEL_DATA_COMPONENTS,
                PermissionConstants.PANEL_EXTRAS_COMPONENTS,
                PermissionConstants.PANEL_TAB_COMPONENTS,
                PermissionConstants.PANEL_OPTION_COMPONENTS,
                PermissionConstants.TOOLBAR_MAIN,
                PermissionConstants.TOOLBAR_EDIT,
                PermissionConstants.TOOLBAR_VIEW,
                PermissionConstants.BUTTON_NEW,
                PermissionConstants.BUTTON_OPEN,
                PermissionConstants.BUTTON_SAVE,
                PermissionConstants.BUTTON_PRINT,
                PermissionConstants.BUTTON_EXIT,
                PermissionConstants.BUTTON_UNDO,
                PermissionConstants.BUTTON_REDO,
                PermissionConstants.BUTTON_COPY,
                PermissionConstants.BUTTON_PASTE,
                PermissionConstants.BUTTON_CUT,
                PermissionConstants.BUTTON_FIND,
                PermissionConstants.BUTTON_REPLACE,
                PermissionConstants.USER_MANAGEMENT,
                PermissionConstants.USER_CREATE,
                PermissionConstants.USER_EDIT,
                PermissionConstants.USER_DELETE,
                PermissionConstants.USER_VIEW,
                PermissionConstants.SYSTEM_SETTINGS,
                PermissionConstants.SYSTEM_CONFIG,
                PermissionConstants.SYSTEM_BACKUP,
                PermissionConstants.SYSTEM_RESTORE,
                PermissionConstants.HELP_ABOUT,
                PermissionConstants.HELP_MANUAL,
                PermissionConstants.HELP_SUPPORT
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
                PermissionConstants.BASE_PERMISSION,
                PermissionConstants.MENU_FILE,
                PermissionConstants.MENU_EDIT,
                PermissionConstants.MENU_VIEW,
                PermissionConstants.MENU_HELP,
                PermissionConstants.FRAME_BASIC_COMPONENTS,
                PermissionConstants.FRAME_CONTAINER_COMPONENTS,
                PermissionConstants.FRAME_DATA_COMPONENTS,
                PermissionConstants.PANEL_BASIC_COMPONENTS,
                PermissionConstants.PANEL_CONTAINER_COMPONENTS,
                PermissionConstants.PANEL_DATA_COMPONENTS,
                PermissionConstants.TOOLBAR_MAIN,
                PermissionConstants.TOOLBAR_EDIT,
                PermissionConstants.BUTTON_NEW,
                PermissionConstants.BUTTON_OPEN,
                PermissionConstants.BUTTON_SAVE,
                PermissionConstants.BUTTON_UNDO,
                PermissionConstants.BUTTON_REDO,
                PermissionConstants.BUTTON_COPY,
                PermissionConstants.BUTTON_PASTE,
                PermissionConstants.BUTTON_CUT,
                PermissionConstants.HELP_ABOUT,
                PermissionConstants.HELP_MANUAL
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
                PermissionConstants.BASE_PERMISSION,
                PermissionConstants.MENU_VIEW,
                PermissionConstants.MENU_HELP,
                PermissionConstants.FRAME_BASIC_COMPONENTS,
                PermissionConstants.PANEL_BASIC_COMPONENTS,
                PermissionConstants.TOOLBAR_MAIN,
                PermissionConstants.HELP_ABOUT
        ));
        return guestRole;
    }
} 