<template>
  <div class="orders-page" ref="listRef" @scroll.passive="handleScroll">
    <HeaderBar :title="t('拼团记录')" backcolor="#e4e2e2" />

    <!-- 顶部Tab切换 -->
    <div class="tabs">
      <div
        v-for="(tab, index) in tabs"
        :key="tab"
        :class="['tab', { active: activeTab === index }]"
        @click="activeTab = index"
      >{{ $t(tab) }}</div>
    </div>

    <!-- 内容区 -->
    <div class="no-order" v-if="orders.length === 0">
      <p>{{ $t("该页面没有记录哦") }}</p>
    </div>

    <div v-else class="orders-list">
      <div v-for="(item, index) in orders" :key="index" class="history-item">
        <div class="item-header">
          <div class="time-code">
            <span class="code">{{ $t("订单号") }}: {{ item.id }}</span>
            <span style="color:red;">{{ $t("已完成") }}</span>
          </div>
        </div>

        <div class="product-info">
          <div class="product-name">
            <img :src="item.productImageUrl" alt />
            <div class="product-details">
              <div>{{ item.productName }}</div>
              <div class="product-price">
                {{ item.unitPrice }}
                <span class="quantity">x{{ item.number }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="calculation">
          <div class="calc-row">
            <span>{{ t("订单总额") }}:</span>
            <span class="amount">${{ formatPrice(item.totalAmount) }}</span>
          </div>
          <div class="calc-row">
            <span>{{ t("返现") }}:</span>
            <span class="amount">${{ formatPrice(item.profit) }}</span>
          </div>
          <div class="calc-row">
            <span>{{ t("合计金额") }}:</span>
            <span
              class="amount"
            >${{ formatPrice(item.totalAmount) }} +{{ formatPrice(item.profit) }}</span>
          </div>
          <div class="calc-row">
            <span>{{ t("订单日期") }}:</span>
            <span class="amount">{{ item.createTime}}</span>
          </div>
        </div>

        <button
          v-if="item.processStatus === 'Waiting'"
          class="send-button"
          @click="Sendbutton(item.id)"
        >提交订单</button>
      </div>
    </div>

    <!-- 订单详情模态框 -->
    <ProductModal v-if="showModal" :id="ordderid" @close="showModal = false" @pay="handlePay" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
import HeaderBar from "../../components/HeaderBar.vue";
import {
  getOrderList,
  sendDistribution,
  getUserGradeAndBalanceAndDiscount
} from "../../api/index";
import ProductModal from "../../components/ProductModal.vue";
import { showAlert } from "../../utils/notify";

const { t } = useI18n();

// 标签和当前选择
const tabs = ["全部单", "待处理", "已完成", "冻结中"];
const activeTab = ref(0);
const accountBalance = ref();
const showModal = ref(false);
const ordderid = ref();
const order = ref({});
const orders = ref([]);
const paginatedOrders = ref([]);
const loading = ref(false);
const noMore = ref(false);
const currentPage = ref(1);
const pageSize = 20;

// tab 对应的接口参数
const tabStatusMap = {
  全部单: null,
  待处理: { processStatus: "Waiting" },
  已完成: { processStatus: "Success" },
  冻结中: { processStatus: "Frozen" } // 后端字段根据实际情况调整
};

const formatPrice = val => Number(val).toFixed(2);

// 获取订单列表（支持分页）
async function getOrderlist(loadMore = false) {
  if (loading.value || noMore.value) return;
  loading.value = true;

  const tabName = tabs[activeTab.value];
  const params = {
    pageNum: currentPage.value,
    pageSize,
    ...(tabStatusMap[tabName] || {})
  };

  try {
    const res = await getOrderList(params);

    if (res.rows.length < pageSize) {
      noMore.value = true;
    }

    if (loadMore) {
      orders.value = [...orders.value, ...res.rows];
    } else {
      orders.value = res.rows;
    }
  } finally {
    loading.value = false;
  }
}

// 滚动到底部时加载下一页
function handleScroll() {
  const el = listRef.value;
  if (!el) return;

  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 20) {
    currentPage.value++;
    getOrderlist(true);
  }
}

// tab 切换时重新加载
watch(activeTab, () => {
  currentPage.value = 1;
  noMore.value = false;
  getOrderlist();
});

onMounted(() => {
  getOrderlist();
});

// 提交订单
const Sendbutton = id => {
  ordderid.value = id;
  showModal.value = true;
};

