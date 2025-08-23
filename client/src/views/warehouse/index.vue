<template>
  <div class="orders-page">
    <div class="p_header">
      <div class="title">
        <h2>{{ $t("拍卖纪录") }}</h2>
        <h3>{{ accountBalance }}</h3>
      </div>
      <div class="info">
        <p style="color: #ccc;">{{ $t("此款综将正式提供EBAY") }}</p>
        <p>{{ $t("剩余可用资金") }}($)</p>
      </div>
    </div>

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
    <div class="no-order" v-if="paginatedOrders.length===0">
      <p>{{ $t("该页面没有记录哦") }}</p>
    </div>
    <div v-else class="orders-list">
      <div v-for="(item, index) in paginatedOrders" :key="index" class="history-item">
        <div class="item-header">
          <div class="time-code">
            <span class="time">{{ $t("是时候赶紧下单了") }}: {{ item.createTime }}</span>
            <span class="code">{{ $t("订单号") }}: {{ item.id }}</span>
          </div>
        </div>

        <div class="product-info">
          <div class="product-name">
            <img :src="item.productImageUrl" alt />
            <div class="product-details">
              <div>{{ item.productName }}</div>
              <div class="product-price">
                ${{ item.unitPrice }}
                <span class="quantity">* {{ item.number }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="calculation">
          <div class="calc-row">
            <span>{{ t("订单总数") }}:</span>
            <span class="amount">${{ formatPrice(item.totalAmount) }}</span>
          </div>
          <div class="calc-row">
            <span>{{ t("委员会") }}:</span>
            <span class="amount">${{ item.profit }}</span>
          </div>
          <div class="calc-row">
            <span>{{ t("差额金额") }}:</span>
            <span class="amount">${{ formatPrice(item.balanceAmount) }}</span>
          </div>
          <div class="calc-row">
            <span>{{ t("预期回报") }}:</span>
            <span class="amount highlight">${{ item.refundAmount }}</span>
          </div>
        </div>

        <button
          v-if="item.processStatus === 'Waiting'"
          class="send-button"
          @click="Sendbutton(item.id)"
        >{{ t("提交订单") }}</button>
      </div>
    </div>
    <!-- 分页器 -->
    <div v-if="paginatedOrders.length!=0" class="pagination-wrapper">
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
    <ProductModal v-if="showModal" :id="ordderid" @close="showModal = false" @pay="handlePay" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
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

// tab 对应的接口参数
const tabStatusMap = {
  全部单: null,
  待处理: { processStatus: "Waiting" },
  已完成: { processStatus: "Success" },
  冻结中: { processStatus: "Frozen" } // 你可以根据后端真实字段改
};

const formatPrice = val => Number(val).toFixed(2);

function getAccountBalance() {
  getUserGradeAndBalanceAndDiscount().then(res => {
    order.value = res.data;
    accountBalance.value = res.data.userBalance;
  });
}

// 获取订单列表（根据 tab）
function getOrderlist() {
  const tabName = tabs[activeTab.value];
  const params = tabStatusMap[tabName] || {};
  console.log(params);
  getOrderList(params).then(res => {
    console.log(res);
    orders.value = res.rows;
  });
}

const Sendbutton = id => {
  ordderid.value = id;
  showModal.value = true;
};

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
          getAccountBalance();
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

// 分页
const currentPage = ref(1);
const pageSize = 20;

// 不再用 filteredOrders，因为数据直接由接口返回
const totalPages = computed(() => Math.ceil(orders.value.length / pageSize));
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return orders.value.slice(start, start + pageSize);
});
const totalOrders = computed(() => orders.value.length);

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};
const previousPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

// 监听 tab 切换，重新获取数据
watch(activeTab, () => {
  currentPage.value = 1;
  getOrderlist();
});

onMounted(() => {
  getAccountBalance();
  getOrderlist();
});
</script>


<style scoped>
.p_header {
  position: relative;
  background: url("../../assets/img/ddb4.b6d5e63.png") no-repeat;
  background-size: 100%;
  padding: 20px;
}

.p_header .title {
  width: 100%;
  display: flex;
  color: #fff;
  font-size: 16px;
  align-items: center;
  justify-content: space-between;
}

.p_header .info {
  width: 100%;
  display: flex;
  color: #fff;
  font-size: 10px;
  align-items: center;
  justify-content: space-between;
  margin-top: -28px;
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

@media screen and (min-width: 768px) {
  .orders-page {
    width: 450px;
    margin: 0 auto;
  }
  .p_header {
    position: relative;
    background: url("../../assets/img/ddb4.b6d5e63.png") no-repeat;
    background-size: 100%;
    padding: 20px;
  }

  .p_header .title {
    width: 100%;
    display: flex;
    color: #fff;
    font-size: 16px;
    align-items: center;
    justify-content: space-between;
  }

  .p_header .info {
    width: 100%;
    display: flex;
    color: #fff;
    font-size: 10px;
    align-items: center;
    justify-content: space-between;
    margin-top: -28px;
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
