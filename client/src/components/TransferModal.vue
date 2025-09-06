<template>
  <!-- 遮罩层 -->
  <div
    class="modal-overlay"
    v-if="visible"
    @click="$emit('close')"
    :class="{ 'overlay-visible': visible }"
  >
    <!-- 弹窗内容 - 底部滑入 -->
    <div class="modal-container" @click.stop :class="{ 'container-visible': visible }">
      <!-- 弹窗顶部操作区 -->
      <div class="modal-top">
        <h3 class="modal-title">转入余额宝</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <!-- 弹窗内容区 -->
      <div class="modal-content">
        <!-- 可转入余额信息 -->
        <div class="balance-info">
          <span>可转入余额</span>
          <span class="available-amount">¥{{ availableBalance.toFixed(2) }}</span>
        </div>

        <!-- 金额输入区域 -->
        <div class="amount-input-container">
          <div class="currency-symbol">¥</div>
          <input
            type="number"
            v-model="amount"
            class="amount-input"
            placeholder="0"
            min="0"
            step="0.01"
            @input="handleAmountChange"
          />
          <button class="all-btn" @click="useAllBalance">全部</button>
        </div>
      </div>

      <!-- 底部确认按钮 -->
      <div class="modal-footer">
        <button class="confirm-btn" :disabled="!isAmountValid" @click="handleConfirm">转入余额宝</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";

// 接收父组件传入的参数
const props = defineProps({
  // 控制弹窗显示/隐藏
  visible: {
    type: Boolean,
    default: false
  },
  // 可转入的余额
  availableBalance: {
    type: Number,
    default: 0
  }
});

// 向外暴露事件
const emit = defineEmits(["close", "confirm"]);

// 输入的金额
const amount = ref(0);

// 处理金额变化
const handleAmountChange = () => {
  // 确保金额不超过可转入余额
  if (amount.value > props.availableBalance) {
    amount.value = props.availableBalance;
  }
  // 确保金额不为负数
  if (amount.value < 0) {
    amount.value = 0;
  }
};

// 使用全部余额
const useAllBalance = () => {
  amount.value = props.availableBalance;
};

// 验证金额是否有效
const isAmountValid = computed(() => {
  return amount.value > 0 && amount.value <= props.availableBalance;
});

// 处理确认转入
const handleConfirm = () => {
  if (isAmountValid.value) {
    emit("confirm", amount.value);
    amount.value = 0; // 重置输入金额
  }
};

// 当弹窗关闭时重置金额
watch(
  () => props.visible,
  newVal => {
    if (!newVal) {
      amount.value = 0;
    }
  }
);
</script>

<style scoped>
/* 遮罩层 - 从透明到半透明的过渡 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0);
  z-index: 1000;
  transition: background-color 0.3s ease;
  pointer-events: none; /* 初始状态不可点击 */
}

/* 遮罩层显示状态 */
.overlay-visible {
  background-color: rgba(0, 0, 0, 0.5);
  pointer-events: auto; /* 显示时可点击 */
}

/* 弹窗容器 - 初始在屏幕底部之外 */
.modal-container {
  position: fixed;
  left: 0;
  right: 0;
  bottom: -100%; /* 初始隐藏在底部 */
  background-color: #000;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  max-height: 80vh;
  transition: bottom 0.3s ease-out;
  z-index: 1001; /* 确保在遮罩层之上 */
}

/* 弹窗显示状态 - 滑入底部 */
.container-visible {
  bottom: 0;
}

/* 弹窗顶部区域 */
.modal-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #fff;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #f5f5f5;
}

/* 弹窗内容区 */
.modal-content {
  padding: 20px;
}

/* 余额信息 */
.balance-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 14px;
  color: #fff;
}

.available-amount {
  color: #ff9500;
  font-weight: 600;
  font-size: 16px;
}

/* 金额输入区域 */
.amount-input-container {
  display: flex;
  align-items: center;
  position: relative;
}

