package com.feng.militia_admin.mapper;

import com.feng.militia_admin.model.domain.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【organization(组织表)】的数据库操作Mapper
* @createDate 2026-06-08 10:36:29
* @Entity com.feng.militia_admin.model.domain.Organization
*/
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    /**
     * 查询当前组织的所有下属组织（path以当前path开头，排除自己）
     */
    List<Organization> selectSubordinates(@Param("orgId") Long orgId);

    /**
     * 查询当前组织及其所有下属组织（path以当前path开头，包含自己）
     */
    List<Organization> selectSelfAndSubordinates(@Param("orgId") Long orgId);
}




