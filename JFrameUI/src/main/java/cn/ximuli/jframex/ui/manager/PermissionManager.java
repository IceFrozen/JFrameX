package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.constants.PermissionConstants;
import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.model.Role;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PermissionManager {

    /**
     * Check if user has permission for specified permission ID
     *
     * @param user         Logged in user
     * @param permissionId Permission ID
     * @return Whether user has permission
     */
    public boolean hasPermission(LoggedInUser user, String permissionId) {
        if (user == null || user.getUser() == null || user.getUser().getUserRole() == null) {
            log.warn("User or user role is null, denying access to permission: {}", permissionId);
            return false;
        }

        UserRole userRole = user.getUser().getUserRole();
        if (!userRole.isEnabled()) {
            log.warn("User role is disabled, denying access to permission: {}", permissionId);
            return false;
        }

        List<Role> roles = getRoles(user.getUser());
        if (roles == null || roles.isEmpty()) {
            log.warn("User has no assigned roles, denying access to permission: {}", permissionId);
            return false;
        }

        if (hasAdmin(roles)) {
            return true;
        }
        // Check if any of user's roles contain the permission ID
        return getUserPermissions(user).stream()
                .anyMatch(userPermission -> userPermission.equals(permissionId));
    }

    /**
     * Get all permission IDs for user
     *
     * @param user Logged in user
     * @return Set of permission IDs
     */
    public Set<String> getUserPermissions(LoggedInUser user) {
        if (user == null || user.getUser() == null || user.getUser().getUserRole() == null) {
            return Set.of();
        }

        UserRole userRole = user.getUser().getUserRole();
        if (!userRole.isEnabled()) {
            return Set.of();
        }

        List<Role> roles = userRole.getRoles();
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        return roles.stream()
                .filter(Role::isEnabled)
                .filter(role -> role.getPermissionIds() != null)
                .flatMap(role -> role.getPermissionIds().stream())
                .collect(Collectors.toSet());
    }

    public boolean hasAdmin(List<Role> roles) {
        return roles.stream().anyMatch(role -> role.getName().equals(PermissionConstants.ADMIN));
    }

    public List<Role> getRoles(User user) {
        List<Role> roles = new ArrayList<>();
        UserRole userRole = user.getUserRole();
        if (userRole != null) {
            roles = userRole.getRoles().stream().filter(Role::isEnabled).toList();
        }
        return roles;
    }

    public boolean isAdmin(LoggedInUser user) {
        if (user == null || user.getUser() == null || user.getUser().getUserRole() == null) {
            return false;
        }
        List<Role> roles = getRoles(user.getUser());
        return hasAdmin(roles);
    }
}