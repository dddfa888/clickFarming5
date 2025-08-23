<template>
  <div class="user-center">
    <!-- Header -->
    <header>
      <div class="header">
        <span></span>
        <span class="title">{{ $t("用户") }}</span>
        <div class="icons">
          <img src="../../assets/img/black-sz.png" @click="router.push('/personalInfo')" alt />
          <img src="../../assets/img/ld-black.png" @click="router.push('/notice')" alt />
        </div>
      </div>

      <!-- 用户信息卡片，重叠 header -->
      <div class="card">
        <div class="card-top">
          <div class="avatar" @click="router.push('/avatar')">
            <img v-if="infoStore.getUserinfo.headImg" :src="infoStore.getUserinfo.headImg" alt />
            <img v-else src="../../assets/img/black-tx.png" alt />
          </div>
          <div class="info">
            <p class="name">{{ infoStore.getUserinfo.loginAccount }}</p>
            <p class="desc">{{ $t("推广程式码") }}: {{ infoStore.getUserinfo.invitationCode }}</p>
            <p class="desc">{{ $t("信用分数") }}: {{ infoStore.getUserinfo.creditScore }}</p>
          </div>
          <div class="vip">{{ infoStore.getUserinfo.levelName }}</div>
        </div>

        <div class="card-balance">
          <p class="label">{{ $t("我的余额") }}</p>
          <p class="amount">${{ infoStore.getUserinfo.accountBalance }}</p>
        </div>

        <div class="card-actions">
          <button class="btn withdraw" @click="router.push('/withdraw')">{{ $t("提款") }}</button>
          <button class="btn recharge" @click="router.push('/recharge')">{{ $t("充值") }}</button>
        </div>
      </div>
    </header>

    <!-- 其他模块 -->
    <div class="shortcut">
      <div class="item" @click="router.push('/depositHistory')">
        <img src="../../assets/img/black-tkjl.png" alt />
        <p>{{ $t("充值记录") }}</p>
      </div>
      <div class="item" @click="router.push('/warehouse')">
        <img src="../../assets/img/black-ddls.png" alt />
        <p>{{ $t("拍卖纪录") }}</p>
      </div>
      <div class="item" @click="router.push('/withdrawHistory')">
        <img src="../../assets/img/black-ls.png" alt />
        <p>{{ $t("提款记录") }}</p>
      </div>
    </div>

    <div class="menu">
      <div class="menu-item" @click="router.push('/personalInfo')">
        <img src="../../assets/img/black-grxx.png" alt />
        {{ $t("个人资讯") }}
      </div>
      <div class="menu-item" @click="router.push('/accountdetail')">
        <img src="../../assets/img/black-khxx.png" alt />
        {{ $t("帐户详细资讯") }}
      </div>
      <div class="menu-item" @click="router.push('/bankinfo')">
        <img src="../../assets/img/black-grxx.png" alt />
        {{ $t("我的银行卡") }}
      </div>
      <div class="menu-item" @click="router.push('/address')">
        <img src="../../assets/img/black-grxx.png" alt />
        {{ $t("TRC 20 地址") }}
      </div>
      <div class="menu-item" @click="router.push('/invite')">
        <img src="../../assets/img/black-yqpy.png" alt />
        {{ $t("邀请朋友") }}
      </div>
      <div class="menu-item" @click="router.push('/notice')">
        <img src="../../assets/img/ld-black.png" alt />
        {{ $t("系统消息") }}
      </div>
    </div>

    <div class="footer">
      <button class="exit-btn" @click="handleLogout">{{ $t("退出") }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { storeToRefs } from "pinia";
import { useInfoStore } from "../../store/useInfoStore.js";
import { getUserInfo } from "../../api/index";

const router = useRouter();
const { locale: i18nLocale } = useI18n();
const { t } = useI18n();
const infoStore = useInfoStore();

const handleAction = row => {
  if (row === "deposit") {
    window.open(
      "https://chat.ichatlink.net/widget/standalone.html?eid=6df096f4e9b05ad245f542d63ed1c8d7&language=en",
      "_blank"
    );
  } else if (row === "withdraw") {
    router.push("/withdraw");
  } else if (row === "withdrawHistory") {
    router.push("/withdrawHistory");
  } else if (row === "depositHistory") {
    router.push("/depositHistory");
  } else if (row === "orderHistory") {
    router.push("/orderHistory");
  } else if (row === "rewardHistory") {
    router.push("/rewardHistory");
  } else if (row === "groupReport") {
    router.push("/groupReport");
  } else if (row === "bankInfo") {
    router.push("/bankInfo");
  } else if (row === "address") {
    router.push("/address");
  }
};

const handleLogout = () => {
  localStorage.removeItem("token");
  infoStore.clearUserinfo();
  router.push("/login");
};

onMounted(() => {
  getUserInfo().then(res => {
    infoStore.setUserinfo(res.data);
  });
});
</script>

<style scoped>
.user-center {
  max-width: 375px;
  margin: 0 auto;
  min-height: 100vh;
  background: #1e201f;
  color: #fff;
  display: flex;
  flex-direction: column;
}

header {
  height: 300px;
}

/* 顶部 header */
.header {
  height: 200px;
  background: url("../../assets/img/my-black.png") no-repeat center/cover; /* 可放背景 */
  display: flex;
  justify-content: space-between;
  padding: 16px;
  position: relative;
  z-index: 1;
}
.header .title {
  font-size: 18px;
  font-weight: bold;
}
.header .icons {
  display: flex;
  gap: 16px;
}
.header .icons img {
  width: 24px;
  height: 24px;
}

/* 卡片浮动效果 */
.card {
  margin: -40px 16px 0; /* 让卡片往上顶出 header */
  background: url("../../assets/img/black-kp.png") no-repeat center/cover;
  color: #000;
  border-radius: 16px;
  padding: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 2;
  position: relative;
  top: -38vw;
}
.card-top {
  display: flex;
  align-items: center;
}
.avatar {
  width: 48px;
  height: 48px;
  background: #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar img {
  width: 100%;
}
.info {
  margin-left: 12px;
}
.info .name {
  font-weight: bold;
}
.info .desc {
  font-size: 12px;
  color: #555;
}
.vip {
  background-image: url("../../assets/img/logo-black.png");
  width: 130px;
  height: 130px;
  text-align: center;
  line-height: 130px;
  color: #d1790f;
  font-weight: bold;
  font-size: 18px;
  position: absolute;
  bottom: 30px;
  right: 10px;
}
.card-balance {
  margin-top: 12px;
  color: #664630;
}
.card-balance .label {
  font-size: 14px;
}
.card-balance .amount {
  font-size: 22px;
  font-weight: bold;
}
.card-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}
.card-actions .btn {
  width: 60px;
  padding: 4px 0;
  border-radius: 8px;
  font-weight: bold;
  font-size: 12px;
  border: none;
}
.card-actions .withdraw {
  background: #e5c990;
  color: #664630;
}
.card-actions .recharge {
  background: #e5c990;
  color: #664630;
}

