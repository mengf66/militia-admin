package com.feng.militia_admin.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.feng.militia_admin.model.dto.LoginResponse;
import com.feng.militia_admin.model.dto.LoginResult;
import com.feng.militia_admin.model.request.LoginRequest;
import com.feng.militia_admin.model.vo.UserInfoVO;
import com.feng.militia_admin.service.UserService;
import com.feng.militia_admin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/4 17:54
 * @description 登录功能
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest == null) {
            return R.failed("参数为空");
        }

        LoginResult loginResult = userService.login(loginRequest);
        if (loginResult == null) {
            return R.failed("账号或密码错误");
        }

        UserInfoVO userInfo = loginResult.getUserInfo();
        String token = jwtUtil.generateToken(
                loginResult.getUserId(),
                userInfo.getName(),
                userInfo.getRole(),
                userInfo.getPermissionList()
        );

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(userInfo);

        return R.ok(response);
    }
}
