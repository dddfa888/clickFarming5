package com.ruoyi.click.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 排行榜传递参数
 */
@Data
public class RankingVo {
    private Integer rank;          // 排名（1、2、3...）
    private Long userId;           // 用户ID
    private String headImg;         //用户头像
    private String userName;   // 用户姓名
    private BigDecimal totalAmount;// 累计利润金额

}
