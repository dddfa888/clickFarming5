package com.ruoyi.click.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额宝利息对象 m_yuebao_profit_record
 *
 * @author ruoyi
 * @date 2025-09-05
 */
@Data
public class YuebaoProfitRecord {

    @Excel(name = "编号")
    private Long id;

    @Excel(name = "用户编号")
    private Long userId;

    @Excel(name = "收益日期")
    private Date profitDate; // 收益日期

    @Excel(name = "当日收益")
    private BigDecimal dailyProfit; // 当日收益

    @Excel(name = "计算时的总资产")
    private BigDecimal totalAssets; // 计算时的总资产

    @Excel(name = "计算时的年化率")
    private BigDecimal annualizedRate; // 计算时的年化率

    @Excel(name = "创建时间")
    private Date createTime;

}
