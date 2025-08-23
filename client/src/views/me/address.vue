<template>
  <div class="company-intro">
    <HeaderBar :title="t('TRC 20 地址')" />
    <div class="form">
      <div class="form-group">
        <label :class="{ floated: inputValue }">{{ t("地址") }}</label>
        <input v-model="withdrawalAddress" type="text" placeholder=" " class="input" />
      </div>
      <button class="submit-btn" @click="submit">{{ t("确认绑定卡") }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { getUserInfo, updateUserSimpleFront } from "../../api/index";
import { showAlert } from "../../utils/notify";
const inputValue = ref("");
import { useI18n } from "vue-i18n";
import router from "../../router";
const { t } = useI18n();
const withdrawalAddress = ref("");
const hasData = ref(true);

getUserInfo().then(res => {
  //console.log(res.data.withdrawalAddress);
  let data = res.data.withdrawalAddress;
  withdrawalAddress.value = data;
  hasData.value = data != null && data != "";
});

function submit() {
  updateUserSimpleFront({ withdrawalAddress: withdrawalAddress.value }).then(
    res => {
      if (res.code === 200) {
        showAlert(t("操作成功"), 2000);
      } else {
        showAlert(t(res.msg), 2000);
      }
    }
  );
}
</script>

<style scoped>
.company-intro {
  background: #121212;
  height: 100vh;
  overflow-y: auto;
}
.form {
  text-align: center;
  border-radius: 8px;
}
.form-group {
  position: relative;
  margin: 20px;
  margin-top: 80px;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 16px 10px 6px;
  background-color: transparent;
  font-family: sans-serif;
  overflow: visible; /* 避免 label 超出边框被裁剪 */
}

.submit-btn {
  width: 80%;
  color: #fff;
  border-radius: 4px;
  text-align: center;
  border: none;
  height: 50px;
  background-color: #2f3848;
}

.input {
  width: 100%;
  border: none;
  outline: none;
  font-size: 16px;
  background: transparent;
  color: #fff;
  padding: 0;
  z-index: 2;
}

label {
  position: absolute;
  left: 10px;
  top: -13px;
  color: #aaa;
  font-size: 16px;
  pointer-events: none;
  transition: 0.2s ease all;
  background-color: #121212;
  padding: 0 4px;
}

@media screen and (min-width: 768px) {
  .company-intro {
    background: #121212;
    height: 100vh;
    width: 450px;
    margin: 0 auto;
    overflow-y: auto;
  }
  .form {
    text-align: center;
    border-radius: 8px;
  }
  .form-group {
    position: relative;
    margin: 20px;
    margin-top: 80px;
    border: 1px solid #ccc;
    border-radius: 6px;
    padding: 16px 10px 6px;
    background-color: transparent;
    font-family: sans-serif;
    overflow: visible; /* 避免 label 超出边框被裁剪 */
  }

  .submit-btn {
    width: 80%;
    color: #fff;
    border-radius: 4px;
    text-align: center;
    border: none;
    height: 50px;
    padding: 0 20px;
    background-color: #2f3848;
  }

  .input {
    width: 100%;
    border: none;
    outline: none;
    font-size: 16px;
    background: transparent;
    color: #fff;
    padding: 0;
    z-index: 2;
  }

  label {
    position: absolute;
    left: 10px;
    top: -13px;
    color: #aaa;
    font-size: 16px;
    pointer-events: none;
    transition: 0.2s ease all;
    background-color: #121212;
    padding: 0 4px;
  }
}
</style>
