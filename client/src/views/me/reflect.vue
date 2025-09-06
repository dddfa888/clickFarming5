<template>
  <div class="withdraw-container">
    <!-- 头部区域 -->
    <HeaderBar title="提现" backcolor="#fff" />
    <!-- 选择提现方式区域 -->
    <div class="withdraw-method" v-if="userInfo.bankName">
      <div class="bank-card">
        <img src="../../assets/image/y-01.png" alt="银联标识" class="unionpay-icon" />
        <div class="bank-info">
          <div class="bank-name">{{userInfo.bankName}}</div>
          <div class="card-number">{{ formatBankCard(userInfo.bankAccountNumber) }}</div>
        </div>
      </div>
    </div>

    <div class="withdraw-method" v-else>
      <div class="method-title">选择提现方式</div>
      <div class="bank-card" @click="router.push('/backcard')">
        <img src="../../assets/image/tj.svg" alt="银联标识" class="unionpay-icon" />
        <div class="bank-info">
          <div class="bank-name">安心可靠 放心提款</div>
          <div class="card-number">添加银行卡</div>
        </div>
      </div>
    </div>

    <!-- 可用提现金额区域 -->
    <div class="available-amount">
      <span>可用提现金额:</span>
      <span class="amount">{{ userInfo.accountBalance }}</span>
    </div>

    <!-- 输入金额区域 -->
    <div class="input-amount">
      <input type="number" placeholder="输入金额" v-model="withdrawAmount" />
      <button class="all-btn" @click="withdrawAmount = userInfo.accountBalance">全部</button>
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

    <!-- 提现说明区域 -->
    <div class="withdraw-instructions">
      <div class="instructions-title">提现说明</div>
      <div
        class="instructions-content"
      >为了保障平台运营秩序与资金安全，若您未完成全部任务，仅可申请提现已返还的佣金部分，未完成任务的本金暂不可提现。感谢您的理解与支持！</div>
    </div>

    <!-- 立即提现按钮 -->
    <button class="withdraw-btn" @click="withdrawBtn">立即提现</button>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { getUserInfo, withdraw } from "../../api/index";
import { showAlert } from "../../utils/notify";
const { t } = useI18n();
const router = useRouter();
const userInfo = ref({});

const withdrawAmount = ref("");

onMounted(async () => {
  getUserInfo().then(res => {
    console.log(res.data);
    userInfo.value = res.data;
  });
});

const withdrawBtn = () => {
  if (
    !userInfo.value.bankAccountName ||
    !userInfo.value.bankAccountNumber ||
    !userInfo.value.bankName
  ) {
    showAlert("请先绑定银行卡", 2000);
    setTimeout(() => {
      router.push("/backcard");
    }, 2000);
  }
  withdraw({ userId: userInfo.value.uid, amount: withdrawAmount.value }).then(
    res => {
      console.log(res);
      if (res.code === 200) {
        showAlert(res.msg);
      } else {
        showAlert(res.msg);
      }
    }
  );
};

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

.bank-card img {
  width: 30px;
  height: 30px;
  margin-right: 10px;
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

@media screen and (min-width: 768px) {
  /* 容器样式 */
  .withdraw-container {
    background-color: #e6f2ff;
    height: 100vh;
    width: 450px;
    margin: 0 auto;
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

  .bank-card img {
    width: 30px;
    height: 30px;
    margin-right: 10px;
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
}
</style>