package com.ruoyi.click.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额宝对象 m_yuebao
 *
 * @author ruoyi
 * @date 2025-09-04
 */
@Data
public class YueBao {

    private static final long serialVersionUID = 1L;

    @Excel(name = "编号")
    private Long id;

    @Excel(name = "用户编号")
    private Long userId;

    @Excel(name = "余额宝金额")
    private BigDecimal totalAssets;

    @Excel(name = "昨日收益")
    private BigDecimal yesterdayProfit;

    @Excel(name = "累计收益")
    private BigDecimal cumulativeProfit;

    @Excel(name = "年化率")
    private BigDecimal annualizedRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createTime;
}
