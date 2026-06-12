package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 权限表
 * @TableName permission
 */
@TableName(value ="permission")
@Data
public class Permission implements Serializable {
    /**
     * 主键，权限编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 权限标识（如 militia:info:view）
     */
    @TableField(value = "permission")
    private String permission;

    /**
     * 权限类型（0-目录，1-菜单，2-按钮/接口）
     */
    @TableField(value = "menu_type")
    private Integer menuType;

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