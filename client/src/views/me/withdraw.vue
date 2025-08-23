<template>
  <div class="withdraw-cancel-page">
    <!-- 顶部标题栏 -->

    <HeaderBar :title="t('提款')" />

    <!-- 提款金额卡片 -->
    <div class="amount-card">
      <div class="card-title">{{ $t("提款金额") }}</div>
      <div class="bank-info">
        <span
          :class="[ activeTab === 2 ? 'bank-tag' : 'currency']"
          @click="activeTab = 2"
        >{{ $t("收到银行账户") }}</span>
        <span :class="[ activeTab === 1 ? 'bank-tag' : 'currency']" @click="activeTab = 1">USDT</span>
      </div>
      <div class="input-group">
        <span class="dollar-sign">$</span>
        <input v-model="amount" class="amount-input" :placeholder="t('请输入取款金额')" type="number" />
      </div>
      <div class="balance-info">
        <span>{{ $t("我的余额") }}: ${{ accountBalance }}</span>
        <span class="withdraw-all" @click="withdrawAll">{{ $t("全部提款") }}</span>
      </div>
    </div>

    <!-- 银行信息表单 -->
    <div v-if="activeTab === 2" class="bank-form-card">
      <div class="form-item">
        <label for="bankName" class="form-label">{{ $t('银行名称') }}</label>
        <input v-model="bankName" :placeholder="$t('银行名称')" class="form-input" />
      </div>
      <div class="form-item">
        <label for="bankName" class="form-label">{{ $t('姓名') }}</label>
        <input v-model="realName" :placeholder="$t('姓名')" class="form-input" />
      </div>
      <div class="form-item">
        <label for="bankName" class="form-label">{{ $t('银行卡号') }}</label>
        <input v-model="bankCard" :placeholder="$t('银行卡号')" class="form-input" />
      </div>
      <div class="form-item password-item">
        <label for="bankName" class="form-label">{{ $t('资金密码') }}</label>
        <input v-model="fundsPwd" class="form-input" type="password" :placeholder="$t('请输入资金密码')" />
      </div>
    </div>
    <div v-if="activeTab === 1" class="bank-form-card">
      <div class="form-item">
        <label for="address" class="form-label">{{ $t('地址') }}</label>
        <input v-model="address" :placeholder="$t('地址')" class="form-input" />
      </div>
      <div class="form-item password-item">
        <label for="bankName" class="form-label">{{ $t('资金密码') }}</label>
        <input v-model="fundsPwd" class="form-input" type="password" :placeholder="$t('请输入资金密码')" />
      </div>
    </div>
    <div class="notice">*{{ $t("请仔细检查付款信息") }}</div>
    <button class="cancel-btn" @click="handleCancel(activeTab === 2 ? 2 : 1)">{{ $t("提款") }}</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { getUserInfo, withdraw } from "../../api";
import { showAlert } from "../../utils/notify";
import { useInfoStore } from "../../store/useInfoStore";

const router = useRouter();
const { t } = useI18n();
// tab 状态
const activeTab = ref(2);
const infoStore = useInfoStore();

// 保持原有响应式数据
const withdrawAmount = ref("");
const bankName = ref(""); // 模拟已有数据
const realName = ref(""); // 模拟已有数据
const bankCard = ref(""); // 模拟已有数据
const fundsPwd = ref("");
const address = ref("");
const accountBalance = ref("");
const amount = ref("");
const bankAccountNumber = ref("");

// 保持原有功能逻辑
function toBack() {
  router.go(-1);
}

function withdrawAll() {
  amount.value = accountBalance.value; // 模拟全部提款
}

function handleCancel(e) {
  console.log(e);
  if (e === 2) {
    withdraw({
      userId: infoStore.getUserinfo.uid,
      amount: amount.value,
      withdrawalMethod: activeTab.value,
      fundPassword: fundsPwd.value
    }).then(res => {
      if (res.code == 200) {
        showAlert(t(res.msg), 2000);
      } else {
        showAlert(t(res.msg), 2000);
      }
    });
  } else {
    withdraw({
      userId: infoStore.getUserinfo.uid,
      amount: amount.value,
      withdrawalMethod: activeTab.value,
      fundPassword: fundsPwd.value
    }).then(res => {
      if (res.code == 200) {
        showAlert(t(res.msg), 2000);
      } else {
        showAlert(t(res.msg), 2000);
      }
    });
  }
  // console.log("提交数据：", {
  //   type: activeTab.value,
  //   address: address.value,
  //   bankName: bankName.value,
  //   bankAccountName: realName.value,
  //   bankAccountNumber: bankAccountNumber.value,
  //   fundsPwd: fundsPwd.value
  // });
  // 这里可保留原有接口调用逻辑
}

