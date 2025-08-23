<template>
  <div class="order-page">
    <!-- 顶部标题栏 -->
    <HeaderBar :title="t('订单记录')" :backcolor="`#1D1D1F`" />

    <!-- Banner + 抢单按钮 -->
    <div class="banner-section">
      <p class="banner-title">HBO Max</p>
      <div class="box-list">
        <div v-for="(box, index) in boxes" :key="index" class="slot-box">
          <!-- 滚动容器 -->
          <div
            class="reel"
            :class="{ rolling: box.rolling }"
            :style="{
          transform: !box.rolling
            ? `translateY(-${box.finalIndex * 100}px)`
            : ''
        }"
          >
            <!-- 图片循环两遍，制造无限滚动感   -->
            <img v-for="(img, i) in [...imagePool, ...imagePool]" :key="i" :src="img" />
          </div>
        </div>
      </div>
      <button class="grab-btn" @click="Sendbutton">{{ btnText }}</button>
      <span class="hand">
        <img src="../../assets/img/download.png" alt />
      </span>
      <p class="banner-desc">{{ t('结果') }}</p>
    </div>

    <!-- 结果统计 -->
    <div class="result-tabs">
      <div class="tab-item">
        <p>{{ t("完整的") }}</p>
        <span>{{ order.orderNum }}</span>
      </div>
      <div class="tab-item">
        <p>{{ t("剩余") }}</p>
        <span>{{ order.remainingNum}}</span>
      </div>
      <div class="tab-item">
        <p>{{ t("已撤销") }}</p>
        <span>{{ order.cancel }}</span>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-card">
      <div class="stat-item">
        <div>
          <p style="color: #707070;font-weight: bold;">{{ t("获得佣金") }}</p>
          <span style="color: #f66">${{ order.profit }}</span>
        </div>
        <img src="../../assets/img/jinbi@2x.png" alt />
      </div>
      <div class="stat-item">
        <div>
          <p style="color: #707070;font-weight: bold;">{{ t("差额金额") }}</p>
          <span style="color: #f66">${{ formatPrice(order?.balanceAmount) }}</span>
        </div>
        <img src="../../assets/img/huise@2x.png" alt />
      </div>
      <div class="stat-item">
        <div>
          <p style="color: #707070;font-weight: bold;">{{ t("冻结金额") }}</p>
          <span style="color: #f66">$0.00</span>
        </div>
        <img src="../../assets/img/shoutidai@2x.png" alt />
      </div>
      <div class="stat-item">
        <div>
          <p style="color: #707070;font-weight: bold;">{{ t("订单数量") }}</p>
          <span style="color: #f66">{{ order.orderNum }}</span>
        </div>
        <img src="../../assets/img/qiandai@2x.png" alt />
      </div>
      <div class="stat-item">
        <div>
          <p style="color: #707070;font-weight: bold;">{{ t("今天的佣金") }}</p>
          <span style="color: #f66">${{ order.numToday }}</span>
        </div>
        <img src="../../assets/img/jinbi@2x.png" alt />
      </div>
      <div class="stat-item">
        <div>
          <p style="color: #707070;font-weight: bold;">{{ t("可用余额") }}</p>
          <span style="color: #f66">${{ order.userBalance }}</span>
        </div>
        <img src="../../assets/img/qiandai@2x.png" alt />
      </div>
    </div>

    <!-- 规则提示 -->
    <div class="rules-section">
      <div class="rules-title">{{ t("抢先订购指令") }}</div>
      <div class="rules-content">
        <p>1: {{$t("会员必须在可取款之前完成每日订单")}}。</p>
        <p>2: {{$t("系统使用LBS科技通过逻辑自动匹配产品")}}。</p>
        <p>3: {{$t("为了防止恶意作弊，会员必须在匹配后30分钟内确认提交订单，否则，您可能会被视为故意违规，从而失去会员资格")}}。</p>
      </div>
    </div>

    <!-- 商品弹窗 -->
    <ProductModal v-if="showModal" :id="id" @close="handleClose" @pay="handlePay" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
  createOrder,
  getUserGradeAndBalanceAndDiscount,
  sendDistribution
} from "../../api";
import { useI18n } from "vue-i18n";
import ProductModal from "../../components/ProductModal.vue";
import { useLangStore } from "../../store/useLangStore";
import { storeToRefs } from "pinia";
import { showAlert } from "../../utils/notify";
import router from "../../router/index.js";
import HeaderBar from "../../components/HeaderBar.vue";
import imgurl from "../../assets/img/black-mh.png";

