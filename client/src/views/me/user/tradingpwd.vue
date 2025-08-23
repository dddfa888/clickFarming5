<template>
  <div class="password-manage-page">
    <HeaderBar :title="t('交易密码管理')" />
    <div class="sub-title">{{ $t("请输入新密码") }}</div>
    <div style="margin-top: 50px;" class="form-item">
      <label class="form-label">{{ $t("旧密码") }}</label>
      <input v-model="oldPwd" class="form-input" type="password" :placeholder="t('请输入旧密码')" />
    </div>
    <div class="form-item">
      <label class="form-label">{{ $t("新密码") }}</label>
      <input v-model="newPwd" class="form-input" type="password" :placeholder="t('请输入新密码')" />
    </div>
    <div class="form-item">
      <label class="form-label">{{ $t("确认密码") }}</label>
      <input v-model="confirmPwd" class="form-input" type="password" :placeholder="t('请重新输入密码')" />
    </div>
    <button class="confirm-btn" @click="handleConfirm">{{ $t('确认修改') }}</button>
  </div>
</template>

<script setup>
import { ref } from "vue";
import HeaderBar from "../../../components/HeaderBar.vue";
import { updateMoneyPassword } from "../../../api/index";
import { useI18n } from "vue-i18n";
import { showAlert } from "../../../utils/notify";
const { t } = useI18n();

// 旧密码
const oldPwd = ref("");
// 新密码
const newPwd = ref("");
// 确认密码
const confirmPwd = ref("");

// 确认修改逻辑，可在此调用接口等
const handleConfirm = () => {
  updateMoneyPassword({
    oldPassword: oldPwd.value,
    newPassword: newPwd.value,
    confirmPassword: confirmPwd.value
  }).then(res => {
    console.log(res);
    if (res.code === 200) {
      showAlert(t("密码修改成功"), 2000);
    } else {
      showAlert(t(res.msg), 2000);
    }
  });
  console.log("旧密码：", oldPwd.value);
  console.log("新密码：", newPwd.value);
  console.log("确认密码：", confirmPwd.value);
  // 可扩展：调用接口提交密码修改
};
</script>

<style scoped>
.password-manage-page {
  width: 100%;
  height: 100vh;
  background-color: #121212;
  color: #fff;
  box-sizing: border-box;
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.sub-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
  padding: 10px 16px 5px 16px;
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

.next-circle {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}

.confirm-btn {
  width: 90%;
  height: 40px;
  background-color: #3b4d63;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 5%;
}

.confirm-btn:hover {
  background-color: #4c6380;
}

@media screen and (min-width: 768px) {
  .password-manage-page {
    width: 450px;
    height: 100vh;
    background-color: #121212;
    color: #fff;
    margin: 0 auto;
    box-sizing: border-box;
  }

  .title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  .sub-title {
    font-size: 14px;
    color: #999;
    margin-bottom: 16px;
  }

  .form-item {
    display: flex;
    width: 100%;
    flex-direction: column;
    margin-bottom: 12px;
    padding: 10px 16px 5px 16px;
  }

  .form-label {
    font-size: 14px;
    margin-bottom: 4px;
    color: #999;
  }

  .form-input {
    height: 36px;
    width: 90%;
    padding: 0;
    background-color: #222;
    border: 1px solid #444;
    color: #fff;
    border-radius: 4px;
  }

  .next-circle {
    font-size: 14px;
    color: #999;
    margin-bottom: 24px;
  }

  .confirm-btn {
    width: 90%;
    height: 40px;
    background-color: #3b4d63;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }

  .confirm-btn:hover {
    background-color: #4c6380;
  }
}
</style>