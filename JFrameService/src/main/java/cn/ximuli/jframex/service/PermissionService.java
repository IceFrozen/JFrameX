package cn.ximuli.jframex.service;

import cn.ximuli.jframex.model.Role;
import cn.ximuli.jframex.model.UserRole;

import java.util.Arrays;
import java.util.List;

/**
 * Permission service class
 * Used to manage user roles and permissions
 */
public interface PermissionService {
    
    /**
     * Get user role by user ID
     * @param userId User ID
     * @return User role
     */
    UserRole getUserRole(int userId);
    
    /**
     * Create default roles
     * @return Default role list
     */
    List<Role> createDefaultRoles();
    
    /**
     * Create admin role
     * @return Admin role
     */
    Role createAdminRole();
    
    /**
     * Create user role
     * @return User role
     */
    Role createUserRole();
    
    /**
     * Create guest role
     * @return Guest role
     */
    Role createGuestRole();
} 