// 支付方法（防止多次点击）
function debounce(fn, delay) {
  let timer = null;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

const handlePay = debounce(() => {
  if (!ordderid.value) return;

  showModal.value = false;
  sendDistribution(ordderid.value)
    .then(res => {
      if (res.code === 200) {
        showAlert(t("正在分发"), 5000);
        setTimeout(() => {
          showAlert(t("订单支付成功！"), 6000);
          getOrderlist();
        }, 5000);
      } else {
        showAlert(t(res.msg), 4000);
      }
    })
    .catch(() => {
      showAlert(t("支付请求失败"), 4000);
    });
}, 1000);
</script>



<style scoped>
.orders-page {
  background: #ecebf0;
  font-family: sans-serif;
  overflow: auto;
  padding-bottom: 100px;
  color: #000;
  height: 100vh;
}

.balance {
  font-size: 14px;
  margin-bottom: 10px;
}

.tabs {
  display: flex;
  background: #ecebf0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
  margin-top: 50px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  font-size: 14px;
  color: #000;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.tab.active {
  color: #1296db;
}

.no-order {
  width: 35vw;
  margin: 0 auto;
}
.no-order img {
  width: 100%;
}
.no-order p {
  color: #888;
  text-align: center;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px;
}

.history-item {
  padding: 15px;
  background-color: #fff;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  color: #898787;
}

.item-header {
  margin-bottom: 15px;
}

.time-code {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}

.time {
  margin-bottom: 3px;
}

.product-info {
  margin-bottom: 15px;
}

.product-name {
  font-weight: bold;
  margin-bottom: 8px;
  line-height: 1.4;
  display: flex;
  align-items: center;
  background-color: #fff;
  color: #333;
}

.product-name > img {
  width: 90px;
  height: 90px;
  margin-right: 10px;
}

.product-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: column;
}

.product-title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 5px;
}

.product-price {
  width: 100%;
  display: flex;
  font-size: 14px;
  margin-bottom: 5px;
  align-items: center;
  color: red;
  justify-content: space-between;
}

.quantity {
  color: #000;
  font-size: 14px;
}

.calculation {
  margin: 15px 0;
  padding: 15px;
  background-color: #fff;
  color: #898787;
  border-radius: 6px;
}

.calculation {
  margin: 15px 0;
  padding: 15px;
  background-color: transparent;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 6px;
}

.calc-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.calc-row:last-child {
  margin-bottom: 0;
}

.highlight {
  color: #fde0a3;
  font-size: 26px;
}

.send-button {
  width: 30%;
  padding: 5px;
  font-size: 12px;
  background-color: #026274;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
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
  .orders-page {
    width: 450px;
    margin: 0 auto;
  }
  .orders-page {
    background: #1e201f;
    font-family: sans-serif;
    overflow: auto;
    padding-bottom: 100px;
    color: #fff;
    height: 100vh;
  }

  .balance {
    font-size: 14px;
    margin-bottom: 10px;
  }

  .tabs {
    display: flex;
    background: #1e201f;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 10px;
    width: 450px;
    margin: 0 auto;
  }

  .tab {
    flex: 1;
    text-align: center;
    padding: 10px 0;
    font-size: 14px;
    color: #fff;
    cursor: pointer;
    border-bottom: 2px solid transparent;
  }

  .tab.active {
    color: #cf9e63;
    font-weight: bold;
    border-bottom: 2px solid #cf9e63;
  }

  .no-order {
    width: 150px;
    margin: 0 auto;
  }
  .no-order img {
    width: 100%;
  }
  .no-order p {
    color: #888;
    text-align: center;
  }

  .orders-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 10px;
  }

  .history-item {
    padding: 15px;
    background-color: #252726;
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    color: #898787;
  }

  .item-header {
    margin-bottom: 15px;
  }

  .time-code {
    display: flex;
    flex-direction: column;
    font-size: 14px;
  }

  .time {
    margin-bottom: 3px;
  }

  .product-info {
    margin-bottom: 15px;
  }

  .product-name {
    font-weight: bold;
    margin-bottom: 8px;
    line-height: 1.4;
    display: flex;
    align-items: center;
    background-color: #fff;
    color: #333;
  }

  .product-name > img {
    width: 90px;
    height: 90px;
    margin-right: 10px;
  }

  .product-details {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-direction: column;
  }

  .product-title {
    font-weight: bold;
    font-size: 14px;
    margin-bottom: 5px;
  }

  .product-price {
    width: 100%;
    display: flex;
    font-weight: bold;
    font-size: 14px;
    margin-bottom: 5px;
    align-items: center;
    justify-content: space-between;
  }

  .quantity {
    font-size: 14px;
  }

  .calculation {
    margin: 15px 0;
    padding: 15px;
    background-color: #fff;
    color: #898787;
    border-radius: 6px;
  }

  .calculation {
    margin: 15px 0;
    padding: 15px;
    background-color: transparent;
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    border-radius: 6px;
  }

  .calc-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
  }

  .calc-row:last-child {
    margin-bottom: 0;
  }

  .highlight {
    color: #fde0a3;
    font-size: 26px;
  }

  .send-button {
    width: 30%;
    padding: 5px;
    font-size: 12px;
    background-color: #026274;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
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
