package com.feng.militia_admin.service;

import com.feng.militia_admin.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.militia_admin.model.dto.LoginResult;
import com.feng.militia_admin.model.request.LoginRequest;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【user(人员表)】的数据库操作Service
* @createDate 2026-06-04 17:41:07
*/
@Service
public interface UserService extends IService<User> {

    LoginResult login(LoginRequest loginRequest);

    List<ShowUserInfoVo> showOrgUser();
}
