package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 工作管理表
 * @TableName work
 */
@TableName(value ="work")
@Data
public class Work implements Serializable {
    /**
     * 主键，工作编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工作标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 工作类型：0-月工作计划，1-工作总结，2-专项活动报告
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 发布组织ID
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 目标组织ID，逗号分隔
     */
    @TableField(value = "target_org_ids")
    private String targetOrgIds;

    /**
     * 状态：0-草稿，1-已发布
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

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
     * 是否删除：0-正常，1-删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
