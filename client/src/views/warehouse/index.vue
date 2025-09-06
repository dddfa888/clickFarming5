<template>
  <div class="orders-page">
    <!-- 顶部区域 -->
    <div class="p_header">
      <div class="header-top">
        <div class="app-name">快团</div>
        <div class="record-link" @click="router.push('/record')">拼团记录</div>
      </div>

      <div class="user-info-card">
        <div class="member-badge">
          <span>{{ userInfo.userLevel }}</span>
        </div>

        <div class="balance-info">
          <div class="balance-row">
            <div class="balance-label">余额</div>
            <div class="balance-value">{{ userInfo.userBalance || '0.00' }}</div>
          </div>
          <div class="balance-row">
            <div class="balance-label">佣金比例</div>
            <div class="balance-value">0.20%</div>
          </div>
        </div>

        <div class="daily-stats">
          <div class="stat-item">
            <div class="stat-label">今日佣金</div>
            <div class="stat-value">{{ userInfo.profit || '0.00' }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">今日已完成</div>
            <div class="stat-value">{{ userInfo.orderNum }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">冻结金额</div>
            <div class="stat-value">{{ userInfo.frozenAmount || '0.00' }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 补贴商品区域 -->
    <div class="subsidy-products">
      <div class="product-list" ref="productListRef">
        <div v-for="(item, index) in paginatedOrders" :key="index" class="product-item">
          <div class="product-left">
            <img class="product-image" :src="item.image" alt />
            <div class="product-name">
              <p>{{ item.username }}</p>
              <p class="product-tag">拼团次数{{ item.randomNum }}次商品</p>
            </div>
          </div>
          <div class="info">
            <button class="btn" @click="handleOrder">极速拼团</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 任务说明 -->
    <div class="task-description">
      <div class="task-title">任务说明</div>
      <div
        class="task-content"
      >为保障平台秩序、维护商家权益及用户资金安全，请在参与任务前认真阅读以下规定：一、提现规则 为防范洗钱等非法行为，白银会员需完成30单，黄金会员完成20单，单任务后方可申请提现。提现申请通过后资金T+0到账，预计24小时内，具体到账时间以银行为准。二、任务机制任务采用“垫付立返”模式,先付款后返佣。每日最多匹配0~3次高佣加急单，由系统随机派发，不可取消。每完成一单方可进行下一单，任务积极者有机会参与现金奖励抽取。三、信用分与异常高佣单接单后请及时参与，延误可能导致卡单并降低信用分，影响提现与任务匹配资格。四、账户规范每人最多注册两个账户，违规将冻结。多账户操作可能导致商家损失并影响平台运营。如有疑问，请联系平台客服。上海寻梦信息技术有限公司</div>
    </div>

    <ProductModal v-if="showModal" :id="id" @close="showModal = false" @pay="handlePay" />
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from "vue";
import {
  createOrder,
  sendDistribution,
  getUserGradeAndBalanceAndDiscount,
  getOrderByUser
} from "../../api/index";
import ProductModal from "../../components/ProductModal.vue";
import { showAlert } from "../../utils/notify";
import { useRouter } from "vue-router";
// 不再用 filteredOrders，因为数据直接由接口返回
const totalPages = computed(() => Math.ceil(orders.value.length / pageSize));
const paginatedOrders = ref([]);
const userInfo = ref({});

const showModal = ref(false);
const id = ref(null);
const router = useRouter();
const isProcessing = ref(false);

function debounce(fn, delay) {
  let timer = null;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

const handleOrder = () => {
  createOrder().then(res => {
    if (res.code === 200) {
      showModal.value = true;
      console.log(res.orderId);
      id.value = res.orderId;
      isProcessing.value = true;
    } else {
      isProcessing.value = false;
      showAlert(res.msg, 4000);
    }
  });
};

const handlePay = debounce(() => {
  console.log("handlePay");
  if (!id.value || !isProcessing.value) return;

  showModal.value = false;

  sendDistribution(id.value)
    .then(res => {
      if (res.code === 200) {
        showAlert("正在分发", 4000);

        setTimeout(() => {
          showAlert("订单支付成功！", 4000);
          isProcessing.value = false;
          handleRefresh();
        }, 4000);

        // // 更新数据
        // return getUserGradeAndBalanceAndDiscount().then(refreshRes => {
        //   order.value = refreshRes.data;
        // });
      } else {
        console.log(res.msg);
        showAlert(res.msg, 3000);
        isProcessing.value = false;
      }
    })
    .catch(() => {
      showAlert("支付请求失败", 4000);
      isProcessing.value = false;
    });
}, 2000);

function handleRefresh() {
  getUserGradeAndBalanceAndDiscount().then(res => {
    console.log(res.data);
    userInfo.value = res.data;
  });
}

onMounted(() => {
  handleRefresh();
  getOrderByUser().then(res => {
    console.log(res.data);
    paginatedOrders.value = res.data;
  });
});

const productListRef = ref(null);
let scrollTimer = null;

function startAutoScroll() {
  const el = productListRef.value;
  if (!el) return;

  scrollTimer = setInterval(() => {
    // 每次往下滚 1px
    el.scrollTop += 1;

    // 如果滚到底了，就回到顶部
    if (el.scrollTop + el.clientHeight >= el.scrollHeight) {
      el.scrollTop = 0;
    }
  }, 50); // 速度自己调，数值越小越快
}

onMounted(() => {
  getOrderByUser().then(res => {
    paginatedOrders.value = res.data || [];
    startAutoScroll();
  });
});

onUnmounted(() => {
  if (scrollTimer) clearInterval(scrollTimer);
});
</script>


<style scoped>
.orders-page {
  background: #f5f5f5;
  font-family: sans-serif;
  overflow: auto;
  padding-bottom: 90px; /* 为底部导航留出空间 */
  color: #333;
  height: 100vh;
  position: relative;
}

/* 顶部区域样式 */
.p_header {
  position: relative;
  background: linear-gradient(135deg, #ff5e62, #ff9966);
  background-size: 100%;
  padding: 15px;
  color: #fff;
  border-radius: 0 0 15px 15px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.app-name {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.record-link {
  font-size: 14px;
  color: #fff;
}

.user-info-card {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
  position: relative;
}

.member-badge {
  position: absolute;
  top: 15px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(90deg, #fff, #f0f0f0);
  border-radius: 15px;
  padding: 3px 12px;
  display: inline-block;
}

.member-badge span {
  color: #ff6b6b;
  font-size: 12px;
  font-weight: bold;
}

.balance-info {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  margin-bottom: 15px;
}

.balance-row {
  text-align: center;
  flex: 1;
}

.balance-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 5px;
}

.balance-value {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.daily-stats {
  display: flex;
  justify-content: space-between;
  border-radius: 8px;
  padding: 10px;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 3px;
}

.stat-value {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
}

/* 补贴商品区域样式 */
.subsidy-products {
  background-color: #fff;
  border-radius: 10px;
  margin: 15px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.subsidy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.subsidy-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.subsidy-stats {
  font-size: 12px;
  color: #ff6b6b;
}

.product-list {
  display: flex;
  flex-direction: column;
  max-height: 260px; /* 显示区域高度 */
  overflow: auto;
  position: relative;
}

.product-list::-webkit-scrollbar {
  display: none;
}

.product-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  font-size: 10px;
  height: 50px;
}

.product-left {
  height: 100%;
  display: flex;
  align-items: center;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 14px;
  font-weight: normal;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}
.product-name p {
  font-size: 10px;
}

.product-tag {
  display: inline-block;
  font-size: 10px;
  color: #ff6b6b;
  padding: 0;
  margin: 0;
  border: 1px solid #ff6b6b;
  border-radius: 4px;
}

.product-right {
  text-align: right;
}

.product-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.product-status {
  font-size: 12px;
  color: #ff6b6b;
  margin-bottom: 5px;
}

.action-button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 3px 8px;
  font-size: 12px;
  cursor: pointer;
}

.action-button.completed {
  background-color: #ffc107;
}

.no-order {
  text-align: center;
  padding: 20px;
  color: #999;
}

/* 任务说明区域 */
.task-description {
  background-color: #fff;
  border-radius: 10px;
  margin: 15px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.task-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.task-content {
  font-size: 12px;
  line-height: 1.5;
}

/* 底部导航栏 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  padding: 8px 0;
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.nav-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}

.home-icon {
  background-image: url("../../assets/img/home.svg");
}

.message-icon {
  background-image: url("../../assets/img/service.svg");
}

.task-icon {
  background-image: url("../../assets/img/warehouse-active.svg");
}

.web-icon {
  background-image: url("../../assets/img/center.svg");
}

.my-icon {
  background-image: url("../../assets/img/user.svg");
}

.nav-text {
  font-size: 12px;
  color: #999;
}

.nav-item.active .nav-text {
  color: #4285f4;
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

/* 状态栏样式 */
.status-bar {
  display: flex;
  justify-content: center;
  padding: 10px;
}

.time {
  font-weight: bold;
}

.status-icons {
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot,
.wifi,
.signal,
.battery {
  width: 15px;
  height: 15px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}

/* 标签样式 */
.status-tag {
  font-size: 10px;
  color: #ff6b6b;
  margin-bottom: 5px;
}

.status-tag.orange {
  color: #ffa500;
}

.join-btn {
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 3px 8px;
  font-size: 12px;
  cursor: pointer;
}

.btn {
  background-color: #ff6f61;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 5px 5px;
  font-size: 10px;
  margin-top: 10px;
}

/* 修改底部导航激活状态颜色 */
.nav-item.active .nav-text {
  color: #1e90ff;
}

.nav-item.active .nav-icon {
  color: #1e90ff;
}

@media screen and (min-width: 768px) {
  .orders-page {
    background: #f5f5f5;
    font-family: sans-serif;
    overflow: auto;
    padding-bottom: 40px; /* 为底部导航留出空间 */
    color: #333;
    height: 100vh;
    max-width: 450px;
    margin: 0 auto;
    position: relative;
  }

  /* 顶部区域样式 */
  .p_header {
    position: relative;
    background: linear-gradient(135deg, #ff5e62, #ff9966);
    background-size: 100%;
    padding: 15px;
    color: #fff;
    border-radius: 0 0 15px 15px;
  }

  .header-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }

  .app-name {
    font-size: 18px;
    font-weight: bold;
    color: #fff;
  }

  .record-link {
    font-size: 14px;
    color: #fff;
  }

  .user-info-card {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    padding: 15px;
    margin-bottom: 10px;
    position: relative;
  }

  .member-badge {
    position: absolute;
    top: 15px;
    left: 50%;
    transform: translateX(-50%);
    background: linear-gradient(90deg, #fff, #f0f0f0);
    border-radius: 15px;
    padding: 3px 12px;
    display: inline-block;
  }

  .member-badge span {
    color: #ff6b6b;
    font-size: 12px;
    font-weight: bold;
  }

  .balance-info {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    margin-bottom: 15px;
  }

  .balance-row {
    text-align: center;
    flex: 1;
  }

  .balance-label {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 5px;
  }

  .balance-value {
    font-size: 18px;
    font-weight: bold;
    color: #fff;
  }

  .daily-stats {
    display: flex;
    justify-content: space-between;
    border-radius: 8px;
    padding: 10px;
  }

  .stat-item {
    text-align: center;
    flex: 1;
  }

  .stat-label {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 3px;
  }

  .stat-value {
    font-size: 14px;
    font-weight: bold;
    color: #fff;
  }

  /* 补贴商品区域样式 */
  .subsidy-products {
    background-color: #fff;
    border-radius: 10px;
    margin: 15px;
    padding: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .subsidy-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }

  .subsidy-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
  }

  .subsidy-stats {
    font-size: 12px;
    color: #ff6b6b;
  }

  .product-list {
    display: flex;
    flex-direction: column;
    max-height: 260px; /* 显示区域高度 */
    overflow: auto;
    position: relative;
  }

  .product-list::-webkit-scrollbar {
    display: none;
  }

  .product-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #f0f0f0;
    font-size: 10px;
    height: 50px;
  }

  .product-left {
    height: 100%;
    display: flex;
    align-items: center;
  }

  .product-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 10px;
  }

  .product-info {
    display: flex;
    flex-direction: column;
  }

  .product-name {
    font-size: 14px;
    font-weight: normal;
    margin-bottom: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 150px;
  }
  .product-name p {
    font-size: 10px;
  }

  .product-tag {
    display: inline-block;
    font-size: 10px;
    color: #ff6b6b;
    padding: 0;
    margin: 0;
    border: 1px solid #ff6b6b;
    border-radius: 4px;
  }

  .product-right {
    text-align: right;
  }

  .product-time {
    font-size: 12px;
    color: #999;
    margin-bottom: 5px;
  }

  .product-status {
    font-size: 12px;
    color: #ff6b6b;
    margin-bottom: 5px;
  }

  .action-button {
    background-color: #ff6b6b;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 3px 8px;
    font-size: 12px;
    cursor: pointer;
  }

  .action-button.completed {
    background-color: #ffc107;
  }

  .no-order {
    text-align: center;
    padding: 20px;
    color: #999;
  }

  /* 任务说明区域 */
  .task-description {
    background-color: #fff;
    border-radius: 10px;
    margin: 15px;
    padding: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .task-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 10px;
  }

  .task-content {
    font-size: 12px;
    line-height: 1.5;
  }

  /* 底部导航栏 */
  .bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    justify-content: space-around;
    background-color: #fff;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    padding: 8px 0;
    z-index: 100;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
  }

  .nav-icon {
    width: 24px;
    height: 24px;
    margin-bottom: 4px;
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
  }

  .home-icon {
    background-image: url("../../assets/img/home.svg");
  }

  .message-icon {
    background-image: url("../../assets/img/service.svg");
  }

  .task-icon {
    background-image: url("../../assets/img/warehouse-active.svg");
  }

  .web-icon {
    background-image: url("../../assets/img/center.svg");
  }

  .my-icon {
    background-image: url("../../assets/img/user.svg");
  }

  .nav-text {
    font-size: 12px;
    color: #999;
  }

  .nav-item.active .nav-text {
    color: #4285f4;
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

  /* 状态栏样式 */
  .status-bar {
    display: flex;
    justify-content: center;
    padding: 10px;
  }

  .time {
    font-weight: bold;
  }

  .status-icons {
    display: flex;
    align-items: center;
    gap: 5px;
  }

  .dot,
  .wifi,
  .signal,
  .battery {
    width: 15px;
    height: 15px;
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
  }

  /* 标签样式 */
  .status-tag {
    font-size: 10px;
    color: #ff6b6b;
    margin-bottom: 5px;
  }

  .status-tag.orange {
    color: #ffa500;
  }

  .join-btn {
    background-color: #ff6b6b;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 3px 8px;
    font-size: 12px;
    cursor: pointer;
  }

  .btn {
    background-color: #ff6f61;
    color: #fff;
    border: none;
    border-radius: 5px;
    padding: 5px 5px;
    font-size: 10px;
    margin-top: 10px;
  }

  /* 修改底部导航激活状态颜色 */
  .nav-item.active .nav-text {
    color: #1e90ff;
  }

  .nav-item.active .nav-icon {
    color: #1e90ff;
  }
}
</style>
