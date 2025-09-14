package com.ruoyi.click.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BackOperateVo {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String notes;

    // 添加充值类型字段，数字类型
    private Integer rechargeType;
}
