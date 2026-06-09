package com.feng.militia_admin.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author FengMeng
 * @version 1.0
 * @date 2026/6/5 11:26
 * @description 添加用户请求的数据载体
 */
@Data
public class AddUserApplyRequest {
    /**
     * 人员姓名
     */
    private String name;

    /**
     * 人员手机号
     */
    private String phone;

    /**
     * 人员性别（0-女生，1-男生）
     */
    private Integer gender;

    /**
     * 所属组织id
     */
    private Long orgId;

    /**
     * 角色id
     */
    private Long roleId;
}
