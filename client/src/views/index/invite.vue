<template>
  <div class="invite">
    <HeaderBar :title="t(`邀请好友`)" />
    <div class="card">
      <qrcode-vue :value="inviteUrl" :size="180" level="H" class="qrcode" />
      <div class="invite-code">{{$t("扩展代码")}}:{{ invitationCode }}</div>
    </div>

    <div class="btn" @click="copy(inviteUrl)">{{ $t("复制邀请链接") }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import QrcodeVue from "qrcode.vue";
import { useI18n } from "vue-i18n";
import { getUserInfo } from "../../api";
import { showAlert } from "../../utils/notify";

const qrcodenum = ref("");
const invitationCode = ref("");

onMounted(async () => {
  const userInfo = await getUserInfo();
  qrcodenum.value = userInfo.data.inviterCode;
  invitationCode.value = userInfo.data.invitationCode;
});

// 用 computed 拼接
const inviteUrl = computed(() => {
  const baseUrl = window.location.origin; // 当前访问的域名+端口
  return `${baseUrl}#/register?invite=${invitationCode.value || ""}`;
});

const { t } = useI18n();

// 复制函数
const copy = async text => {
  try {
    await navigator.clipboard.writeText(text);
    showAlert(t("复制成功"), 2000);
  } catch (err) {
    console.error("复制失败", err);
  }
};
</script>

<style scoped>
.invite {
  background: url("../../assets/img/black-yq.png") no-repeat center center;
  background-size: cover;
  width: 100%;
  height: 100vh;

  display: flex;
  flex-direction: column;
  justify-content: center; /* 卡片垂直居中 */
  align-items: center;
}

.card {
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  width: 300px;
  margin-top: 79vw;
}

.invite-code {
  margin-top: 10px;
  font-size: 16px;
  color: #fff;
}

.qrcode {
  margin-top: 16px;
}

.btn {
  margin-top: 20px;
  padding: 12px 44px;
  background: linear-gradient(to bottom left, #fbf9e7, #d2b87d, #fbf9e7);
  border-radius: 45px;
  color: #fff;
  cursor: pointer;
  font-size: 16px;
}

@media screen and (min-width: 768px) {
  .invite {
    background: url("../../assets/img/black-yq.png") no-repeat center center;
    background-size: cover;
    width: 450px;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center; /* 卡片垂直居中 */
    align-items: center;
    margin: 0 auto;
  }

  .card {
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    width: 300px;
    margin-top: 20vw;
  }

  .invite-code {
    margin-top: 10px;
    font-size: 16px;
    color: #fff;
  }

  .qrcode {
    margin-top: 16px;
  }

  .btn {
    margin-top: 20px;
    padding: 12px 44px;
    background: linear-gradient(to bottom left, #fbf9e7, #d2b87d, #fbf9e7);
    border-radius: 45px;
    color: #fff;
    cursor: pointer;
    font-size: 16px;
  }
}
</style>
