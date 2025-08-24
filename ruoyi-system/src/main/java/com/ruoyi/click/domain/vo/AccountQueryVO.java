package com.ruoyi.click.domain.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 用户获取账户详细咨询数据
 */
@Data
public class AccountQueryVO {

    //用户id
    private Long userId;

    // 开始时间（前端传递的查询起始时间）
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    // 结束时间（前端传递的查询结束时间）
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    // 可选：其他查询条件（如交易类型、关键词等）
    private String type; // 对应“所有类型”“充值奖金”等标签
}
