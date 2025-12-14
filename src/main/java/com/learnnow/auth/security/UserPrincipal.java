package com.learnnow.auth.security;

import com.learnnow.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learnnow.user.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Custom implementation of Spring Security's UserDetails.
 * This object holds the authenticated user's details and authorities.
 */
public class UserPrincipal implements UserDetails {

    private long id;
    private String firstName;
    private String lastName;

    //Using @JsonIgnore to prevent the password from being serialized and exposed in APIs
    //TODO can email be hidden if we use it as username
    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(long id, String firstName, String lastName, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Factory method to create a UserPrincipal from your application's User entity.
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 1. Get the single enum value
        UserRole role = user.getRole();
        // 2. Convert it to a SimpleGrantedAuthority with the ROLE_ prefix
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        return new UserPrincipal(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Spring Security uses this method for the username/principal
    }

    //TODO apply below in user management
    @Override
    public boolean isAccountNonExpired() {
        return true; // Simple implementation: account never expires
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Simple implementation: account never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Simple implementation: credentials never expire
    }

    @Override
    public boolean isEnabled() {
        return true; // Simple implementation: account is always enabled
    }


    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    // Custom equality check required by Spring Security for comparison WHY THO???
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}