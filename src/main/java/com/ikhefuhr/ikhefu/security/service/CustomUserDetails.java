package com.ikhefuhr.ikhefu.security.service;

import com.ikhefuhr.ikhefu.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    // Wrap your core database entity here
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Converts our custom Role enum into Spring Security's GrantedAuthority format.
     * Prepend "ROLE_" to fit standard role-based access checks (e.g., "ROLE_ADMIN").
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Maps our user's email to serve as their unique authentication username.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Links directly to your new @Builder.Default field from the entity.
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    /**
     * Custom getter so your controllers can check if this is the employee's first login.
     */
    public boolean isFirstLogin() {
        return user.getFirstLogin();
    }

    /**
     * Custom helper to easily retrieve the full name in your authentication responses.
     */
    public String getFullName() {
        return user.getFirstName() + " " + user.getLastName();
    }

    /**
     * Expose the underlying user object if needed for advanced context tracking.
     */
    public User getUser() {
        return this.user;
    }

    // Account state flags kept simple for now
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
}