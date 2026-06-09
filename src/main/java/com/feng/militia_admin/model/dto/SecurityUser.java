package com.feng.militia_admin.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/4 18:14
 * @description Spring Security 内部认证用户，只用于权限判断，不用于前端展示
 */
@Data
public class SecurityUser implements UserDetails {

    @JsonIgnore
    private Long id;

    private String name;
    private String role;
    private List<String> permissionList;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // role 需要加 ROLE_ 前缀，这样才能使用 hasRole('ADMIN') 表达式
        if (role != null && !role.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        // 权限字符串直接使用，对应 hasAuthority('xxx') 表达式
        if (permissionList != null) {
            for (String permission : permissionList) {
                if (permission != null && !permission.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                }
            }
        }
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        // JWT 认证不依赖密码，返回 null
        return null;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
