<template>
  <div class="recharge-page">
    <!-- 顶部导航 -->
    <div class="header">
      <span>
        <van-icon name="arrow-left" class="back" @click="goBack" />
      </span>
      <span style="margin-left: 70px;" class="tab" @click="goTo('/recharge')">{{ $t("充值") }}</span>
      <span class="tab" @click="goTo('/depositHistory')">{{ $t("充值记录") }}</span>
    </div>

    <!-- 内容区域 -->
    <div class="card">
      <p class="title">{{ $t("充值金额") }}</p>
      <div class="input-box">
        <span class="currency">￥</span>
        <input type="number" :placeholder="t('请输入充值金额')" v-model="amount" />
      </div>

      <!-- 金额选项 -->
      <div class="amount-grid">
        <div
          class="amount-item"
          v-for="(item, index) in amounts"
          :key="index"
          @click="amount = item"
        >{{ item }}</div>
      </div>

      <p class="tips">
        1. {{ $t("付款金额必须与订单金额相同，否则不会自动到帐") }}
        <br />
        2. {{$t("如果您没有收到充值到账，请确认您的首付款已解决其他问题")}}
      </p>
    </div>

    <!-- 底部按钮 -->
    <button class="submit-btn" @click="submit">{{ $t("充值") }}</button>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { recharge } from "../../api/index";
import { useInfoStore } from "../../store/useInfoStore";
import { showAlert } from "../../utils/notify";

const router = useRouter();
const route = useRoute();
const { t } = useI18n();
const infoStore = useInfoStore();

const amount = ref("");
const amounts = [
  110,
  320,
  708,
  1009,
  2001,
  3000,
  5000,
  10000,
  30000,
  50000,
  100000
];

const goTo = path => {
  router.push(path);
};

const goBack = () => {
  router.back();
};

const submit = () => {
  console.log(infoStore.getUserinfo.uid);
  recharge(amount.value, infoStore.getUserinfo.uid).then(res => {
    console.log(res);
    if (res.code === 200) {
      showAlert(t(res.msg), 2000);
    } else {
      showAlert(t(res.msg), 2000);
    }
  });
};
</script>

<style scoped>
.recharge-page {
  background: #000;
  min-height: 100vh;
  color: #fff;
  padding: 0 12px;
}

/* 顶部导航 */
.header {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  position: relative;
  color: #fff;
}

.back {
  font-size: 20px;
  cursor: pointer;
  position: absolute;
  left: 0;
}

.tabs {
  display: flex;
  justify-content: center;
  width: 100%;
  gap: 40px;
}

.tab {
  font-size: 16px;
  cursor: pointer;
  color: #aaa;
}

.tab.active {
  color: #fff;
  font-weight: bold;
  border-bottom: 2px solid #fff;
  padding-bottom: 4px;
}

/* 卡片 */
.card {
  background: #1c1c1c;
  border-radius: 8px;
  padding: 16px;
  margin-top: 12px;
}

.title {
  font-size: 14px;
  margin-bottom: 12px;
}

.input-box {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #555;
  margin-bottom: 16px;
  padding: 4px 0;
}

.currency {
  margin-right: 8px;
}

.input-box input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #fff;
}

.amount-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.amount-item {
  background: #111;
  border: 1px solid #555;
  border-radius: 6px;
  padding: 10px 0;
  text-align: center;
  cursor: pointer;
}

.amount-item:hover {
  border-color: #d4af37;
}

.tips {
  font-size: 12px;
  color: #ccc;
  line-height: 1.6;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 12px 0;
  margin-top: 20px;
  border-radius: 24px;
  border: none;
  background: linear-gradient(to bottom left, #fbf9e7, #d2b87d, #fbf9e7);
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}

@media screen and (min-width: 768px) {
  .recharge-page {
    background: #000;
    width: 450px;
    margin: 0 auto;
    height: 100vh;
    color: #fff;
    padding: 0 12px;
  }

  /* 顶部导航 */
  .header {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    position: relative;
    color: #fff;
  }

  .back {
    font-size: 20px;
    cursor: pointer;
    position: absolute;
    left: 0;
  }

  .tabs {
    display: flex;
    justify-content: center;
    width: 100%;
    gap: 40px;
  }

  .tab {
    font-size: 16px;
    cursor: pointer;
    color: #aaa;
  }

  .tab.active {
    color: #fff;
    font-weight: bold;
    border-bottom: 2px solid #fff;
    padding-bottom: 4px;
  }

  /* 卡片 */
  .card {
    background: #1c1c1c;
    border-radius: 8px;
    padding: 16px;
    margin-top: 12px;
  }

  .title {
    font-size: 14px;
    margin-bottom: 12px;
  }

  .input-box {
    display: flex;
    align-items: center;
    border-bottom: 1px solid #555;
    margin-bottom: 16px;
    padding: 4px 0;
  }

  .currency {
    margin-right: 8px;
  }

  .input-box input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: #fff;
  }

  .amount-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    margin-bottom: 16px;
  }

  .amount-item {
    background: #111;
    border: 1px solid #555;
    border-radius: 6px;
    padding: 10px 0;
    text-align: center;
    cursor: pointer;
  }

  .amount-item:hover {
    border-color: #d4af37;
  }

  .tips {
    font-size: 12px;
    color: #ccc;
    line-height: 1.6;
  }

  /* 提交按钮 */
  .submit-btn {
    width: 100%;
    padding: 12px 0;
    margin-top: 20px;
    border-radius: 24px;
    border: none;
    background: linear-gradient(to bottom left, #fbf9e7, #d2b87d, #fbf9e7);
    color: #fff;
    font-size: 16px;
    cursor: pointer;
  }
}
</style>
