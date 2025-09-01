<template>
  <div class="withdraw-container">
    <!-- 头部区域 -->
    <HeaderBar :title="t('提现')" backcolor="#fff" />
    <!-- 选择提现方式区域 -->
    <div class="withdraw-method">
      <div class="method-title">选择提现方式</div>
      <!--<div class="bank-card">
        <img src alt="银联标识" class="unionpay-icon" />
        <div class="bank-info">
          <div class="bank-name">浙商银行</div>
          <div class="card-number">xxccc 8888 **** **** 8844</div>
        </div>
      </div>-->
    </div>

    <!-- 可用提现金额区域 -->
    <div class="available-amount">
      <span>可用提现金额:</span>
      <span class="amount">0.00</span>
    </div>

    <!-- 输入金额区域 -->
    <div class="input-amount">
      <input type="number" placeholder="输入金额" v-model="withdrawAmount" />
      <button class="all-btn">全部</button>
    </div>

    <!-- 提现次数和额度区域 -->
    <div class="withdraw-info">
      <span>今日提现次数 0/3</span>
      <span>剩余提现额度:60000.00</span>
    </div>

    <!-- 费用和税收区域 -->
    <div class="fee-tax">
      <div class="item">
        <span>费用</span>
        <span class="free">限时免费</span>
      </div>
      <div class="item">
        <span>税收</span>
        <span class="free">限时免费</span>
      </div>
    </div>

    <!-- 到账金额区域 -->
    <div class="arrival-amount">
      <span>到账金额</span>
      <span class="amount">0.00</span>
    </div>

    <!-- 提现说明区域 -->
    <div class="withdraw-instructions">
      <div class="instructions-title">提现说明</div>
      <div
        class="instructions-content"
      >为了保障平台运营秩序与资金安全，若您未完成全部任务，仅可申请提现已返还的佣金部分，未完成任务的本金暂不可提现。感谢您的理解与支持！</div>
    </div>

    <!-- 立即提现按钮 -->
    <button class="withdraw-btn">立即提现</button>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref } from "vue";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const withdrawAmount = ref("");
</script>

<style scoped>
/* 容器样式 */
.withdraw-container {
  background-color: #e6f2ff;
  height: 100vh;
  box-sizing: border-box;
}

/* 头部样式 */
.header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  margin-bottom: 15px;
}
.back {
  cursor: pointer;
  margin-right: 10px;
}
.title {
  font-weight: bold;
  font-size: 18px;
}

/* 选择提现方式样式 */
.withdraw-method {
  margin-bottom: 15px;
  margin-top: 50px;
  padding: 10px;
}
.method-title {
  margin-bottom: 8px;
  font-size: 16px;
}
.bank-card {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
}
.unionpay-icon {
  width: 30px;
  height: 20px;
  margin-right: 10px;
}
.bank-info {
  display: flex;
  flex-direction: column;
}
.bank-name {
  font-weight: bold;
  margin-bottom: 3px;
}
.card-number {
  color: #666;
  font-size: 14px;
}

/* 可用提现金额样式 */
.available-amount {
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 15px;
  margin: 10px;
}
.amount {
  color: blue;
}

/* 输入金额样式 */
.input-amount {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 15px;
  margin: 10px;
}
input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
}
.all-btn {
  background-color: #fff;
  border: 1px solid blue;
  color: blue;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

/* 提现次数和额度样式 */
.withdraw-info {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
  margin: 10px;
}

/* 费用和税收样式 */
.fee-tax {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
  margin: 10px;
}
.item {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
}
.free {
  color: blue;
}

/* 到账金额样式 */
.arrival-amount {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 15px;
  margin: 10px;
}

/* 提现说明样式 */
.withdraw-instructions {
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  margin: 10px;
  margin-bottom: 20px;
}
.instructions-title {
  font-weight: bold;
  margin-bottom: 8px;
}
.instructions-content {
  color: #999;
  font-size: 14px;
  line-height: 1.5;
}

/* 立即提现按钮样式 */
.withdraw-btn {
  background-color: blue;
  color: #fff;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  width: 90%;
  margin: 5%;
}
</style>