const { t } = useI18n();
const order = ref({});
const showModal = ref(false);
const id = ref(null);
const langStore = useLangStore();
const { locale } = storeToRefs(langStore);
const userinfo = ref({});
const btnText = ref(t("开始抢订单"));
const imagePool = [
  new URL("../../assets/img/black-mh.png", import.meta.url).href,
  new URL("../../assets/img/black-mh.png", import.meta.url).href,
  new URL("../../assets/img/black-mh.png", import.meta.url).href,
  new URL("../../assets/img/black-mh.png", import.meta.url).href
];

const boxes = ref([
  { rolling: false, finalIndex: 0 },
  { rolling: false, finalIndex: 0 },
  { rolling: false, finalIndex: 0 }
]);

function formatPrice(val) {
  if (val == null || val === "" || isNaN(val)) {
    return "0.00";
  }
  return Number(val).toFixed(2);
}

function refreshData() {
  getUserGradeAndBalanceAndDiscount().then(res => {
    order.value = res.data;
  });
}

onMounted(() => {
  refreshData();
});

const formatCurrency = value => {
  if (typeof value !== "number") return "0 $";
  return value.toLocaleString("de-US", { style: "currency", currency: "USD" });
};

const Sendbutton = debounce(() => {
  if (!order.value.withdrawalAddress) {
    showAlert(t("地址未填写,请填写完整"), 4000);
    router.push({ path: "/address" });
    return;
  }

  // 启动老虎机动画
  boxes.value.forEach((box, i) => {
    box.rolling = true;
    box.finalIndex = Math.floor(Math.random() * imagePool.length);
    btnText.value = t("导入订单");

    setTimeout(() => {
      box.rolling = false;
      btnText.value = t("开始抢订单");
    }, 5000 + i * 1000); // 依次停
  });

  // 等待最后一个转轮停下再执行 createOrder
  const totalTime = 5000 + (boxes.value.length - 1) * 1000; // 最后一个停止的时间

  setTimeout(() => {
    createOrder()
      .then(res => {
        if (res.code === 200) {
          showModal.value = true;
          id.value = res.orderId;
        } else if (res.code === 5001) {
          showAlert(
            t("membership_requirement", {
              level: t(res.data.level),
              value: res.data.value
            }),
            4000
          );
        } else {
          showAlert(t(res.msg), 4000);
        }
      })
      .catch(() => {});
  }, totalTime);
}, 1000);

