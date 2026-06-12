package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

/**
 * 角色表
 * @TableName role
 */
@TableName(value = "role", autoResultMap = true)
@Data
public class Role implements Serializable {
    /**
     * 主键，角色编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 权限列表（关联权限表ID数组）
     */
    @TableField(typeHandler = JacksonTypeHandler.class, value = "menu_ids")
    private List<Integer> menuIds;

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
     * 角色标识
     */
    @TableField(value = "role")
    private String role;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}