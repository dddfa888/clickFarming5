<template>
  <div class="bank-card-container">
    <!-- 头部区域 -->
    <HeaderBar :title="t('收款银行卡')" backcolor="#e6f2ff" />

    <!-- 银行卡信息区域 -->
    <div class="bank-card-info">
      <img src="../../assets/unionpay.png" alt="银联标识" class="unionpay-icon" />
      <div class="bank-info">
        <div class="bank-name">浙商银行</div>
        <div class="card-number">8888 **** **** 8844</div>
      </div>
    </div>

    <!-- 绑卡说明区域 -->
    <div class="binding-instructions">
      <div class="instructions-title">绑卡说明</div>
      <div class="instructions-content">用户仅支持添加一张银行卡，如需修改请联系在线客服</div>
    </div>

    <!-- 添加银行卡按钮 -->
    <button class="add-bank-card">+ 添加银行卡</button>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
</script>

<style scoped>
/* 容器样式 */
.bank-card-container {
  background-color: #e6f2ff;
  height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  font-size: 18px;
  border-bottom: 1px solid #eee;
}
.back {
  cursor: pointer;
  margin-right: 10px;
}
.title {
  font-weight: bold;
}

/* 银行卡信息样式 */
.bank-card-info {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 15px;
  border-radius: 8px;
  margin: 60px 10px 20px;
}
.unionpay-icon {
  width: 40px;
  height: 30px;
  margin-right: 15px;
}
.bank-info {
  display: flex;
  flex-direction: column;
}
.bank-name {
  font-weight: bold;
  margin-bottom: 5px;
}
.card-number {
  color: #666;
}

/* 绑卡说明样式 */
.binding-instructions {
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.instructions-title {
  font-weight: bold;
  margin-bottom: 8px;
}
.instructions-content {
  color: #999;
  font-size: 14px;
}

/* 添加银行卡按钮样式 */
.add-bank-card {
  background-color: #fff;
  border: 1px solid #007bff;
  color: #007bff;
  padding: 10px;
  border-radius: 4px;
  margin: 10px;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 20px;
}

/* 底部导航样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
  border-top: 1px solid #eee;
}
.nav-item {
  text-align: center;
}
.active {
  color: blue;
}
.icon {
  font-size: 14px;
}
</style>