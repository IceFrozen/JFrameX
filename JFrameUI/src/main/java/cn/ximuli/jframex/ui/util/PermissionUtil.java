package cn.ximuli.jframex.ui.util;

import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Mate;
import cn.ximuli.jframex.ui.manager.PermissionManager;
import cn.ximuli.jframex.service.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PermissionUtil {
    
    private static PermissionManager permissionManager;
    
    private static PermissionManager getPermissionManager() {
        if (permissionManager == null) {
            permissionManager = SpringUtils.getBean(PermissionManager.class);
        }
        return permissionManager;
    }
    
    /**
     * Check if user has permission for specified permission ID
     * @param user Logged in user
     * @param permissionId Permission ID
     * @return Whether user has permission
     */
    public static boolean hasPermission(LoggedInUser user, String permissionId) {
        return getPermissionManager().hasPermission(user, permissionId);
    }
    
    /**
     * Check if user has admin permissions
     * @param user Logged in user
     * @return Whether user is admin
     */
    public static boolean isAdmin(LoggedInUser user) {

        return getPermissionManager().isAdmin( user);
    }
    
    /**
     * Filter component list by permissions
     * @param user Logged in user
     * @param components Component list
     * @return Filtered component list
     */
    public static <T> List<T> filterByPermission(LoggedInUser user, List<T> components) {
        if (isAdmin(user)) {
            return components;
        }
        
        return components.stream()
                .filter(component -> {
                    Mate mate = component.getClass().getAnnotation(Mate.class);
                    if (mate == null) {
                        return true; // Components without Mate annotation are visible by default
                    }
                    String permissionId = mate.id();
                    if (permissionId.isEmpty()) {
                        return true; // Components without permission ID are visible by default
                    }
                    return hasPermission(user, permissionId);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Set component visibility based on permissions
     * @param user Logged in user
     * @param component Component
     * @param permissionId Permission ID
     */
    public static void setComponentVisibility(LoggedInUser user, Component component, String permissionId) {
        boolean hasPermission = hasPermission(user, permissionId);
        component.setVisible(hasPermission);
        component.setEnabled(hasPermission);
        
        if (!hasPermission) {
            log.debug("User has no permission to access component: {}, Permission ID: {}", component.getClass().getSimpleName(), permissionId);
        }
    }
    
    /**
     * Automatically set component visibility based on Mate annotation
     * @param user Logged in user
     * @param component Component
     */
    public static void setComponentVisibilityByMate(LoggedInUser user, Component component) {
        Mate mate = component.getClass().getAnnotation(Mate.class);
        if (mate != null && !mate.id().isEmpty()) {
            setComponentVisibility(user, component, mate.id());
        }
    }
    
    /**
     * Recursively set permissions for all components in container
     * @param user Logged in user
     * @param container Container component
     */
    public static void setContainerPermissions(LoggedInUser user, Container container) {
        if (container == null) {
            return;
        }
        
        // Set permissions for container itself
        setComponentVisibilityByMate(user, container);
        
        // Recursively set permissions for child components
        for (Component child : container.getComponents()) {
            if (child instanceof Container) {
                setContainerPermissions(user, (Container) child);
            } else {
                setComponentVisibilityByMate(user, child);
            }
        }
    }
    
    /**
     * Get component permission ID
     * @param component Component
     * @return Permission ID
     */
    public static String getComponentPermissionId(Component component) {
        Mate mate = component.getClass().getAnnotation(Mate.class);
        return mate != null ? mate.id() : "";
    }
    
    /**
     * Check if component needs permission control
     * @param component Component
     * @return Whether component needs permission control
     */
    public static boolean needsPermissionControl(Component component) {
        Mate mate = component.getClass().getAnnotation(Mate.class);
        return mate != null && !mate.id().isEmpty();
    }
} 