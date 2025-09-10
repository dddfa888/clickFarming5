<template>
  <div class="layout-container">
    <!-- 顶部蓝色区域 -->
    <div class="top-section">
      <img
        style="width: 100px; height: 100px; border-radius: 8px; margin-bottom: 10px;"
        src="/favicon.ico"
        alt="App Icon"
        class="app-icon"
      />
      <h1 class="app-name">快团</h1>
      <p class="version-device">版本 1.0.7 | 当前设备: {{ deviceType }}</p>
    </div>

    <!-- 中间按钮区域 -->
    <div class="button-section">
      <div class="button-item">
        <button class="detect-btn" @click="downloadApp">{{ title }}</button>
      </div>
      <div class="button-item">
        <button class="web-btn" @click="goWeb">访问网页版</button>
      </div>
    </div>

    <!-- 常见问题区域 -->
    <div class="faq-section">
      <h2 class="faq-title">常见安装问题</h2>
      <div class="faq-item" @click="openGuide">
        <span class="faq-badge">!</span>
        <span class="faq-text">{{ deviceType }} 安装指南</span>
        <span class="faq-arrow">></span>
      </div>
    </div>

    <!-- 弹框 -->
    <div v-if="showGuide" class="modal">
      <div class="modal-content">
        <h3>{{ deviceType }} 安装流程</h3>
        <p v-if="deviceType === '安卓设备'">
          1. 点击下载按钮，获取 APK 文件。
          <br />2. 如果提示“来自未知来源”，请在设置中允许安装。
          <br />3. 安装完成后即可使用。
        </p>
        <p v-else-if="deviceType === 'iOS设备'">
          1. 点击下载按钮，跳转至安装页面。
          <br />2. 安装后如果提示“不受信任的开发者”，请到【设置 > 通用 > 设备管理】中信任证书。
          <br />3. 返回桌面即可打开。
        </p>
        <p v-else>请使用网页版或联系管理员获取安装包。</p>
        <button class="close-btn" @click="closeGuide">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

const deviceType = ref("未知设备");
const showGuide = ref(false);
const title = ref("立即下载安装");

const openGuide = () => (showGuide.value = true);
const closeGuide = () => (showGuide.value = false);

const downloadApp = () => {
  title.value = "下载中...";

  if (deviceType.value === "安卓设备") {
    window.location.href = "https://example.com/app.apk";
  } else if (deviceType.value === "iOS设备") {
    window.location.href = "https://example.com/ios-install";
  } else {
    alert("当前设备暂不支持安装，请使用网页版");
    return;
  }

  // 模拟 3 秒后完成下载
  setTimeout(() => {
    title.value = "安装完成";
  }, 3000);
};

const goWeb = () => {
  window.location.href = "https://example.com/web";
};

onMounted(() => {
  const ua = navigator.userAgent || navigator.vendor || window.opera;

  if (/android/i.test(ua)) {
    deviceType.value = "安卓设备";
  } else if (/iPad|iPhone|iPod/.test(ua) && !window.MSStream) {
    deviceType.value = "iOS设备";
  } else if (/windows/i.test(ua)) {
    deviceType.value = "Windows设备";
  } else if (/macintosh|mac os x/i.test(ua)) {
    deviceType.value = "Mac设备";
  } else {
    deviceType.value = "其他设备";
  }
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.layout-container {
  position: relative; /* 让子元素绝对定位参照它 */
  margin: 0;
  padding: 0;
  height: 100vh;
  background-color: #f5f5f5;
  box-sizing: border-box;
}

/* 顶部蓝色区域样式 */
.top-section {
  background-color: #426ef0;
  color: white;
  text-align: center;
  padding: 60px 10px;
}

.app-icon {
  border-radius: 8px;
  margin-bottom: 10px;
}

.app-name {
  font-size: 24px;
  margin: 0 0 5px 0;
}

.version-device {
  font-size: 14px;
  margin: 0;
}

/* 中间按钮区域样式 */
/* 中间按钮区域样式 */
.button-section {
  position: absolute; /* 悬浮定位在蓝色区域底部 */
  top: calc(130px + 100px + 20px);
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 40px); /* 左右留白 */
  background-color: #fff; /* 白色背景 */
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12); /* 卡片阴影 */
  padding: 20px;
  z-index: 10;
}

/* 单个按钮外层 */
.button-item {
  margin-bottom: 15px;
}

.button-item:last-child {
  margin-bottom: 0;
}

/* 给按钮加立体效果 */
.detect-btn,
.web-btn {
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.detect-btn:active,
.web-btn:active {
  transform: scale(0.97);
}

/* 主按钮 */
.detect-btn {
  background-color: #426ef0;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 16px;
  width: 100%;
  cursor: pointer;
}

/* 次按钮 */
.web-btn {
  background-color: white;
  color: #426ef0;
  border: 1px solid #426ef0;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 16px;
  width: 100%;
  cursor: pointer;
}

.faq-section {
  padding: 20px;
  margin-top: 140px; /* 根据 button-section 的高度来调整 */
}

.faq-title {
  font-size: 18px;
  margin-bottom: 10px;
}

.faq-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.faq-badge {
  background-color: #eee;
  color: #426ef0;
  padding: 2px 6px;
  border-radius: 50%;
  margin-right: 10px;
}

.faq-text {
  flex: 1;
}

.faq-arrow {
  color: #999;
}

/* 遮罩层 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

/* 弹框主体 */
.modal {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  width: 80%;
  max-width: 400px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  text-align: left;
  animation: fadeIn 0.3s ease;
}

.modal h3 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #426ef0;
}

.modal-content p {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.close-btn {
  margin-top: 20px;
  background: #426ef0;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  cursor: pointer;
  width: 100%;
  font-size: 16px;
}

/* 动画 */
@keyframes fadeIn {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>