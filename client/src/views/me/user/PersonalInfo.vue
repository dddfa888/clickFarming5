<template>
  <div class="personal-info">
    <HeaderBar :title="t('个人资讯')" />
    <ul class="info-list">
      <!-- 头像 -->
      <li class="list-item" @click="handleItemClick('avatar')">
        <div class="left">
          <img class="icon-img" src="../../../assets/img/txbt.png" alt />
          <span class="item-text">{{ $t("头像") }}</span>
        </div>
        <div class="right">
          <img class="avatar-img" src="../../../assets/img/head.png" alt />
          <img class="arrow-icon" src="../../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 系统账户 -->
      <li class="list-item">
        <div class="left">
          <img class="icon-img" src="../../../assets/img/qbbt.png" alt />
          <span class="item-text">{{ $t("系统账户") }}</span>
        </div>
        <div class="right">
          <span class="item-value">{{ loginAccount }}</span>
          <img class="arrow-icon" src="../../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 账号名 -->
      <li class="list-item" @click="handleItemClick('username')">
        <div class="left">
          <img class="icon-img" src="../../../assets/img/my_cyan.png" alt />
          <span class="item-text">{{ $t("账号名") }}</span>
        </div>
        <div class="right">
          <span class="item-value">{{ loginAccount }}</span>
          <img class="arrow-icon" src="../../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 密码管理 -->
      <li class="list-item" @click="handleItemClick('pwdmanagement')">
        <div class="left">
          <img class="icon-img" src="../../../assets/img/mmbt.png" alt />
          <span class="item-text">{{ $t("密码管理") }}</span>
        </div>
        <div class="right">
          <img class="arrow-icon" src="../../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 交易密码管理 -->
      <li class="list-item" @click="handleItemClick('tradingpwd')">
        <div class="left">
          <img class="icon-img" src="../../../assets/img/mmbt.png" alt />
          <span class="item-text">{{ $t("交易密码管理") }}</span>
        </div>
        <div class="right">
          <img class="arrow-icon" src="../../../assets/img/right.png" alt />
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import HeaderBar from "../../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { getUserInfo } from "../../../api/index";
import { useInfoStore } from "../../../store/useInfoStore";
import { onMounted, ref } from "vue";
const { t } = useI18n();
const router = useRouter();
const loginAccount = ref("");
const InfoStore = useInfoStore();

const handleItemClick = type => {
  router.push(type);
};
onMounted(() => {
  getUserInfo().then(res => {
    // InfoStore.setUserinfo(res.data);
    loginAccount.value = res.data.loginAccount;
  });
});
</script>

<style scoped>
.personal-info {
  background-color: #1e201f;
  color: #fff;
  height: 100vh;
}

/* 列表 */
.info-list {
  list-style: none;
  margin: 0;
  padding: 0;
  padding-top: 50px;
}

/* 每一行 */
.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px;
  background-color: #252726;
  cursor: pointer;
  margin: 8px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.list-item:hover {
  background: #2c2c2c;
}

/* 左右区块 */
.left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 图标 */
.icon-img {
  width: 20px;
  height: 22px;
}

/* 文字 */
.item-text {
  font-size: 15px;
}

.item-value {
  font-size: 14px;
  color: #aaa;
}

/* 头像 */
.avatar-img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
}

/* 箭头 */
.arrow-icon {
  height: 14px;
  opacity: 0.7;
}

@media screen and (min-width: 768px) {
  .personal-info {
    background-color: #1e201f;
    color: #fff;
    height: 100vh;
    width: 450px;
    margin: 0 auto;
  }

  /* 列表 */
  .info-list {
    list-style: none;
    margin: 0;
    padding: 0;
    padding-top: 50px;
  }

  /* 每一行 */
  .list-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 20px;
    background-color: #252726;
    cursor: pointer;
    margin: 8px 12px;
    border-radius: 8px;
    transition: background 0.2s;
  }

  .list-item:hover {
    background: #2c2c2c;
  }

  /* 左右区块 */
  .left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .right {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  /* 图标 */
  .icon-img {
    width: 20px;
    height: 22px;
  }

  /* 文字 */
  .item-text {
    font-size: 15px;
  }

  .item-value {
    font-size: 14px;
    color: #aaa;
  }

  /* 头像 */
  .avatar-img {
    width: 38px;
    height: 38px;
    border-radius: 50%;
  }

  /* 箭头 */
  .arrow-icon {
    height: 14px;
    opacity: 0.7;
  }
}
</style>
