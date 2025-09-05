<template>
  <div class="bank-card-container">
    <!-- 头部区域 -->
    <HeaderBar title="收款银行卡" backcolor="#e6f2ff" />

    <!-- 银行卡信息区域 -->
    <div style="margin-top: 50px" v-if="userInfo.bankAccountNumber">
      <div class="bank-card-info">
        <div class="bank-info">
          <div class="bank-name">{{ userInfo.bankName }}</div>
          <div class="card-number">{{ formatBankCard(userInfo.bankAccountNumber) }}</div>
        </div>
      </div>
    </div>

    <!-- 绑卡说明区域 -->
    <div class="binding-instructions">
      <div class="instructions-title">绑卡说明</div>
      <div class="instructions-content">用户仅支持添加一张银行卡，如需修改请联系在线客服</div>
    </div>

    <!-- 添加银行卡按钮 -->
    <button class="add-bank-card" @click="router.push('/binding')">+ 添加银行卡</button>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { getUserInfo } from "../../api/index";

const router = useRouter();
const userInfo = ref({});

const { t } = useI18n();
onMounted(() => {
  getUserInfo().then(res => {
    console.log(res.data);
    userInfo.value = res.data;
  });
});

function formatBankCard(cardNo) {
  if (!cardNo) return "";
  const clean = cardNo.replace(/\s+/g, "");
  if (clean.length <= 6) return clean;
  const start = clean.slice(0, 4);
  const end = clean.slice(-4);
  return `${start} **** **** ${end}`;
}
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
  margin: 10px 20px;
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
  margin-top: 40px;
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

@media screen and (min-width: 768px) {
  /* 容器样式 */
  .bank-card-container {
    background-color: #e6f2ff;
    height: 100vh;
    max-width: 450px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    margin: 0 auto;
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
    margin: 10px 20px;
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
}
</style>