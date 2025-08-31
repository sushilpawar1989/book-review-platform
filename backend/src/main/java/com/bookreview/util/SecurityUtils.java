package com.bookreview.util;

import com.bookreview.user.User;
import com.bookreview.user.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for security-related operations.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the currently authenticated user.
     *
     * @return the authenticated User object, or null if not authenticated
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the ID of the currently authenticated user.
     *
     * @return the user ID, or null if not authenticated
     */
    public static Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * Check if the current user has the ADMIN role.
     *
     * @return true if the current user is an ADMIN, false otherwise
     */
    public static boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    /**
     * Check if the current user is the owner of a resource.
     *
     * @param resourceOwnerId the ID of the resource owner
     * @return true if the current user is the owner, false otherwise
     */
    public static boolean isOwner(final Long resourceOwnerId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(resourceOwnerId);
    }

    /**
     * Check if the current user has access to a resource (either admin or owner).
     *
     * @param resourceOwnerId the ID of the resource owner
     * @return true if the current user has access, false otherwise
     */
    public static boolean hasAccess(final Long resourceOwnerId) {
        return isAdmin() || isOwner(resourceOwnerId);
    }
}