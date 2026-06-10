package com.feng.militia_admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feng.militia_admin.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.militia_admin.model.vo.ShowUserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Lenovo
* @description 针对表【user(人员表)】的数据库操作Mapper
* @createDate 2026-06-04 17:41:07
* @Entity com.feng.militia_admin.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询指定组织及其所有下属组织内的用户
     */
    List<ShowUserInfoVo> selectUsersByOrgId(
            @Param("orgId") Long orgId,
            @Param("keyword") String keyword
    );

    /**
     * 分页查询指定组织及其所有下属组织内的用户
     */
    IPage<ShowUserInfoVo> selectUsersByOrgIdPage(
            IPage<ShowUserInfoVo> page,
            @Param("orgId") Long orgId,
            @Param("keyword") String keyword
    );

    /**
     * 只查指定组织的直属用户（不含下属组织）
     */
    List<ShowUserInfoVo> selectUsersByOrgIdOnly(
            @Param("orgId") Long orgId,
            @Param("keyword") String keyword
    );

    /**
     * 按组织分组统计人数
     */
    List<Map<String, Object>> countUsersGroupByOrg(
            @Param("orgId") Long orgId
    );

    Long getUserOrgId(@Param("user_id") Long userId);

    List<Long> getUserIds(@Param("org_id") Long orgId);
}




