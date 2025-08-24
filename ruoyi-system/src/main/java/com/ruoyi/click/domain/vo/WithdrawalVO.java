package com.ruoyi.click.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户提款传递参数
 */
@Data
public class WithdrawalVO {

    //用户id
    private Long userId;

    //提款方式
    private int withdrawalMethod;

    //资金密码
    private String fundPassword;

    //提款金额
    private BigDecimal amount;
}
