<template>
  <div class="tab-container">
    <!-- 头部区域 -->
    <HeaderBar :title="t('余额宝记录')" :backcolor="'#f0e68c'" @back="handleBack" />

    <!-- 标签栏区域 -->
    <div class="tab-bar">
      <div
        v-for="(tab, index) in tabs"
        :key="index"
        class="tab-item"
        :class="{ active: activeTab === index }"
        @click="switchTab(index)"
      >
        {{ tab }}
        <div class="tab-indicator" v-if="activeTab === index"></div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="tab-content">
      <transition name="fade" mode="out-in">
        <!-- 用一个根div包裹所有条件渲染的内容，解决Transition只能有一个子元素的问题 -->
        <div key="content-wrapper">
          <!-- 收益记录 -->
          <div v-if="activeTab === 0" class="tab-pane">
            <div v-if="incomeRecords.length > 0" class="record-list">
              <div class="record-item" v-for="item in incomeRecords" :key="item.id">
                <div class="record-icon income-icon">+</div>
                <div class="record-info">
                  <div class="record-title">{{ item.title }}</div>
                  <div class="record-date">{{ item.date }}</div>
                </div>
                <div class="record-amount income-amount">+{{ item.amount }}元</div>
              </div>
            </div>
          </div>

          <!-- 转入记录 -->
          <div v-if="activeTab === 1" class="tab-pane">
            <div v-if="transferInRecords.length > 0" class="record-list">
              <div class="record-item" v-for="item in transferInRecords" :key="item.id">
                <div class="record-icon transfer-in-icon">↓</div>
                <div class="record-info">
                  <div class="record-title">{{ item.title }}</div>
                  <div class="record-date">{{ item.date }}</div>
                </div>
                <div class="record-amount transfer-in-amount">+{{ item.amount }}元</div>
              </div>
            </div>
          </div>

          <!-- 转出记录 -->
          <div v-if="activeTab === 2" class="tab-pane">
            <div v-if="transferOutRecords.length > 0" class="record-list">
              <div class="record-item" v-for="item in transferOutRecords" :key="item.id">
                <div class="record-icon transfer-out-icon">↑</div>
                <div class="record-info">
                  <div class="record-title">{{ item.title }}</div>
                  <div class="record-date">{{ item.date }}</div>
                </div>
                <div class="record-amount transfer-out-amount">-{{ item.amount }}元</div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>
    

<script setup>
import { ref } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

// 定义标签
const tabs = [t("收益"), t("余额转入"), t("转出到余额")];
// 当前激活的标签索引
const activeTab = ref(0);

// 切换标签
const switchTab = index => {
  activeTab.value = index;
};

// 返回按钮处理
const handleBack = () => {
  // 实际应用中可以使用路由返回
  // router.back();
  console.log("返回上一页");
};

// 模拟数据 - 收益记录
const incomeRecords = [
  { id: 1, title: t("余额宝收益"), date: t("今天 03:00"), amount: "0.45" },
  { id: 2, title: t("余额宝收益"), date: t("昨天 03:00"), amount: "0.32" },
  { id: 3, title: t("余额宝收益"), date: "08-28 03:00", amount: "0.51" },
  { id: 4, title: t("余额宝收益"), date: "08-27 03:00", amount: "0.48" }
];

// 模拟数据 - 转入记录
const transferInRecords = [
  { id: 1, title: t("银行卡转入"), date: "08-25 14:30", amount: "1000.00" },
  { id: 2, title: t("余额转入"), date: "08-20 09:15", amount: "500.00" },
  { id: 3, title: t("银行卡转入"), date: "08-15 16:45", amount: "2000.00" }
];

// 模拟数据 - 转出记录
const transferOutRecords = [
  { id: 1, title: t("转出到银行卡"), date: "08-22 11:20", amount: "300.00" },
  { id: 2, title: t("转出到余额"), date: "08-18 15:10", amount: "200.00" }
];
</script>

<style scoped>
.tab-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

/* 标签栏样式 */
.tab-bar {
  display: flex;
  background-color: #fff;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  margin-top: 50px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 5px 0;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  position: relative;
  transition: color 0.3s, transform 0.2s;
}

.tab-item.active {
  color: #000;
  font-weight: 500;
}

.tab-item:active {
  transform: scale(0.95);
}

.tab-indicator {
  position: absolute;
  bottom: -13px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background-color: #f0c14b;
  border-radius: 2px;
  transition: width 0.3s;
}

