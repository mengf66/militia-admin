package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【permission(权限表)】的数据库操作Mapper
* @createDate 2026-06-04 20:14:55
* @Entity com.feng.militia_admin.model.domain.Permission
*/
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    List<String> selectPermission(@Param("ids") List<Integer> ids);

    List<Permission> selectPermissionsByIds(@Param("ids") List<Integer> ids);
}




