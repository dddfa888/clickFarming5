<template>
  <div class="member-center-container">
    <!-- 头部区域 -->

    <HeaderBar :title="t('会员中心')" backcolor="#e6f2ff" />
    <div class="header">
      <span class="back">&lt;</span>
      <span class="title"></span>
    </div>

    <!-- 会员卡片列表 -->
    <div class="member-cards">
      <!-- 白银会员卡片 -->
      <div class="member-card silver">
        <div class="card-top">
          <div class="member-info">
            <div class="member-level">白银会员</div>
            <div class="commission-rate">0.20%佣金比例</div>
          </div>
          <div class="price">¥0元</div>
        </div>
        <div class="card-icon">
          <img src="../../assets/silver-icon.png" alt="白银会员图标" />
        </div>
        <div class="card-bottom">
          <div class="item">
            <div class="label">提现次数</div>
            <div class="value">3次/天</div>
          </div>
          <div class="item">
            <div class="label">提现额度</div>
            <div class="value">60000.00</div>
          </div>
          <div class="item">
            <div class="label">拼团次数</div>
            <div class="value">30</div>
          </div>
        </div>
      </div>

      <!-- 黄金会员卡片 -->
      <div class="member-card gold">
        <div class="card-top">
          <div class="member-info">
            <div class="member-level">黄金会员</div>
            <div class="commission-rate">0.20%佣金比例</div>
          </div>
          <div class="price">¥10000元</div>
        </div>
        <div class="card-icon">
          <img src="../../assets/gold-icon.png" alt="黄金会员图标" />
        </div>
        <div class="card-bottom">
          <div class="item">
            <div class="label">提现次数</div>
            <div class="value">3次/天</div>
          </div>
          <div class="item">
            <div class="label">提现额度</div>
            <div class="value">150000.00</div>
          </div>
          <div class="item">
            <div class="label">拼团次数</div>
            <div class="value">70</div>
          </div>
        </div>
      </div>

      <!-- 铂金会员卡片 -->
      <div class="member-card platinum">
        <div class="card-top">
          <div class="member-info">
            <div class="member-level">铂金会员</div>
            <div class="commission-rate">0.30%佣金比例</div>
          </div>
          <div class="price">¥68888元</div>
        </div>
        <div class="card-icon">
          <img src="../../assets/platinum-icon.png" alt="铂金会员图标" />
        </div>
        <div class="card-bottom">
          <div class="item">
            <div class="label">提现次数</div>
            <div class="value">5次/天</div>
          </div>
          <div class="item">
            <div class="label">提现额度</div>
            <div class="value">500000.00</div>
          </div>
          <div class="item">
            <div class="label">拼团次数</div>
            <div class="value">120</div>
          </div>
        </div>
      </div>

      <!-- 铂金会员卡片 -->
      <div class="member-card platinum">
        <div class="card-top">
          <div class="member-info">
            <div class="member-level">铂金会员</div>
            <div class="commission-rate">0.30%佣金比例</div>
          </div>
          <div class="price">¥68888元</div>
        </div>
        <div class="card-icon">
          <img src="../../assets/platinum-icon.png" alt="铂金会员图标" />
        </div>
        <div class="card-bottom">
          <div class="item">
            <div class="label">提现次数</div>
            <div class="value">5次/天</div>
          </div>
          <div class="item">
            <div class="label">提现额度</div>
            <div class="value">500000.00</div>
          </div>
          <div class="item">
            <div class="label">拼团次数</div>
            <div class="value">120</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const title = ref("会员中心");
const { t } = useI18n();
</script>

<style scoped>
/* 容器样式 */
.member-center-container {
  background-color: #e6f2ff;
  min-height: 100vh;
  padding: 10px;
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

/* 会员卡片样式 */
.member-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px;
}
.member-card {
  border-radius: 8px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  width: 90%;
  height: auto;
}

.silver {
  background-image: url(../../assets/image/level-1.png);
  background-size: cover;
}

.gold {
  background-image: url(../../assets/image/level-2.png);
  background-size: cover;
}
.platinum {
  background-image: url(../../assets/image/level-3.png);
  background-size: cover;
}
.card-top {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 15px;
}
.member-info {
  display: flex;
  flex-direction: column;
}
.member-level {
  font-weight: bold;
  margin-bottom: 5px;
}
.commission-rate {
  color: #666;
}
.price {
  font-weight: bold;
}
.card-icon img {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
}
.card-bottom {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.label {
  color: #999;
  font-size: 14px;
  margin-bottom: 3px;
}
.value {
  font-weight: 500;
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