<template>
  <div class="account-detail">
    <HeaderBar :title="t('账户详细信息')" />

    <!-- 搜索栏：两个日期输入框分别独立赋值 -->
    <div class="search-bar">
      <input type="text" v-model="leftInputValue" readonly @click="openPicker('left')" />
      <p>{{ $t("收信人") }}</p>
      <input type="text" v-model="rightInputValue" readonly @click="openPicker('right')" />
      <button class="search-btn">
        <img src="../../assets/img/shousuo.png" alt />
        {{ $t("搜索") }}
      </button>
    </div>

    <!-- 分类 Tabs -->
    <div class="tabs">
      <span
        v-for="tab in tabList"
        :key="tab.value"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >{{ $t(tab.label) }}</span>
    </div>

    <!-- 列表区域 -->
    <div class="record-list">
      <div v-for="(item, index) in filteredList" :key="index" class="record-item">
        <div>
          <p class="time">{{ item.time }}</p>
          <p :class="item.typeClass">{{ item.desc }}</p>
        </div>
        <p
          :class="item.amount > 0 ? 'green' : 'red'"
        >{{ item.amount > 0 ? '+' : '' }}{{ item.amount }}</p>
      </div>
    </div>

    <!-- 分页器 -->
    <div v-if="filteredList.length!=0" class="pagination-wrapper">
      <div class="pagination">
        <span
          class="pagination-info"
        >{{ t('paginationSummary', { total: totalOrders, size: pageSize }) }}</span>
        <div class="page-buttons">
          <button class="pagination-button" @click="previousPage" :disabled="currentPage === 1">«</button>
          <span class="page-number">{{ currentPage }} / {{ totalPages }}</span>
          <button
            class="pagination-button"
            @click="nextPage"
            :disabled="currentPage === totalPages"
          >»</button>
        </div>
      </div>
    </div>

    <!-- 弹框组件：日期选择器 -->
    <DatePicker
      v-if="showPicker"
      :visible="showPicker"
      @cancel="showPicker = false"
      @confirm="handleConfirm"
    />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import DatePicker from "../../components/DatePicker.vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";

// tab 配置
const tabList = [
  { label: "所有类型", value: "all" },
  { label: "充值金", value: "recharge" },
  { label: "取款记录", value: "withdraw" },
  { label: "充值记录", value: "deposit" }
];

// 当前激活的 tab
const activeTab = ref("all");
const { t } = useI18n();
const currentPage = ref(1); // 当前页码
const pageSize = ref(20); // 每页显示的记录数
const totalPages = computed(() =>
  Math.ceil(filteredList.value.length / pageSize.value)
); // 总页数

// 上一页
const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

// 模拟数据
const recordList = ref([
  {
    time: "2025-08-13 02:40",
    desc: "用戶接受訂單",
    amount: 750.0,
    type: "recharge",
    typeClass: "blue"
  },
  {
    time: "2025-08-13 02:40",
    desc: "收到訂單回扣",
    amount: 120.0,
    type: "deposit",
    typeClass: "red"
  },
  {
    time: "2025-08-13 02:35",
    desc: "用戶接受訂單",
    amount: -750.0,
    type: "withdraw",
    typeClass: "blue"
  },
  {
    time: "2025-08-13 02:35",
    desc: "收到訂單回扣",
    amount: 13.56,
    type: "deposit",
    typeClass: "red"
  }
]);

// 根据 tab 过滤数据
const filteredList = computed(() => {
  if (activeTab.value === "all") return recordList.value;
  return recordList.value.filter(item => item.type === activeTab.value);
});

// 日期选择相关变量
const showPicker = ref(false); // 控制日期选择器显示/隐藏
const currentInput = ref(""); // 记录当前点击的输入框（'left' 或 'right'）
const leftInputValue = ref(""); // 左侧输入框的值
const rightInputValue = ref(""); // 右侧输入框的值

// 打开日期选择器，记录当前点击的输入框
const openPicker = input => {
  currentInput.value = input; // 存储点击的是左侧还是右侧输入框
  showPicker.value = true; // 显示日期选择器
};

