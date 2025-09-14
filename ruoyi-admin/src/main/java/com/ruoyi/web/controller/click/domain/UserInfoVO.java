// 在 com.ruoyi.click.domain.vo 包下创建 UserInfoVO.java
package com.ruoyi.web.controller.click.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserInfoVO {
    // 用户ID
    private Long uid;

    // 用户名
    private String username;

    // 手机号
    private String phoneNumber;

    // 邀请码
    private String invitationCode;

    // 账户余额
    private BigDecimal accountBalance;

    // 注册时间
    private String createTime;

    // 累计充值
    private BigDecimal totalRecharge = BigDecimal.ZERO;

    // 累计收益
    private BigDecimal totalProfit = BigDecimal.ZERO;
}
