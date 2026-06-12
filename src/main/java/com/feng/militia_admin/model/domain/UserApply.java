package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 审核工单表
 * @TableName user_apply
 */
@TableName(value ="user_apply")
@Data
public class UserApply implements Serializable {
    /**
     * 主键，审核工单编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 人员姓名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 人员手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 人员性别（0-女生，1-男生）
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 所属组织id
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 角色id
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 审核工单状态（0-通过，1-驳回，2-未审批）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 审批意见/备注
     */
    @TableField(value = "audit_remark")
    private String auditRemark;

    /**
     * 创建人id
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 更新人id
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 是否删除（0-正常，1-删除）
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 所属组织名称（联表查询，非数据库字段）
     */
    @TableField(exist = false)
    private String orgName;

    /**
     * 角色名称（联表查询，非数据库字段）
     */
    @TableField(exist = false)
    private String roleName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}