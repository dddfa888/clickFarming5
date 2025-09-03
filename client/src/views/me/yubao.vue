<template>
  <div class="yeb-container">
    <!-- 头部区域 -->
    <div style="background-color:#ece9ee;" class="header-bar">
      <van-icon name="arrow-left" class="back-icon" @click="toback" />
      <span class="title">{{ t('余额宝') }}</span>
      <span class="right-placeholder" @click="toRecord">{{ t('余额宝记录') }}</span>
    </div>
    <!-- 资产信息区域 -->
    <div class="asset">
      <div class="total-assets">
        <span class="label">总资产</span>
        <span class="record" @click="eyeClick">
          <img v-if="!isEyes" src="../../assets/image/eye.svg" alt />
          <img v-else src="../../assets/image/openeys.svg" alt />
        </span>
      </div>
      <div class="amount">
        <span v-if="!isEyes">0.00</span>
        <span v-else>****</span>
      </div>
      <div class="yesterday-income">
        昨日收益
        <span v-if="!isEyes">0.00</span>
        <span v-else>****</span>
        元
      </div>
      <div class="other-info">
        <div class="item">
          <span class="value">
            <span v-if="!isEyes">0.00</span>
            <span v-else>****</span>
          </span>
          <span class="desc">累计收益(元)</span>
        </div>
        <div class="item">
          <span class="value">
            <span v-if="!isEyes">5.00</span>
            <span v-else>****</span>
          </span>
          <span class="desc">年化率 (%)</span>
        </div>
      </div>
      <div class="buttons">
        <button class="transfer-in" @click="showModal = true">余额转入</button>
        <button class="transfer-out" @click="showModal = true">余额转出</button>
      </div>
    </div>
    <TransferModal
      :visible="showModal"
      :available-balance="98.62"
      @close="showModal = false"
      @confirm="handleConfirm"
    />
  </div>
</template>

<script setup>
import { ref } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import TransferModal from "../../components/TransferModal.vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

const { t } = useI18n();
const router = useRouter();
const showModal = ref(false);
const isEyes = ref(false);

const eyeClick = () => {
  isEyes.value = !isEyes.value;
};

const toRecord = () => {
  router.push("/baorecords");
};

const handleConfirm = amount => {
  console.log("转入金额:", amount);
  // 处理转入逻辑...
  showModal.value = false;
};

const toback = () => {
  router.back();
};
</script>

<style scoped>
/* 容器样式 */
.yeb-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  background-color: #ece9ee;
  height: 100vh;
  box-sizing: border-box;
}

/* 头部样式 */
.header-bar {
  display: flex;
  align-items: center;
  height: 50px;
  width: 100%;
  background-color: #1d1d1f;
  color: #000;
  font-size: 16px;
  position: fixed;
  top: 0;
  z-index: 999;
}

.back-icon {
  position: absolute;
  left: 10px;
  font-size: 20px;
}

.title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.right-placeholder {
  transform: translateX(350%);
}
.record {
  margin-left: 10px;
}
.record img {
  width: 20px;
  height: 20px;
}

/* 资产区域样式 */
.asset {
  background: linear-gradient(to right, #f0e68c, #fffacd);
  border-radius: 8px;
  padding: 20px;
  margin: 50px 10px 0;
}
.total-assets {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.label {
  font-weight: bold;
}
.amount {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}
.yesterday-income {
  background-color: rgba(255, 255, 255, 0.5);
  display: inline-block;
  padding: 5px 10px;
  border-radius: 10px;
  margin-bottom: 20px;
}
.other-info {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}
.item {
  text-align: center;
}
.value {
  font-size: 18px;
  font-weight: bold;
  display: block;
}
.desc {
  font-size: 14px;
}
.buttons {
  display: flex;
  gap: 10px;
}
.transfer-in,
.transfer-out {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 16px;
}
.transfer-in {
  background-color: #000;
  color: #fff;
}
.transfer-out {
  background-color: transparent;
  border: 1px solid #000;
}
</style>