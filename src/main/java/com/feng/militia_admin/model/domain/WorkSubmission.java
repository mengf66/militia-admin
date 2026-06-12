package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 工作填写表
 * 每个目标组织对应一条记录
 * @TableName work_submission
 */
@TableName(value ="work_submission")
@Data
public class WorkSubmission implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联工作内容ID
     */
    @TableField(value = "work_id")
    private Long workId;

    /**
     * 填写组织ID
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 填写的工作内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 填写状态：0-未填写，1-已填写
     */
    @TableField(value = "fill_status")
    private Integer fillStatus;

    /**
     * 审批状态：0-待审批，1-已通过，2-已驳回
     */
    @TableField(value = "audit_status")
    private Integer auditStatus;

    /**
     * 审批意见
     */
    @TableField(value = "audit_remark")
    private String auditRemark;

    /**
     * 审批人ID
     */
    @TableField(value = "audit_user_id")
    private Long auditUserId;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