.tab-item.active .tab-indicator {
  animation: indicatorPulse 0.3s;
}

/* 内容区域样式 */
.tab-content {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
}

.tab-pane {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

/* 记录列表样式 */
.record-list {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  flex: 1;
}

.record-item {
  display: flex;
  align-items: center;
  padding: 15px 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: background-color 0.2s;
}

.record-item:last-child {
  border-bottom: none;
}

.record-item:active {
  background-color: #f9f9f9;
}

.record-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  margin-right: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.income-icon {
  background-color: #4cd964;
}

.transfer-in-icon {
  background-color: #5ac8fa;
}

.transfer-out-icon {
  background-color: #ff9500;
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 16px;
  color: #000;
  margin-bottom: 4px;
}

.record-date {
  font-size: 12px;
  color: #999;
}

.record-amount {
  font-size: 16px;
  font-weight: 500;
}

.income-amount,
.transfer-in-amount {
  color: #4cd964;
}

.transfer-out-amount {
  color: #ff3b30;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
}

/* 无数据样式 */
.no-data {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;
  padding: 20px;
}

.no-data-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
  opacity: 0.6;
}

/* 动画 */
@keyframes indicatorPulse {
  0% {
    width: 12px;
  }
  50% {
    width: 30px;
  }
  100% {
    width: 24px;
  }
}

/* 适配不同屏幕 */
@media (max-width: 320px) {
  .tab-item {
    font-size: 14px;
  }

  .record-title,
  .record-amount {
    font-size: 14px;
  }
}

@media screen and (min-width: 768px) {
  .tab-container {
    margin: 0 auto;
    max-width: 450px;
    background-color: #f5f5f5;
    min-height: 100vh;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
  }

  /* 标签栏样式 */
  .tab-bar {
    display: flex;
    background-color: #fff;
    padding: 12px 0;
    border-bottom: 1px solid #eee;
    margin-top: 50px;
  }

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 5px 0;
    cursor: pointer;
    font-size: 16px;
    color: #666;
    position: relative;
    transition: color 0.3s, transform 0.2s;
  }

  .tab-item.active {
    color: #000;
    font-weight: 500;
  }

  .tab-item:active {
    transform: scale(0.95);
  }

  .tab-indicator {
    position: absolute;
    bottom: -13px;
    left: 50%;
    transform: translateX(-50%);
    width: 24px;
    height: 3px;
    background-color: #f0c14b;
    border-radius: 2px;
    transition: width 0.3s;
  }

  .tab-item.active .tab-indicator {
    animation: indicatorPulse 0.3s;
  }

  /* 内容区域样式 */
  .tab-content {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
  }

  .tab-pane {
    min-height: 100%;
    display: flex;
    flex-direction: column;
  }

  /* 记录列表样式 */
  .record-list {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    flex: 1;
  }

  .record-item {
    display: flex;
    align-items: center;
    padding: 15px 16px;
    border-bottom: 1px solid #f5f5f5;
    transition: background-color 0.2s;
  }

  .record-item:last-child {
    border-bottom: none;
  }

  .record-item:active {
    background-color: #f9f9f9;
  }

  .record-icon {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    color: #fff;
    margin-right: 12px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }

  .income-icon {
    background-color: #4cd964;
  }

  .transfer-in-icon {
    background-color: #5ac8fa;
  }

  .transfer-out-icon {
    background-color: #ff9500;
  }

  .record-info {
    flex: 1;
  }

  .record-title {
    font-size: 16px;
    color: #000;
    margin-bottom: 4px;
  }

  .record-date {
    font-size: 12px;
    color: #999;
  }

  .record-amount {
    font-size: 16px;
    font-weight: 500;
  }

  .income-amount,
  .transfer-in-amount {
    color: #4cd964;
  }

  .transfer-out-amount {
    color: #ff3b30;
  }

  /* 过渡动画 */
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.3s, transform 0.3s;
  }

  .fade-enter {
    opacity: 0;
    transform: translateY(10px);
  }

  .fade-leave-to {
    opacity: 0;
  }

  /* 无数据样式 */
  .no-data {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #999;
    padding: 20px;
  }

  .no-data-icon {
    width: 80px;
    height: 80px;
    margin-bottom: 15px;
    opacity: 0.6;
  }

  /* 动画 */
  @keyframes indicatorPulse {
    0% {
      width: 12px;
    }
    50% {
      width: 30px;
    }
    100% {
      width: 24px;
    }
  }
}
</style>
    