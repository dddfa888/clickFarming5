<template>
  <div class="pay-password-page">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <polyline points="15 18 9 12 15 6" />
      </svg>
    </button>

    <!-- 标题和说明文字 -->
    <h2>{{ currentStep === 1 ? '设置支付密码' : '确认支付密码' }}</h2>
    <p>
      {{ currentStep === 1
      ? '请设置支付密码，用于支付验证'
      : '请再次输入支付密码'
      }}
    </p>

    <!-- 错误提示 -->
    <p class="error-message" v-if="showError">两次输入的密码不一致，请重新输入</p>

    <!-- 密码输入框（6个方块） -->
    <div class="password-boxes">
      <div
        v-for="(box, index) in passwordLength"
        :key="index"
        class="password-box"
        :class="{ 'filled': currentPassword.length > index }"
      ></div>
    </div>

    <!-- 数字键盘 -->
    <div class="number-keyboard">
      <div class="keyboard-row">
        <button class="keyboard-key" @click="handleKeyPress(1)">1</button>
        <button class="keyboard-key" @click="handleKeyPress(2)">2</button>
        <button class="keyboard-key" @click="handleKeyPress(3)">3</button>
      </div>
      <div class="keyboard-row">
        <button class="keyboard-key" @click="handleKeyPress(4)">4</button>
        <button class="keyboard-key" @click="handleKeyPress(5)">5</button>
        <button class="keyboard-key" @click="handleKeyPress(6)">6</button>
      </div>
      <div class="keyboard-row">
        <button class="keyboard-key" @click="handleKeyPress(7)">7</button>
        <button class="keyboard-key" @click="handleKeyPress(8)">8</button>
        <button class="keyboard-key" @click="handleKeyPress(9)">9</button>
      </div>
      <div class="keyboard-row">
        <button class="keyboard-key empty-key"></button>
        <button class="keyboard-key" @click="handleKeyPress(0)">0</button>
        <button class="keyboard-key" @click="deleteLastChar">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M18 6L6 18M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <button class="nav-btn" @click="goToHome">首页</button>
      <button class="nav-btn" @click="goToMessage">信息</button>
      <button class="nav-btn" @click="goToPinGoods">拼好货</button>
      <button class="nav-btn active">网页版</button>
      <button class="nav-btn" @click="goToMy">我的</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

// 密码相关
const passwordLength = ref(6);
const firstPassword = ref(""); // 第一次输入的密码
const currentPassword = ref(""); // 当前输入的密码
const currentStep = ref(1); // 当前步骤：1-设置密码，2-确认密码
const showError = ref(false); // 是否显示错误提示

// 处理数字按键点击
const handleKeyPress = num => {
  if (currentPassword.value.length < passwordLength.value) {
    currentPassword.value += num;

    // 检查是否完成当前步骤的输入
    if (currentPassword.value.length === passwordLength.value) {
      if (currentStep.value === 1) {
        // 第一步完成，保存密码并进入确认步骤
        firstPassword.value = currentPassword.value;
        currentPassword.value = "";
        currentStep.value = 2;
        showError.value = false;
      } else {
        // 第二步完成，验证两次密码是否一致
        if (currentPassword.value === firstPassword.value) {
          // 密码一致，完成设置
          console.log("密码设置成功，密码为：", firstPassword.value);
          // 这里可以添加调用设置密码的接口等操作
          alert("支付密码设置成功！");
        } else {
          // 密码不一致，显示错误提示并重置
          showError.value = true;
          currentPassword.value = "";
        }
      }
    }
  }
};

// 删除最后一个字符
const deleteLastChar = () => {
  currentPassword.value = currentPassword.value.slice(0, -1);
  // 清除错误提示
  showError.value = false;
};

// 返回上一页
const goBack = () => {
  if (currentStep.value === 2) {
    // 如果在确认步骤，返回设置步骤
    currentStep.value = 1;
    currentPassword.value = "";
    showError.value = false;
  } else {
    // 否则返回上一页
    console.log("返回上一页");
    // 可结合路由进行页面跳转，比如使用 router.back()
  }
};

// 底部导航方法
const goToHome = () => console.log("前往首页");
const goToMessage = () => console.log("前往信息页");
const goToPinGoods = () => console.log("前往拼好货页");
const goToMy = () => console.log("前往我的页");
</script>

<style scoped>
/* 页面整体样式 */
.pay-password-page {
  width: 100%;
  height: 100vh;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
  box-sizing: border-box;
}

/* 返回按钮样式 */
.back-btn {
  position: absolute;
  left: 20px;
  top: 20px;
  background: none;
  border: none;
  cursor: pointer;
  color: #333;
}

/* 标题样式 */
h2 {
  font-size: 20px;
  margin-top: 40px;
  margin-bottom: 10px;
  color: #333;
}

/* 说明文字样式 */
p {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  text-align: center;
  max-width: 80%;
}

/* 错误提示样式 */
.error-message {
  color: #ff4d4f;
  margin: 10px 0;
  animation: shake 0.5s;
}

/* 抖动动画 */
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20%,
  60% {
    transform: translateX(-5px);
  }
  40%,
  80% {
    transform: translateX(5px);
  }
}

/* 密码方块容器样式 */
.password-boxes {
  display: flex;
  gap: 10px;
  margin: 20px 0 50px;
}

/* 密码方块样式 */
.password-box {
  width: 36px;
  height: 36px;
  border: 1px solid #ccc;
  border-radius: 6px;
  transition: all 0.3s ease;
}

/* 已填充密码的方块样式 */
.password-box.filled {
  background-color: #333;
  position: relative;
}

.password-box.filled::after {
  content: "•";
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 24px;
}

/* 数字键盘样式 */
.number-keyboard {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
  flex-grow: 1;
  justify-content: center;
}

/* 键盘行样式 */
.keyboard-row {
  display: flex;
  width: 90%;
  justify-content: space-around;
  margin-bottom: 12px;
}

/* 键盘按键样式 */
.keyboard-key {
  width: 70px;
  height: 70px;
  font-size: 24px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.keyboard-key:active {
  background-color: #e0e0e0;
  transform: scale(0.95);
}

/* 空按键样式（左下角空按键） */
.empty-key {
  background-color: transparent;
  box-shadow: none;
}

/* 底部导航栏样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  width: 100%;
  padding: 10px 0;
  background-color: #fff;
  border-top: 1px solid #eee;
  position: fixed;
  bottom: 0;
  left: 0;
}

/* 导航按钮样式 */
.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #666;
  padding: 5px 0;
}

/* 激活状态的导航按钮样式 */
.nav-btn.active {
  color: #007bff;
}
</style>
