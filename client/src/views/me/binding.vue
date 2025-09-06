<template>
  <div class="withdraw-page">
    <HeaderBar title="提现" backcolor="#F7FAFD" />
    <!-- 表单容器 -->
    <div class="form-container">
      <div class="input-box" style="margin-top: 60px;" @click="showBankModal = true">
        <input type="text" v-model="form.bankName" placeholder="卡类型  请选择卡类型" readonly />
      </div>
      <div class="input-box">
        <input type="text" v-model="form.bankAccountNumber" placeholder="账号  输入银行卡卡号" />
      </div>
      <div class="input-box">
        <input type="text" v-model="form.bankAccountName" placeholder="姓名  请输入姓名" />
      </div>
      <button class="submit-btn" @click="submitForm">完成添加</button>
    </div>

    <!-- 银行选择弹框 -->
    <div v-if="showBankModal" class="modal-mask">
      <div class="modal-container">
        <!-- 顶部标题 -->
        <div class="modal-header">
          <span class="title">添加银行卡</span>
          <span class="close-btn" @click="showBankModal = false">✕</span>
        </div>

        <!-- 搜索框 -->
        <div class="search-box">
          <input type="text" v-model="searchText" placeholder="请输入银行名称" />
        </div>

        <!-- 银行列表 -->
        <div class="bank-list">
          <div
            v-for="(bank, index) in filteredBanks"
            :key="index"
            class="bank-item"
            @click="selectBank(bank)"
          >{{ bank }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { updateBankInfo } from "../../api/index";
import { showAlert } from "../../utils/notify";

const form = ref({
  bankName: "",
  bankAccountNumber: "",
  bankAccountName: ""
});

const showBankModal = ref(false);
const searchText = ref("");

const banks = ref([
  "中国工商银行",
  "中国农业银行",
  "中国银行",
  "中国建设银行",
  "交通银行",
  "邮政储蓄银行",
  "招商银行",
  "中信银行",
  "光大银行",
  "华夏银行",
  "民生银行",
  "广发银行",
  "兴业银行",
  "浦发银行",
  "平安银行",
  "北京银行",
  "上海银行",
  "江苏银行",
  "南京银行",
  "宁波银行",
  "杭州银行",
  "广州银行",
  "重庆银行",
  "天津银行",
  "徽商银行",
  "北京农商银行",
  "上海农商银行",
  "广州农商银行",
  "深圳农商银行",
  "江苏省农村信用社",
  "浙江农商银行",
  "重庆农商银行",
  "农村信用合作社",
  "国家开发银行",
  "中国进出口银行",
  "中国农业发展银行"
]);

const submitForm = () => {
  updateBankInfo(form.value)
    .then(res => {
      showAlert(res.msg);
      if (res.code === 200) {
        form.value.bankName = "";
        form.value.bankAccountNumber = "";
        form.value.bankAccountName = "";
      }
    })
    .catch(err => {
      showAlert(res.msg);
    });
};

// 搜索过滤
const filteredBanks = computed(() => {
  if (!searchText.value) return banks.value;
  return banks.value.filter(b => b.includes(searchText.value));
});

// 选择银行并回填
const selectBank = bank => {
  form.value.bankName = bank;
  showBankModal.value = false;
};
</script>

<style scoped>
.withdraw-page {
  background: #f7fafd;
  min-height: 100vh;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

.input-box {
  margin-bottom: 12px;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  margin: 10px;
  border: 1px solid #eee;
}

.input-box input {
  border: none;
  outline: none;
  width: 100%;
  font-size: 14px;
}

.submit-btn {
  width: 90%;
  background: #5ab4ff;
  border: none;
  color: #fff;
  font-size: 16px;
  padding: 12px;
  margin-left: 5%;
  border-radius: 8px;
  margin-top: 20px;
}

/* 弹框样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  z-index: 999;
  align-items: flex-end;
}

.modal-container {
  background: #fff;
  width: 100%;
  max-height: 70%;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px;
  position: relative;
  font-weight: bold;
  border-bottom: 1px solid #eee;
}

.modal-header .close-btn {
  position: absolute;
  right: 16px;
  font-size: 18px;
  cursor: pointer;
}

.search-box {
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.search-box input {
  width: 100%;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
}

.bank-list {
  flex: 1;
  overflow-y: auto;
}

.bank-item {
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 15px;
  cursor: pointer;
}

.bank-item:hover {
  background: #f7f7f7;
}

@media screen and (min-width: 768px) {
  .withdraw-page {
    background: #f7fafd;
    min-height: 100vh;
    width: 450px;
    margin: 0 auto;
  }

  .input-box {
    margin-bottom: 12px;
    background: #fff;
    border-radius: 8px;
    padding: 12px;
    margin: 10px;
    border: 1px solid #eee;
  }

  .input-box input {
    border: none;
    outline: none;
    width: 100%;
    font-size: 14px;
  }

  .submit-btn {
    width: 90%;
    background: #5ab4ff;
    border: none;
    color: #fff;
    font-size: 16px;
    padding: 12px;
    margin-left: 5%;
    border-radius: 8px;
    margin-top: 20px;
  }

  /* 弹框样式 */
  .modal-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 999;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    width: 450px;
    margin: 0 auto;
    justify-content: center;
    align-items: flex-end;
  }

  .modal-container {
    background: #fff;
    width: 450px;
    max-height: 70%;
    border-top-left-radius: 16px;
    border-top-right-radius: 16px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .modal-header {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 12px;
    position: relative;
    font-weight: bold;
    border-bottom: 1px solid #eee;
  }

  .modal-header .close-btn {
    position: absolute;
    right: 16px;
    font-size: 18px;
    cursor: pointer;
  }

  .search-box {
    padding: 10px;
    border-bottom: 1px solid #eee;
  }

  .search-box input {
    width: 100%;
    padding: 8px;
    border-radius: 8px;
    border: 1px solid #ddd;
    font-size: 14px;
  }

  .bank-list {
    flex: 1;
    overflow-y: auto;
  }

  .bank-item {
    padding: 14px 16px;
    border-bottom: 1px solid #f0f0f0;
    font-size: 15px;
    cursor: pointer;
  }

  .bank-item:hover {
    background: #f7f7f7;
  }
}
</style>
