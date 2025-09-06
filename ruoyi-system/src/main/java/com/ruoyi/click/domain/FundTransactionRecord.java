package com.ruoyi.click.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额宝记录 m_yuebao
 *
 * @author ruoyi
 * @date 2025-09-04
 */
@Data
public class FundTransactionRecord {

    @Excel(name = "编号")
    private Long id;

    @Excel(name = "用户编号")
    private Long userId;

    @Excel(name = "转入转出类型")
    private String type;

    @Excel(name = "交易金额")
    private BigDecimal amount;

    @Excel(name = "交易时间")
    private Date transactionTime;

    @Excel(name = "交易前金额")
    private BigDecimal balanceBefore;

    @Excel(name = "交易后金额")
    private BigDecimal balanceAfter;

    @Excel(name = "备注")
    private String remark;
}