// 处理日期选择确认：根据当前输入框赋值
const handleConfirm = date => {
  if (currentInput.value === "left") {
    leftInputValue.value = date; // 给左侧输入框赋值
  } else if (currentInput.value === "right") {
    rightInputValue.value = date; // 给右侧输入框赋值
  }
  showPicker.value = false; // 关闭日期选择器
};
</script>

<style scoped>
.account-detail {
  background: #000;
  color: #fff;
  height: 100vh;
  font-size: 14px;
}
.header {
  display: flex;
  align-items: center;
  background: #1d1d1f;
  padding: 10px;
  font-weight: bold;
}
.back {
  margin-right: 10px;
}
.search-bar {
  display: flex;
  align-items: center;
  padding: 10px;
  padding-top: 60px;
}
.search-bar input {
  width: 20%;
  margin: 0 5px;
  padding: 6px 10px;
  border-radius: 20px;
  border: none;
  color: #000;
}
.search-btn {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  border: none;
  background: #444;
  color: #fff;
  border-radius: 20px;
  margin-left: 20px;
}
.search-bar img {
  width: 16px;
  text-align: center;
  margin-right: 4px;
}
.tabs {
  display: flex;
  justify-content: space-around;
  padding: 10px;
}
.tabs span {
  flex: 1;
  text-align: center;
  padding: 6px 0;
  cursor: pointer;
}
.tabs .active {
  color: orange;
  border-bottom: 2px solid orange;
}
.record-list {
  padding: 10px;
}
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #333;
}

.record-item .blue {
  color: #4a90e2;
  text-align: center;
}
.record-item .red {
  color: red;
  text-align: center;
}
.record-item .green {
  color: #0f0;
  text-align: center;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 10px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 480px;
}

.pagination-info {
  font-size: 12px;
  color: #888;
}

.page-buttons {
  display: flex;
  align-items: center;
}

.pagination-button {
  padding: 5px 10px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #ccc;
  margin: 0 5px;
  color: #000;
}

.pagination-button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
  color: #fff;
}

.page-number {
  font-size: 14px;
  color: #888;
}

@media screen and (min-width: 768px) {
  .account-detail {
    background: #000;
    color: #fff;
    width: 450px;
    margin: 0 auto !important;
    height: 100vh;
    font-size: 14px;
  }
  .header {
    display: flex;
    align-items: center;
    background: #1d1d1f;
    padding: 10px;
    font-weight: bold;
  }
  .back {
    margin-right: 10px;
  }
  .search-bar {
    display: flex;
    align-items: center;
    padding: 10px;
    padding-top: 60px;
  }
  .search-bar input {
    width: 20%;
    margin: 0 5px;
    padding: 6px 10px;
    border-radius: 20px;
    border: none;
    color: #000;
  }
  .search-btn {
    display: flex;
    align-items: center;
    padding: 6px 12px;
    border: none;
    background: #444;
    color: #fff;
    border-radius: 20px;
    margin-left: 40px;
  }
  .search-bar img {
    width: 16px;
    text-align: center;
    margin-right: 4px;
  }
  .tabs {
    display: flex;
    justify-content: space-around;
    padding: 10px;
  }
  .tabs span {
    flex: 1;
    text-align: center;
    padding: 6px 0;
    cursor: pointer;
  }
  .tabs .active {
    color: orange;
    border-bottom: 2px solid orange;
  }
  .record-list {
    padding: 10px;
  }
  .record-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .record-item .blue {
    color: #4a90e2;
    text-align: center;
  }
  .record-item .red {
    color: red;
    text-align: center;
  }
  .record-item .green {
    color: #0f0;
    text-align: center;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    padding: 10px;
  }

  .pagination {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    max-width: 480px;
  }

  .pagination-info {
    font-size: 12px;
    color: #888;
  }

  .page-buttons {
    display: flex;
    align-items: center;
  }

  .pagination-button {
    padding: 5px 10px;
    font-size: 14px;
    cursor: pointer;
    border: 1px solid #ccc;
    margin: 0 5px;
    color: #000;
  }

  .pagination-button:disabled {
    cursor: not-allowed;
    opacity: 0.5;
    color: #fff;
  }

  .page-number {
    font-size: 14px;
    color: #888;
  }
}
</style>