package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Lenovo
* @description 针对表【role(角色表)】的数据库操作Mapper
* @createDate 2026-06-04 20:21:47
* @Entity com.feng.militia_admin.model.domain.Role
*/
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}

