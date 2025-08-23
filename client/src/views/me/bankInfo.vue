<template>
  <div class="form-container">
    <!-- Header Section -->
    <HeaderBar :title="$t('绑定银行账户')" />
    <!-- Form Section -->

    <p class="form-title" style="color: #fff;">{{ $t("您的银行卡资讯") }}</p>
    <form @submit.prevent="submitForm">
      <!-- Bank Name -->
      <div class="form-field">
        <label for="bankName">{{ $t("银行名称") }}</label>
        <input type="text" v-model="bankName" id="bankName" :placeholder="$t('请输入银行名称')" />
        <!-- <select :disabled="showinput" v-model="bankName" id="bankName">
          <option value disabled selected>{{ $t("请选择银行") }}</option>
          <option v-for="(item,index) in bankList" :value="item" :key="index">{{ item }}</option>
        </select>-->
      </div>

      <div class="form-field">
        <label for="bankAccountname">{{ $t("银行账户名") }}</label>
        <input
          v-model="bankAccountName"
          id="bankAccountname"
          type="text"
          :placeholder=" $t('请输入银行账户名')"
        />
      </div>

      <!-- Bank Account -->
      <div class="form-field">
        <label for="bankAccountNumber">{{ $t("银行账户") }}</label>
        <input
          v-model="bankAccountNumber"
          id="bankAccountNumber"
          type="text"
          :placeholder=" $t('请输入银行账户')"
        />
      </div>

      <div class="form-field">
        <label for="fundPassword">{{ $t("提款密码") }}</label>
        <input
          v-model="fundPassword"
          id="fundPassword"
          type="password"
          :placeholder=" $t('请输入提款密码')"
        />
      </div>

      <!-- Submit Button -->
      <button type="submit" class="submit-button">{{ $t("确认绑定卡") }}</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { updateUserInfo, getUserInfo } from "../../api/index";
import { showAlert } from "../../utils/notify";
import router from "../../router";
const fundPassword = ref("");
const bankName = ref("");
const bankAccountName = ref("");
const bankAccountNumber = ref("");
const isWithdraw = ref(true);
const showinput = ref(false);

const { t } = useI18n();
const bankList = [
  "UOB",
  "PVCOMBANK",
  "VIET BANK",
  "BANVIET BANK",
  "THE SHANGHAI COMMERCIAL & SAVINGS BANK LTD",
  "MONETA MONEY BANK",
  "KIEN LONG BANK",
  "NGÂN HÀNG YUANTA",
  "KIOPBANK",
  "BANK OF TAIWAN",
  "郵政",
  "FIRST BANK",
  "ABBANK",
  "EXIMBANK",
  "SCB NGÂN HÀNG SÀI GÒN",
  "HIGOBANK",
  "SHB BANK",
  "FIRST BANK",
  "THẺ NGÂN HÀNG SEABANK",
  "DONGA BANK",
  "TAIWAN BUSINESS BANK",
  "CHANG HWA BANK",
  "TP BANK",
  "NCB BANK",
  "VIET CAPITAL BANK",
  "SHINHAN BANK",
  "PG BANK",
  "MSB BANK",
  "HDBANK",
  "NAMABANK",
  "POSB",
  "OCBC",
  "CITIBANK",
  "MAYBANK",
  "PUBLIC BANK",
  "CIMB BANK",
  "HONG LEONG BANK",
  "HSBC",
  "BANK RAKYT",
  "WOORI BANK",
  "UNION BANK",
  "DBS",
  "LIENVIETPOSTBANK",
  "REMITTANCE CARD",
  "OCB BANK",
  "MARITIME BANK",
  "BANK CENTRAL ASIA",
  "株式会社みずほ銀行",
  "株式会社三菱東京UFJ銀行",
  "株式会社三井住友銀行",
  "株式会社りそな銀行",
  "株式会社埼玉りそな銀行",
  "日本銀行",
  "みなと銀行",
  "北おおさか信用金庫",
  "大地みらい信用金庫",
  "株式会社ジャパンネット銀行",
  "ソニー銀行株式会社",
  "ABN AMRO BANK",
  "BANK CITY OSAKA",
  "SHINKIN OSAKA",
  "SBI NET BANK",
  "ITS SEVEN BANK",
  "YUCHO BANK",
  "南洋商業銀行",
  "FUKUOKA BANK",
  "USDT",
  "SAITAMAKEN SHINKIN BANK",
  "AGRIBANK",
  "MB BANK",
  "PG BANK",
  "HANABANK",
  "TECHCOMBANK",
  "BSN",
  "SIAM COMMERCIAL BANK PCL.",
  "SACOMBANK",
  "TPBANK",
  "南都銀行",
  "広島銀行",
  "ゆうちょ银行",
  "VIB BANK",
  "GMO AOZORA NET BANK",
  "BIDV",
  "HOKURIKU BANK",
  "VIETTIN BANK",
  "TOMATO BANK",
  "SEVENBANK",
  "HIROSHIMA BANK",
  "VP BANK",
  "ACB BANK",
  "VIETCOMBANK",
  "AND",
  "WESTPAC",
  "NAB",
  "SUNCORP",
  "NAZ",
  "AND",
  "WESTPAC",
  "NAB",
  "SUNCORP",
  "NAZ",
  "ANZ",
  "CBA",
  "INDOVINA BANK",
  "VIETA BANK",
  "CAKE BY VPBANK",
  "BAC A BANK",
  "LIOBANK (OCB)",
  "COOPBANK",
  "LP Bank"
];

