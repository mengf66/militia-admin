package com.feng.militia_admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.militia_admin.model.domain.Permission;
import com.feng.militia_admin.service.PermissionService;
import com.feng.militia_admin.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【permission(权限表)】的数据库操作Service实现
* @createDate 2026-06-04 20:14:55
*/
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
    implements PermissionService{

}




