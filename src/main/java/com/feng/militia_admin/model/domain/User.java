package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 人员表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键，人员编号
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
     * 账号密码，8位，库里存储为加密后形式
     */
    @TableField(value = "password")
    private String password;

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
     * 账号状态（0-冻结，1-正常）
     */
    @TableField(value = "status")
    private Integer status;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}