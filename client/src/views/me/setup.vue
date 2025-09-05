<template>
  <div class="setting-container">
    <!-- 头部区域 -->

    <HeaderBar title="设置" backcolor="#ece9ee" />
    <div class="header">
      <span class="back">&lt;</span>
      <span class="title"></span>
    </div>

    <!-- 设置选项区域 -->
    <div class="setting-options">
      <div class="option-item" @click="router.push('/setpaypassword')">
        <span>二级密码</span>
        <span class="arrow">
          <img src="../../assets/img/right.png" alt />
        </span>
      </div>
      <div class="option-item">
        <span>检查更新</span>
        <span class="arrow">
          <img src="../../assets/img/right.png" alt />
        </span>
      </div>
      <div class="option-item center logout" @click="logout">退出登录</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

const { t } = useI18n();
const router = useRouter();

const logout = () => {
  localStorage.removeItem("token");
  router.push("/login");
};
</script>

<style scoped>
/* 容器样式 */
.setting-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  background-color: #ece9ee;
  height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 头部样式 */
.header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  font-size: 18px;
  border-bottom: 1px solid #eee;
}
.back {
  cursor: pointer;
  margin-right: 10px;
}
.title {
  font-weight: bold;
}

/* 设置选项样式 */
.setting-options {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}
.option-item {
  background-color: #ffff;
  padding: 15px;
  margin: 5px 10px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.arrow {
  color: #ccc;
}
.center {
  justify-content: center;
}
.logout {
  color: red;
}

/* 版本信息样式 */
.version {
  text-align: center;
  color: #999;
  margin-bottom: 10px;
}

/* 底部导航样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
  border-top: 1px solid #eee;
}
.nav-item {
  text-align: center;
}
.active {
  color: blue;
}
.icon {
  font-size: 14px;
}

@media screen and (min-width: 768px) {
  /* 容器样式 */
  .setting-container {
    background-color: #ece9ee;
    height: 100vh;
    max-width: 450px;
    margin: 0 auto;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  /* 头部样式 */
  .header {
    display: flex;
    align-items: center;
    padding: 10px 0;
    font-size: 18px;
    border-bottom: 1px solid #eee;
  }
  .back {
    cursor: pointer;
    margin-right: 10px;
  }
  .title {
    font-weight: bold;
  }

  /* 设置选项样式 */
  .setting-options {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 10px 0;
  }
  .option-item {
    background-color: #ffff;
    padding: 15px;
    margin: 5px 10px;
    border-radius: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .arrow {
    color: #ccc;
  }
  .center {
    justify-content: center;
  }
  .logout {
    color: red;
  }

  /* 版本信息样式 */
  .version {
    text-align: center;
    color: #999;
    margin-bottom: 10px;
  }

  /* 底部导航样式 */
  .bottom-nav {
    display: flex;
    justify-content: space-around;
    padding: 10px 0;
    border-top: 1px solid #eee;
  }
  .nav-item {
    text-align: center;
  }
  .active {
    color: blue;
  }
  .icon {
    font-size: 14px;
  }
}
</style>