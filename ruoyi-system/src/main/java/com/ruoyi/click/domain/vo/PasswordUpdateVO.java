package com.ruoyi.click.domain.vo;

import lombok.Data;

/**
 * 用于修改密码
 * @author ruoyi
 * @date 2025-08-21
 */
@Data
public class PasswordUpdateVO {

    /**
     * 原始密码：用于验证用户身份
     */
    private String oldPassword;

    /**
     * 新密码：必须6-20位，包含数字和字母
     */
    private String newPassword;

    /**
     * 确认密码：必须与新密码一致
     */
    private String confirmPassword;
}
