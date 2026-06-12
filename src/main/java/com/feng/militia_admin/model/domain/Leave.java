package com.feng.militia_admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 请假表
 * @TableName leave
 */
@TableName(value ="`leave`")
@Data
public class Leave implements Serializable {
    /**
     * 主键，请假编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请假人ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 请假人姓名（冗余快照）
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 所属组织ID
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 请假类型：0-事假，1-病假，2-年假，3-其他
     */
    @TableField(value = "leave_type")
    private Integer leaveType;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 请假事由
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 状态：0-已通过，1-已驳回，2-待审批
     */
    @TableField(value = "status")
    private Integer status;

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

    /**
     * 审批人姓名
     */
    @TableField(value = "audit_user_name")
    private String auditUserName;

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

    /**
     * 销假状态：0-未销假，1-待确认，2-已销假
     */
    @TableField(value = "cancel_status")
    private Integer cancelStatus;

    /**
     * 销假时间
     */
    @TableField(value = "cancel_time")
    private Date cancelTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