onMounted(() => {
  getUserInfo().then(res => {
    bankName.value = res.data.bankName;
    realName.value = res.data.bankAccountName;
    bankCard.value = formatBankCard(res.data.bankAccountNumber);
    bankAccountNumber.value = res.data.bankAccountNumber;
    address.value = res.data.withdrawalAddress;
    accountBalance.value = res.data.accountBalance;
    if (
      !res.data.bankName ||
      !res.data.bankAccountName ||
      !res.data.bankAccountNumber
    ) {
      showAlert(t("地址未填写,请填写完整"), 3000);
    }
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
/* 页面整体布局 */
.withdraw-cancel-page {
  width: 100%;
  min-height: 100vh;
  background-color: #121212;
  color: #fff;
  font-family: Arial, sans-serif;
  box-sizing: border-box;
}

/* 顶部标题栏 */
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.back-icon {
  font-size: 20px;
  color: #fff;
  margin-right: 8px;
  cursor: pointer;
}
.title {
  font-size: 18px;
  font-weight: bold;
}

/* 提款金额卡片 */
.amount-card {
  background-color: #1f1f1f;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  padding-top: 50px;
}
.card-title {
  font-size: 16px;
  margin-bottom: 12px;
  font-weight: 500;
}
.bank-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.bank-tag {
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  margin-right: 8px;
  border: 1px solid #fbbd08;
  color: #fbbd08;
}
.currency {
  font-size: 12px;
  color: #fbbd08;
  margin-right: 5px;
}
.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.dollar-sign {
  font-size: 20px;
  margin-right: 8px;
  color: #fff;
}
.amount-input {
  flex: 1;
  height: 36px;
  padding: 0 8px;
  background-color: #222;
  border: 1px solid #444;
  color: #fff;
  border-radius: 4px;
}
.balance-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #fff;
}
.withdraw-all {
  cursor: pointer;
}

/* 银行信息表单卡片 */
.bank-form-card {
  background-color: #1f1f1f;
  border-radius: 8px;
  padding: 16px;
}
.form-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}
.form-label {
  font-size: 14px;
  margin-bottom: 4px;
  color: #999;
}
.form-input {
  height: 36px;
  padding: 0 8px;
  background-color: #222;
  border: 1px solid #444;
  color: #fff;
  border-radius: 4px;
}
.form-input:disabled {
  background-color: #2c2c2c;
  cursor: not-allowed;
}
.card-mask {
  color: #fff;
  margin-left: 8px;
}
.password-item {
  position: relative;
}
.password-placeholder {
  position: absolute;
  right: 12px;
  top: 38px;
  font-size: 14px;
  color: #999;
  pointer-events: none;
}
.notice {
  color: red;
  font-size: 12px;
  margin: 8px;
  background-color: #1d1d1f;
  padding: 10px;
  border-radius: 10px;
}
.cancel-btn {
  width: 90%;
  height: 40px;
  background-color: #3b4d63;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-left: 5%;
}
.cancel-btn:hover {
  background-color: #4c6380;
}

@media screen and (min-width: 768px) {
  .withdraw-cancel-page {
    width: 450px;
    min-height: 100vh;
    margin: 0 auto;
    background-color: #121212;
    color: #fff;
    font-family: Arial, sans-serif;
    box-sizing: border-box;
  }

  /* 顶部标题栏 */
  .page-header {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
  }
  .back-icon {
    font-size: 20px;
    color: #fff;
    margin-right: 8px;
    cursor: pointer;
  }
  .title {
    font-size: 18px;
    font-weight: bold;
  }

  /* 提款金额卡片 */
  .amount-card {
    background-color: #1f1f1f;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
    padding-top: 60px;
  }
  .card-title {
    font-size: 16px;
    margin-bottom: 12px;
    font-weight: 500;
  }
  .bank-info {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    padding: 16px;
  }
  .bank-tag {
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 12px;
    margin-right: 8px;
    border: 1px solid #fbbd08;
    color: #fbbd08;
  }
  .currency {
    font-size: 12px;
    color: #fbbd08;
    margin-right: 5px;
  }
  .input-group {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }
  .dollar-sign {
    font-size: 20px;
    margin-right: 8px;
    color: #fff;
  }
  .amount-input {
    flex: 1;
    height: 36px;
    padding: 0 8px;
    background-color: #222;
    border: 1px solid #444;
    color: #fff;
    border-radius: 4px;
  }
  .balance-info {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: #fff;
  }
  .withdraw-all {
    cursor: pointer;
  }

  /* 银行信息表单卡片 */
  .bank-form-card {
    background-color: #1f1f1f;
    border-radius: 8px;
    padding: 16px;
  }
  .form-item {
    display: flex;
    flex-direction: column;
    margin-bottom: 16px;
  }
  .form-label {
    font-size: 14px;
    margin-bottom: 4px;
    color: #999;
  }
  .form-input {
    height: 36px;
    padding: 0 8px;
    background-color: #222;
    border: 1px solid #444;
    color: #fff;
    border-radius: 4px;
  }
  .form-input:disabled {
    background-color: #2c2c2c;
    cursor: not-allowed;
  }
  .card-mask {
    color: #fff;
    margin-left: 8px;
  }
  .password-item {
    position: relative;
  }
  .password-placeholder {
    position: absolute;
    right: 12px;
    top: 38px;
    font-size: 14px;
    color: #999;
    pointer-events: none;
  }
  .notice {
    color: red;
    font-size: 12px;
    margin: 8px;
    background-color: #1d1d1f;
    padding: 10px;
    border-radius: 10px;
  }
  .cancel-btn {
    width: 90%;
    height: 40px;
    background-color: #3b4d63;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    margin-left: 5%;
  }
  .cancel-btn:hover {
    background-color: #4c6380;
  }
}
</style>