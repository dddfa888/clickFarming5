<template>
  <div class="user_name">
    <HeaderBar :title="t('账户名')" />
    <div class="user_name_input">
      <label class="user_name_label" for="username">{{ $t("账户名") }}</label>
      <input
        type="text"
        style="background-color: #121212; width: 70%;border:none; color: #fff"
        v-model="username"
      />
    </div>
    <button class="btn-primary" @click="updateUsername">{{ $t("确认修改") }}</button>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import HeaderBar from "../../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { updateUser } from "../../../api/index";
import { useInfoStore } from "../../../store/useInfoStore.js";
import { showAlert } from "../../../utils/notify.js";
import router from "../../../router/index.js";
const { t } = useI18n();
const username = ref("");
const infoStore = useInfoStore();

const updateUsername = async () => {
  const res = await updateUser({
    loginAccount: username.value,
    uid: infoStore.getUserinfo.uid
  });
  if (res.code === 200) {
    showAlert(t("操作成功"), 2000);
    router.back();
  } else {
    showAlert(t("修改失败"), 2000);
  }
};
</script>
<style scoped>
.user_name {
  width: 100%;
  height: 100vh;
  background-color: #121212;
}

.user_name_input {
  margin: 10px;
  padding-top: 50px;
  width: 100%;
  display: flex;
  justify-content: space-around;
}

.user_name_label {
  color: #fff;
}

.btn-primary {
  margin-top: 20px;
  width: 90%;
  height: 40px;
  background-color: #2f3848;
  border: none;
  color: #fff;
  border-radius: 5px;
  font-size: 15px;
  margin-left: 5%;
}

@media screen and (min-width: 768px) {
  .user_name {
    width: 450px;
    height: 100vh;
    margin: 0 auto;
    background-color: #121212;
  }

  .user_name_input {
    margin: 10px;
    padding-top: 50px;
    width: 100%;
    display: flex;
    justify-content: space-around;
  }

  .user_name_label {
    color: #fff;
  }

  .btn-primary {
    margin-top: 20px;
    width: 90%;
    height: 40px;
    background-color: #2f3848;
    border: none;
    color: #fff;
    border-radius: 5px;
    font-size: 15px;
    margin-left: 5%;
  }
}
</style>