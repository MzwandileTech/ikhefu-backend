package com.ikhefuhr.ikhefu.security.service;

import com.ikhefuhr.ikhefu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Generates constructor injection for UserRepository at compile time
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security calls this method during authentication to look up a user.
     * In our platform, the "username" is the employee's email address.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(CustomUserDetails::new) // If found, wrap our User entity in CustomUserDetails
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}