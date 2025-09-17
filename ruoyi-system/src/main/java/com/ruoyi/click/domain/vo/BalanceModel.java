package com.ruoyi.click.domain.vo;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BalanceModel {

    @NotNull(message = "用户ID不能为空")
    private Long uid;

    //@NotNull(message = "请选择是增加还是减少")
    //private boolean increaseDecrease;

    //@DecimalMin(value = "0.01", inclusive = true, message = "余额必须大于0")
    @NotNull(message = "余额数值不能为空")
    private BigDecimal balance;

    @NotNull(message = "更改余额理由不能为空")
    private String reason;

    // 在 BalanceModel 类中添加（如果还没有的话）
    private BigDecimal accountBalance;

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
}