.currency-symbol {
  font-size: 28px;
  color: #333;
  margin-right: 12px;
}

.amount-input {
  flex: 1;
  height: 56px;
  width: 100%;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 0 16px;
  font-size: 28px;
  outline: none;
  transition: border-color 0.2s;
}

.amount-input:focus {
  border-color: #ff9500;
}

.all-btn {
  margin-left: 12px;
  padding: 10px 20px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  color: #ccc;
  cursor: pointer;
  transition: background-color 0.2s;
}

.all-btn:hover {
  background-color: #e5e5e5;
}

/* 底部按钮区域 */
.modal-footer {
  padding: 20px;
  border-top: 1px solid #f5f5f5;
}

/* 确认按钮 */
.confirm-btn {
  width: 100%;
  height: 52px;
  background-color: #ff9500;
  color: #fff;
  border: none;
  border-radius: 26px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.confirm-btn:disabled {
  background-color: #ffd6b3;
  cursor: not-allowed;
}

.confirm-btn:not(:disabled):hover {
  background-color: #e68a00;
}

@media screen and (min-width: 768px) {
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0);
    z-index: 1000;
    width: 450px;
    margin: 0 auto;
    transition: background-color 0.3s ease;
    pointer-events: none; /* 初始状态不可点击 */
  }

  /* 遮罩层显示状态 */
  .overlay-visible {
    background-color: rgba(0, 0, 0, 0.5);
    pointer-events: auto; /* 显示时可点击 */
  }

  /* 弹窗容器 - 初始在屏幕底部之外 */
  .modal-container {
    position: fixed;
    left: 0;
    right: 0;
    bottom: -100%; /* 初始隐藏在底部 */
    background-color: #000;
    border-top-left-radius: 20px;
    border-top-right-radius: 20px;
    max-height: 90vh;
    transition: bottom 0.3s ease-out;
    width: 450px;
    margin: 0 auto;
    z-index: 1001; /* 确保在遮罩层之上 */
  }

  /* 弹窗显示状态 - 滑入底部 */
  .container-visible {
    bottom: 0;
  }

  /* 弹窗顶部区域 */
  .modal-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #eee;
  }

  .modal-title {
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    margin: 0;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #fff;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: background-color 0.2s;
  }

  .close-btn:hover {
    background-color: #f5f5f5;
  }

  /* 弹窗内容区 */
  .modal-content {
    padding: 20px;
  }

  /* 余额信息 */
  .balance-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    font-size: 14px;
    color: #fff;
  }

  .available-amount {
    color: #ff9500;
    font-weight: 600;
    font-size: 16px;
  }

  /* 金额输入区域 */
  .amount-input-container {
    display: flex;
    align-items: center;
    position: relative;
  }

  .currency-symbol {
    font-size: 28px;
    color: #333;
    margin-right: 12px;
  }

  .amount-input {
    flex: 1;
    height: 56px;
    width: 100%;
    border: 1px solid #eee;
    border-radius: 10px;
    padding: 0 16px;
    font-size: 28px;
    outline: none;
    transition: border-color 0.2s;
  }

  .amount-input:focus {
    border-color: #ff9500;
  }

  .all-btn {
    margin-left: 12px;
    padding: 10px 20px;
    background-color: #f5f5f5;
    border: none;
    border-radius: 24px;
    font-size: 16px;
    color: #ccc;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .all-btn:hover {
    background-color: #e5e5e5;
  }

  /* 底部按钮区域 */
  .modal-footer {
    padding: 20px;
    border-top: 1px solid #f5f5f5;
  }

  /* 确认按钮 */
  .confirm-btn {
    width: 100%;
    height: 52px;
    background-color: #ff9500;
    color: #fff;
    border: none;
    border-radius: 26px;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .confirm-btn:disabled {
    background-color: #ffd6b3;
    cursor: not-allowed;
  }

  .confirm-btn:not(:disabled):hover {
    background-color: #e68a00;
  }
}
</style>
