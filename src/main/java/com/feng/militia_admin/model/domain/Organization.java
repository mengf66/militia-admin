package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 组织表
 * @TableName organization
 */
@TableName(value ="organization")
@Data
public class Organization implements Serializable {
    /**
     * 主键，组织编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组织名字
     */
    @TableField(value = "name")
    private String name;

    /**
     * 组织层级（0-民兵，1-营部、连、分队，2-团机关，3-师机关，4-军机关）
     */
    @TableField(value = "org_level")
    private Integer orgLevel;

    /**
     * 组织类型（0-民兵，1-营部，2-连，3-分队，4-团机关，5-师机关，6-军机关）
     */
    @TableField(value = "org_type")
    private Integer orgType;

    /**
     * 所属父节点编号
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 负责人编号
     */
    @TableField(value = "leader_id")
    private Long leaderId;

    /**
     * 组织路径（用于层级查询）
     */
    @TableField(value = "path")
    private String path;

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