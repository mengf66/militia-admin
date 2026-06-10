package com.feng.militia_admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.mapper.PermissionMapper;
import com.feng.militia_admin.mapper.RoleMapper;
import com.feng.militia_admin.model.domain.Permission;
import com.feng.militia_admin.model.domain.Role;
import com.feng.militia_admin.model.domain.User;
import com.feng.militia_admin.model.dto.LoginResult;
import com.feng.militia_admin.model.dto.SecurityUser;
import com.feng.militia_admin.model.request.LoginRequest;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import com.feng.militia_admin.model.vo.UserInfoVO;
import com.feng.militia_admin.service.UserService;
import com.feng.militia_admin.mapper.UserMapper;
import com.feng.militia_admin.utils.MD5Util;
import com.feng.militia_admin.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【user(人员表)】的数据库操作Service实现
* @createDate 2026-06-04 17:41:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService, UserDetailsService {

    @Value("${login.secret-salt}")
    private String salt;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public LoginResult login(LoginRequest loginRequest) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String account = loginRequest.getAccount();
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getName, account);
            User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                return null;
            }
            String combined = salt + loginRequest.getPassword();
            String result = MD5Util.md5(combined);
            if(!result.equals(user.getPassword())) {
                return null;
            }

            return buildLoginResult(user);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ShowUserInfoVo> showOrgUser() {
        Long orgId = userMapper.getUserOrgId(SecurityUtil.getCurrentUserId());
        return userMapper.selectUsersByOrgId(orgId, "");
    }

    private LoginResult buildLoginResult(User user) {
        LoginResult result = new LoginResult();
        result.setUserId(user.getId());

        // 查询完整 Role 对象，不限制字段，确保 typeHandler 生效
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(Role::getId, user.getRoleId());
        Role role = roleMapper.selectOne(roleQueryWrapper);

        // 安全处理 menuIds 为 null 的情况
        List<Integer> menuIds = role != null && role.getMenuIds() != null ? role.getMenuIds() : Collections.emptyList();
        List<Permission> permissionList = permissionMapper.selectPermissionsByIds(menuIds);

        List<String> permissions = permissionList.stream()
                .map(Permission::getPermission)
                .collect(Collectors.toList());

        List<Permission> menus = permissionList.stream()
                .filter(p -> p.getMenuType() != null && (p.getMenuType() == 0 || p.getMenuType() == 1))
                .collect(Collectors.toList());

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setName(user.getName());
        userInfo.setRole(role != null ? role.getRole() : "");
        userInfo.setRoleName(role != null ? role.getName() : "");
        userInfo.setPermissionList(permissions);
        userInfo.setMenus(menus);
        result.setUserInfo(userInfo);

        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        SecurityUser su = new SecurityUser();
        su.setId(user.getId());
        su.setName(user.getName());

        // 查询完整 Role 对象，不限制字段
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(Role::getId, user.getRoleId());
        Role role = roleMapper.selectOne(roleQueryWrapper);
        su.setRole(role != null ? role.getRole() : "");

        // 安全处理 menuIds 为 null 的情况
        List<Integer> menuIds = role != null && role.getMenuIds() != null ? role.getMenuIds() : Collections.emptyList();
        List<String> permissionList = permissionMapper.selectPermission(menuIds);
        su.setPermissionList(permissionList);

        return su;
    }
}