/* 下面的部分保持不变 */
.shortcut {
  display: flex;
  justify-content: space-around;
  background: #252726;
  border-radius: 8px;
  margin: 20px 16px 0;
  padding: 16px 0;
  text-align: center;
}
.shortcut .item img {
  width: 25px;
}
.shortcut .item p {
  font-size: 12px;
}

.menu {
  margin: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
  background-color: #252726;
}
.menu-item {
  background: #252726;
  padding: 12px;
  display: flex;
  align-items: center;
  border-radius: 8px;
  font-size: 14px;
}
.menu-item img {
  width: 25px;
  margin-right: 10px;
}

.footer {
  margin: 5px 16px;
  margin-bottom: 80px;
}
.exit-btn {
  width: 100%;
  background: #dc2626;
  color: #fff;
  padding: 12px;
  border-radius: 8px;
  font-weight: bold;
  border: none;
  font-size: 16px;
}
@media screen and (min-width: 768px) {
  .user-center {
    width: 450px;
    margin: 0 auto;
    min-height: 100vh;
    background: #1e201f;
    color: #fff;
    display: flex;
    flex-direction: column;
  }

  header {
    height: 300px;
  }

  /* 顶部 header */
  .header {
    height: 200px;
    background: url("../../assets/img/my-black.png") no-repeat center/cover; /* 可放背景 */
    display: flex;
    justify-content: space-between;
    padding: 16px;
    position: relative;
    z-index: 1;
  }
  .header .title {
    font-size: 18px;
    font-weight: bold;
  }
  .header .icons {
    display: flex;
    gap: 16px;
  }
  .header .icons img {
    width: 24px;
    height: 24px;
  }

  /* 卡片浮动效果 */
  .card {
    margin: -40px 16px 0; /* 让卡片往上顶出 header */
    background: url("../../assets/img/black-kp.png") no-repeat center/cover;
    color: #000;
    border-radius: 16px;
    padding: 5px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    z-index: 2;
    position: relative;
    top: -7vw;
  }
  .card-top {
    display: flex;
    align-items: center;
  }
  .avatar {
    width: 48px;
    height: 48px;
    background: #ccc;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .avatar img {
    width: 100%;
  }
  .info {
    margin-left: 12px;
  }
  .info .name {
    font-weight: bold;
  }
  .info .desc {
    font-size: 12px;
    color: #555;
  }
  .vip {
    background-image: url("../../assets/img/logo-black.png");
    width: 130px;
    height: 130px;
    text-align: center;
    line-height: 130px;
    color: #d1790f;
    font-weight: bold;
    font-size: 18px;
    position: absolute;
    bottom: 30px;
    right: 10px;
  }
  .card-balance {
    margin-top: 12px;
    color: #664630;
  }
  .card-balance .label {
    font-size: 14px;
  }
  .card-balance .amount {
    font-size: 22px;
    font-weight: bold;
  }
  .card-actions {
    display: flex;
    gap: 12px;
    margin-top: 16px;
  }
  .card-actions .btn {
    width: 60px;
    padding: 4px 0;
    border-radius: 8px;
    font-weight: bold;
    font-size: 12px;
    border: none;
  }
  .card-actions .withdraw {
    background: #e5c990;
    color: #664630;
  }
  .card-actions .recharge {
    background: #e5c990;
    color: #664630;
  }

  /* 下面的部分保持不变 */
  .shortcut {
    display: flex;
    justify-content: space-around;
    background: #252726;
    border-radius: 8px;
    margin: 20px 16px 0;
    padding: 16px 0;
    text-align: center;
  }
  .shortcut .item img {
    width: 25px;
  }
  .shortcut .item p {
    font-size: 12px;
  }

  .menu {
    margin: 20px 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    flex: 1;
    background-color: #252726;
  }
  .menu-item {
    background: #252726;
    padding: 12px;
    display: flex;
    align-items: center;
    border-radius: 8px;
    font-size: 14px;
  }
  .menu-item img {
    width: 25px;
    margin-right: 10px;
  }

  .footer {
    margin: 5px 16px;
    margin-bottom: 80px;
  }
  .exit-btn {
    width: 100%;
    background: #dc2626;
    color: #fff;
    padding: 12px;
    border-radius: 8px;
    font-weight: bold;
    border: none;
    font-size: 16px;
  }
}
</style>