const closeModal = () => {
  // Close modal or navigate back
  console.log("Modal Closed");
};
// 获取用户信息
function getuserInfo() {
  getUserInfo().then(res => {
    bankAccountName.value = res.data.bankAccountName || "";
    bankAccountNumber.value = res.data.bankAccountNumber || "";
    bankName.value = res.data.bankName || "";
    if (
      !res.data.bankAccountName &&
      !res.data.bankAccountNumber &&
      !res.data.bankName
    ) {
      isWithdraw.value = true;
      showinput.value = false;
    } else {
      isWithdraw.value = false;
      showinput.value = true;
    }
  });
}

const submitForm = () => {
  updateUserInfo({
    bankAccountName: bankAccountName.value,
    bankAccountNumber: bankAccountNumber.value,
    bankName: bankName.value,
    fundPassword: fundPassword.value
  }).then(res => {
    console.log(res);
    if (res.code === 200) {
      showAlert(t(res.msg), 2000);
      router.push("/withdraw");
    } else {
      showAlert(t(res.msg), 2000);
    }
    getuserInfo();
  });
};
onMounted(() => {
  getuserInfo();
});
</script>

<style scoped>
.form-container {
  font-family: sans-serif;
  background-color: #121212;
  width: 100vw;
  height: 100vh;
}

.form-title {
  padding: 5px;
  padding-top: 50px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-field {
  display: flex;
  align-items: center;
  margin: 0 15px 0 15px;
}

.form-field label {
  font-size: 14px;
  color: #fff;
  padding: 5px;
}

.form-field input {
  padding: 10px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  background-color: transparent;
  color: #bbb;
  display: flex;
  align-items: center;
}

.submit-button {
  padding: 14px;
  font-size: 18px;
  background-color: #2f3848;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-transform: uppercase;
  width: 90%;
  margin-left: 5%;
}

.bankName label {
  color: #ccc;
}

@media screen and (min-width: 768px) {
  .form-container {
    font-family: sans-serif;
    background-color: #121212;
    width: 450px;
    height: 100vh;
    margin: 0 auto;
  }

  .form-title {
    padding: 5px;
    padding-top: 50px;
  }

  form {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .form-field {
    display: flex;
    align-items: center;
    margin: 0 15px 0 15px;
  }

  .form-field label {
    font-size: 14px;
    color: #fff;
    padding: 5px;
  }

  .form-field input {
    padding: 10px;
    font-size: 16px;
    border-radius: 4px;
    border: none;
    background-color: transparent;
    clear: #bbb;
    display: flex;
    align-items: center;
  }

  .submit-button {
    padding: 14px;
    font-size: 18px;
    background-color: #2f3848;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    text-transform: uppercase;
    width: 90%;
    margin-left: 5%;
  }

  .bankName label {
    color: #ccc;
  }
}
</style>