function debounce(fn, delay) {
  let timer = null;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

const handleClose = () => {
  showModal.value = false;
  refreshData();
};

const handlePay = debounce(() => {
  if (!id.value) return;

  showModal.value = false;

  sendDistribution(id.value)
    .then(res => {
      if (res.code === 200) {
        showAlert(t("正在分发"), 5000);
        setTimeout(() => {
          showAlert(t("订单支付成功！"), 6000);
        }, 5000);

        // 更新数据
        refreshData();
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
.order-page {
  background: #2b2b2b;
  min-height: 100vh;
  color: #fff;
  font-family: Arial, sans-serif;
  padding-bottom: 20px;
}

/* banner部分 */
.banner-section {
  text-align: center;
  padding: 20px 10px;
  background: #2b2b2b;
  background-image: url("../../assets/img/youxi-black.png");
  background-size: cover;
  position: relative;
  height: 325px;
}

.banner-title {
  font-size: 1.2rem;
  margin-top: 35px;
}
.banner-desc {
  font-size: 1.2rem;
  margin-top: 60px;
  font-weight: bold;
}

.box-list {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 40px 0 30px 0;
}

.slot-box {
  width: 100px;
  height: 100px;
  overflow: hidden;
  border-radius: 10px;
}

.reel {
  display: flex;
  flex-direction: column;
  transition: transform 1s ease-out; /* 停止时缓慢对齐 */
}

.reel img {
  width: 100%;
  height: 100px; /* 高度必须和 slot-box 一致 */
  object-fit: contain;
}

/* 无限滚动动画 */
.reel.rolling {
  animation: spin 0.3s linear infinite;
}

@keyframes spin {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-400px); /* imagePool.length * 100px */
  }
}

.grab-btn {
  background: #d4b981;
  color: #fff;
  font-size: 1rem;
  font-weight: bold;
  padding: 12px 80px;
  border-radius: 25px;
  border: none;
  cursor: pointer;
}

.hand {
  margin-left: 8px;
  position: absolute;
  right: 65px;
  bottom: 40px;
}

.hand img {
  width: 60px;
  height: 40px;
  animation: floatTap 2.5s infinite ease-in-out;
  transform-origin: center bottom; /* 以底部为旋转基准，更像手指点击 */
}

@keyframes floatTap {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  20% {
    transform: translate(-20px, -20px) rotate(0deg); /* 往左上移动 */
  }
  30% {
    transform: translate(-20px, -20px) rotate(-15deg); /* 第一次点击（轻微旋转） */
  }
  40% {
    transform: translate(-20px, -20px) rotate(0deg);
  }
  50% {
    transform: translate(-20px, -20px) rotate(-15deg); /* 第二次点击 */
  }
  60% {
    transform: translate(-20px, -20px) rotate(0deg);
  }
  100% {
    transform: translate(0, 0) rotate(0deg); /* 回到原位 */
  }
}

/* 结果统计 */
.result-tabs {
  display: flex;
  justify-content: space-around;
  margin: 15px;
  margin-top: 15px;
  background: #ffeed7;
  border-radius: 10px;
  color: #2b2b2b;
  text-align: center;
  font-weight: bold;
}

.tab-item p {
  font-size: 0.85rem;
  margin-bottom: 5px;
}

.tab-item span {
  font-size: 1rem;
  font-weight: bold;
  color: #f66;
}

/* 结果统计 */
.result-tabs {
  display: flex;
  justify-content: space-around;
  padding: 15px;
  margin-top: 15px;
  background: #ffeed7;
  border-radius: 10px;
  color: #707070;
  text-align: center;
}

.tab-item p {
  font-size: 0.85rem;
  margin-bottom: 5px;
}

.tab-item span {
  font-size: 1rem;
  font-weight: bold;
  color: #f66;
}

/* 数据统计卡片 */
.stats-card {
  background: #f5e3c6;
  color: #000;
  border-radius: 10px;
  margin: 15px;
  padding: 15px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
}

.stat-item img {
  width: 48px;
  height: 50px;
}

.stat-item p {
  font-size: 0.85rem;
  margin: 0;
}

.stat-item span {
  font-size: 1rem;
  font-weight: bold;
}

/* 规则提示 */
.rules-section {
  background: #f5e3c6;
  color: #000;
  border-radius: 10px;
  margin: 15px;
  overflow: hidden;
  margin-bottom: 50px;
}

.rules-title {
  background: #d6b679;
  padding: 8px 10px;
  font-weight: bold;
  font-size: 0.9rem;
  color: #8d8d8d;
}

.rules-content {
  padding: 10px;
  font-size: 0.85rem;
  line-height: 1.5;
}

@media screen and (min-width: 768px) {
  .order-page {
    background: #2b2b2b;
    width: 450px;
    margin: 0 auto;
    min-height: 100vh;
    color: #fff;
    font-family: Arial, sans-serif;
    padding-bottom: 20px;
  }

  /* banner部分 */
  .banner-section {
    text-align: center;
    padding: 20px 10px;
    background: #2b2b2b;
    background-image: url("../../assets/img/youxi-black.png");
    background-size: cover;
    position: relative;
    height: 325px;
  }

  .banner-title {
    font-size: 1.2rem;
    margin-top: 35px;
  }
  .banner-desc {
    font-size: 1.2rem;
    margin-top: 60px;
    font-weight: bold;
  }

  .box-list {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin: 60px 0 10px 0;
  }

  .slot-box {
    width: 100px;
    height: 100px;
    overflow: hidden;
    border-radius: 10px;
  }

  .reel {
    display: flex;
    flex-direction: column;
    transition: transform 1s ease-out; /* 停止时缓慢对齐 */
  }

  .reel img {
    width: 100%;
    height: 100px; /* 高度必须和 slot-box 一致 */
    object-fit: contain;
  }

  /* 无限滚动动画 */
  .reel.rolling {
    animation: spin 0.3s linear infinite;
  }

  @keyframes spin {
    from {
      transform: translateY(0);
    }
    to {
      transform: translateY(-400px); /* imagePool.length * 100px */
    }
  }

  .grab-btn {
    background: #d4b981;
    color: #fff;
    font-size: 1rem;
    font-weight: bold;
    padding: 12px 80px;
    border-radius: 25px;
    border: none;
    cursor: pointer;
  }

  .hand {
    margin-left: 8px;
    position: absolute;
    right: 65px;
    bottom: 40px;
  }

  .hand img {
    width: 60px;
    height: 40px;
    animation: floatTap 2.5s infinite ease-in-out;
    transform-origin: center bottom; /* 以底部为旋转基准，更像手指点击 */
  }

  @keyframes floatTap {
    0% {
      transform: translate(0, 0) rotate(0deg);
    }
    20% {
      transform: translate(-20px, -20px) rotate(0deg); /* 往左上移动 */
    }
    30% {
      transform: translate(-20px, -20px) rotate(-15deg); /* 第一次点击（轻微旋转） */
    }
    40% {
      transform: translate(-20px, -20px) rotate(0deg);
    }
    50% {
      transform: translate(-20px, -20px) rotate(-15deg); /* 第二次点击 */
    }
    60% {
      transform: translate(-20px, -20px) rotate(0deg);
    }
    100% {
      transform: translate(0, 0) rotate(0deg); /* 回到原位 */
    }
  }

  /* 结果统计 */
  .result-tabs {
    display: flex;
    justify-content: space-around;
    margin: 15px;
    margin-top: 15px;
    background: #ffeed7;
    border-radius: 10px;
    color: #2b2b2b;
    text-align: center;
    font-weight: bold;
  }

  .tab-item p {
    font-size: 0.85rem;
    margin-bottom: 5px;
  }

  .tab-item span {
    font-size: 1rem;
    font-weight: bold;
    color: #f66;
  }

  /* 结果统计 */
  .result-tabs {
    display: flex;
    justify-content: space-around;
    padding: 15px;
    margin-top: 15px;
    background: #ffeed7;
    border-radius: 10px;
    color: #707070;
    text-align: center;
  }

  .tab-item p {
    font-size: 0.85rem;
    margin-bottom: 5px;
  }

  .tab-item span {
    font-size: 1rem;
    font-weight: bold;
    color: #f66;
  }

  /* 数据统计卡片 */
  .stats-card {
    background: #f5e3c6;
    color: #000;
    border-radius: 10px;
    margin: 15px;
    padding: 15px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
  }

  .stat-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
  }

  .stat-item img {
    width: 48px;
    height: 50px;
  }

  .stat-item p {
    font-size: 0.85rem;
    margin: 0;
  }

  .stat-item span {
    font-size: 1rem;
    font-weight: bold;
  }

  /* 规则提示 */
  .rules-section {
    background: #f5e3c6;
    color: #000;
    border-radius: 10px;
    margin: 15px;
    overflow: hidden;
    margin-bottom: 50px;
  }

  .rules-title {
    background: #d6b679;
    padding: 8px 10px;
    font-weight: bold;
    font-size: 0.9rem;
    color: #8d8d8d;
  }

  .rules-content {
    padding: 10px;
    font-size: 0.85rem;
    line-height: 1.5;
  }
}
</style>
