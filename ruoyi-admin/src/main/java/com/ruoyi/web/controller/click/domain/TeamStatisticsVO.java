// 在 com.ruoyi.click.domain.vo 包下创建 TeamStatisticsVO.java
package com.ruoyi.web.controller.click.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TeamStatisticsVO {
    // 团队总佣金
    private BigDecimal totalCommission = BigDecimal.ZERO;

    // 下级佣金
    private BigDecimal subordinateCommission = BigDecimal.ZERO;

    // 团队总充值
    private BigDecimal teamTotalRecharge = BigDecimal.ZERO;

    // 团队总收益
    private BigDecimal teamTotalProfit = BigDecimal.ZERO;

    // 团队总人数
    private Integer teamTotalCount = 0;

    // 直推人数
    private Integer directPushCount = 0;

    // 今日新增人数
    private Integer todayNewCount = 0;

    // 活跃人数
    private Integer activeCount = 0;

    // 一级人数及详细信息
    private List<UserInfoVO> level1Users;

    // 二级人数及详细信息
    private List<UserInfoVO> level2Users;

    // 三级人数及详细信息
    private List<UserInfoVO> level3Users;

    // 一级人数统计
    private Integer level1Count = 0;

    // 二级人数统计
    private Integer level2Count = 0;

    // 三级人数统计
    private Integer level3Count = 0;
}
