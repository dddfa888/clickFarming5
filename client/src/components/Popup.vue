<template>
  <div class="popup-overlay" v-if="showPopup">
    <div class="popup-content">
      <p class="popup-text">请联系客服进行充值</p>
      <p class="popup-subtext">每日10:00至23:00期间联系客服办理充值业务</p>
      <div class="popup-buttons">
        <button class="contact-btn" @click="goToCustomerService">立即联系</button>
        <button class="cancel-btn" @click="closePopup">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getCustomerService } from "../api/index";

// 控制弹框显示隐藏
const showPopup = ref(false);

// 打开弹框方法，可在父组件中调用
const openPopup = () => {
  showPopup.value = true;
};

// 关闭弹框方法
const closePopup = () => {
  showPopup.value = false;
};

// 联系客服方法，实际可根据需求对接客服逻辑
const contactCustomerService = () => {
  console.log("联系客服");
  // 这里可以添加调用客服相关接口或打开客服聊天窗口等逻辑
};
const CustomerService = ref("");
const goToCustomerService = () => {
  window.open(CustomerService.value, "_blank");
};

onMounted(() => {
  getCustomerService().then(res => {
    CustomerService.value = res.data[0].configValue;
    console.log(res.data[0].configValue);
  });
});

// 暴露方法给父组件
defineExpose({
  openPopup,
  closePopup
});
</script>

<style scoped>
/* 遮罩层样式 */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

/* 弹框内容样式 */
.popup-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 400px;
  text-align: center;
}

/* 弹框文本样式 */
.popup-text {
  font-size: 18px;
  margin-bottom: 10px;
}

/* 弹框子文本样式 */
.popup-subtext {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

/* 按钮容器样式 */
.popup-buttons {
  display: flex;
  justify-content: space-around;
}

/* 联系按钮样式 */
.contact-btn {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

/* 取消按钮样式 */
.cancel-btn {
  background-color: #ccc;
  color: #333;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

@media screen and (min-width: 768px) {
  /* 遮罩层样式 */
  .popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
  }

  /* 弹框内容样式 */
  .popup-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 80%;
    max-width: 400px;
    text-align: center;
  }

  /* 弹框文本样式 */
  .popup-text {
    font-size: 18px;
    margin-bottom: 10px;
  }

  /* 弹框子文本样式 */
  .popup-subtext {
    font-size: 14px;
    color: #666;
    margin-bottom: 20px;
  }

  /* 按钮容器样式 */
  .popup-buttons {
    display: flex;
    justify-content: space-around;
  }

  /* 联系按钮样式 */
  .contact-btn {
    background-color: #007bff;
    color: #fff;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
  }

  /* 取消按钮样式 */
  .cancel-btn {
    background-color: #ccc;
    color: #333;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
  }
}
</style>