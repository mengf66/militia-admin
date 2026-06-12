package com.feng.militia_admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.model.domain.Role;
import com.feng.militia_admin.service.RoleService;
import com.feng.militia_admin.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【role(角色表)】的数据库操作Service实现
* @createDate 2026-06-04 20:21:47
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




