<template>
  <div class="ecommerce-container">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-tabs">
        <div
          v-for="(tab, index) in tabs"
          :key="index"
          class="nav-tab"
          :class="{ active: activeTabIndex === index }"
          @click="switchTab(index)"
        >{{ tab.name }}</div>
        <div @click="router.push('/income')" class="nav-tab-more">
          <img src="../../assets/image/nav_tb.svg" alt />
        </div>
      </div>
    </div>

    <!-- 会员福利横幅 -->
    <div class="member-banner">
      <img src="../../assets/image/back.png" alt="会员福利" />
    </div>

    <!-- 应用图标区 -->
    <div class="app-icons">
      <div class="app-icon-item">
        <div class="app-icon taobao">
          <img src="../../assets/image/tb.png" alt="淘宝" />
        </div>
        <div class="app-name">淘宝</div>
      </div>
      <div class="app-icon-item">
        <div class="app-icon pinduoduo">
          <img src="../../assets/image/pdd.png" alt="拼多多" />
        </div>
        <div class="app-name">拼多多</div>
      </div>
      <div class="app-icon-item">
        <div class="app-icon vip">
          <img src="../../assets/image/wph.png" alt="唯品会" />
        </div>
        <div class="app-name">唯品会</div>
      </div>
      <div class="app-icon-item">
        <div class="app-icon jd">
          <img src="../../assets/image/jd.png" alt="京东" />
        </div>
        <div class="app-name">京东</div>
      </div>
      <div class="app-icon-item">
        <div class="app-icon tmall">
          <img src="../../assets/image/tm.png" alt="天猫" />
        </div>
        <div class="app-name">天猫</div>
      </div>
    </div>

    <!-- 收益排行 -->
    <div class="earnings-ranking" @click="router.push('/income')">
      <div class="ranking-title">
        <img src="../../assets/image/syph.png" alt />
      </div>
      <div class="ranking-list">
        <div class="ranking-item">
          <div class="user-avatar">
            <img src="../../assets/avatars/1.png" alt="用户头像" />
          </div>
          <div class="user-name">大山</div>
          <div class="user-earnings">+213.36</div>
          <div class="earnings-date">2025-08-28</div>
          <div class="arrow-right">
            <img src="../../assets/img/right.png" alt />
          </div>
        </div>
        <div class="ranking-item">
          <div class="user-avatar">
            <img src="../../assets/avatars/12.png" alt="用户头像" />
          </div>
          <div class="user-name">王强</div>
          <div class="user-earnings">+192.96</div>
          <div class="earnings-date">2025-08-28</div>
          <div class="arrow-right">
            <img src="../../assets/img/right.png" alt />
          </div>
        </div>
      </div>
    </div>

    <!-- 商品展示区 -->
    <div class="product-showcase">
      <div v-for="(product, index) in currentProducts" :key="index" class="product-item">
        <div class="product-image">
          <img :src="product.imageUrl" :alt="product.name" />
        </div>
        <div class="product-tag">{{ t("热卖") }}</div>
        <div class="product-delivery">{{ product.delivery }}</div>
        <div class="product-name">
          {{ product.productName
          }}
        </div>
        <div class="product-price">¥{{ product.price }}</div>
        <div class="product-market-price">市场价¥{{ product.marketPrice }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { getProductList } from "../../api/index";
import { useI18n } from "vue-i18n";

const router = useRouter();
const { t } = useI18n();

// 定义标签页
const tabs = [
  { name: "全部" },
  { name: "淘宝" },
  { name: "拼多多" },
  { name: "唯品会" },
  { name: "京东" },
  { name: "天猫" }
];

// 当前激活的标签页索引
const activeTabIndex = ref(0);

// 定义不同平台的商品数据
const allProducts = {
  all: [
    {
      id: 1,
      name: "Dior迪奥女士太阳镜女...",
      brand: "DIOR",
      tag: "热卖",
      delivery: "1h可退欧洲直邮",
      price: "2657.00",
      marketPrice: "2657.00",
      image: "../../assets/img/banner1.jpg"
    },
    {
      id: 2,
      name: "能10万水精华水浓萃型...",
      brand: "DIOR",
      tag: "热卖",
      delivery: "Dior迪奥肌活蘊",
      price: "685.00",
      marketPrice: "685.00",
      image: "../../assets/img/banner2.jpg"
    }
  ],
  taobao: [
    {
      id: 3,
      name: "淘宝特惠商品限时抢购...",
      brand: "淘宝",
      tag: "特惠",
      delivery: "24小时发货",
      price: "199.00",
      marketPrice: "299.00",
      image: "../../assets/img/banner3.jpg"
    },
    {
      id: 4,
      name: "淘宝爆款商品推荐...",
      brand: "淘宝",
      tag: "爆款",
      delivery: "极速发货",
      price: "99.00",
      marketPrice: "199.00",
      image: "../../assets/img/banner.jpg"
    }
  ],
  pinduoduo: [
    {
      id: 5,
      name: "拼多多超值优惠商品...",
      brand: "拼多多",
      tag: "拼团",
      delivery: "48小时发货",
      price: "59.00",
      marketPrice: "159.00",
      image: "../../assets/img/banner2.jpg"
    },
    {
      id: 6,
      name: "拼多多爆款推荐...",
      brand: "拼多多",
      tag: "秒杀",
      delivery: "拼团优惠",
      price: "39.00",
      marketPrice: "99.00",
      image: "../../assets/img/banner1.jpg"
    }
  ],
  vip: [
    {
      id: 7,
      name: "唯品会品牌折扣商品...",
      brand: "唯品会",
      tag: "折扣",
      delivery: "品牌直发",
      price: "299.00",
      marketPrice: "599.00",
      image: "../../assets/img/banner.jpg"
    },
    {
      id: 8,
      name: "唯品会限时特卖...",
      brand: "唯品会",
      tag: "特卖",
      delivery: "正品保障",
      price: "199.00",
      marketPrice: "499.00",
      image: "../../assets/img/banner3.jpg"
    }
  ],
  jd: [
    {
      id: 9,
      name: "京东自营商品推荐...",
      brand: "京东",
      tag: "自营",
      delivery: "211限时达",
      price: "399.00",
      marketPrice: "499.00",
      image: "../../assets/img/banner2.jpg"
    },
    {
      id: 10,
      name: "京东超值优惠商品...",
      brand: "京东",
      tag: "PLUS",
      delivery: "京东物流",
      price: "299.00",
      marketPrice: "399.00",
      image: "../../assets/img/banner1.jpg"
    }
  ],
  tmall: [
    {
      id: 11,
      name: "天猫国际商品推荐...",
      brand: "天猫",
      tag: "国际",
      delivery: "天猫国际直邮",
      price: "599.00",
      marketPrice: "799.00",
      image: "../../assets/img/banner3.jpg"
    },
    {
      id: 12,
      name: "天猫超级品牌日特惠...",
      brand: "天猫",
      tag: "品牌日",
      delivery: "品牌直发",
      price: "499.00",
      marketPrice: "699.00",
      image: "../../assets/img/banner.jpg"
    }
  ]
};

// 根据当前选中的标签页计算显示的商品
const currentProducts = ref([]);

// 切换标签页
const switchTab = index => {
  activeTabIndex.value = index;
};
function goToSearch(params = {}) {
  getProductList(params).then(res => {
    console.log(res.rows);
    currentProducts.value = res.rows;
  });
}

// 页面加载时的初始化
onMounted(() => {
  goToSearch();
});

watch(activeTabIndex, (newValue, oldValue) => {
  console.log("新值:", newValue);
  goToSearch({ platform: newValue });
});
</script>

<style scoped>
/* 基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.ecommerce-container {
  width: 100%;
  max-width: 450px;
  margin: 0 auto;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica,
    Arial, sans-serif;
  color: #333;
  position: relative;
  overflow: hidden;
  min-height: 100vh;
}

/* 顶部状态栏 */
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 15px;
  background-color: #ff4e44;
  color: white;
  height: 24px;
}

.time {
  font-weight: bold;
  font-size: 14px;
}

.status-icons {
  display: flex;
  align-items: center;
}

.camera-hole {
  width: 8px;
  height: 8px;
  background-color: #000;
  border-radius: 50%;
  margin-right: 10px;
}

.right-icons {
  display: flex;
  gap: 5px;
}

/* 顶部导航栏 */
.top-nav {
  background-color: #ff4e44;
  padding: 10px 10px;
  overflow-x: auto;
  white-space: nowrap;
}

.nav-tabs {
  display: flex;
  justify-content: space-around;
}

.nav-tab {
  padding: 5px 10px;
  color: white;
  font-size: 14px;
  position: relative;
  cursor: pointer;
}

.nav-tab.active {
  font-weight: bold;
}

.nav-tab.active::after {
  content: "";
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background-color: white;
}

.nav-tab-more {
  padding: 5px 10px;
  color: white;
}

.nav-tab-more img {
  width: 20px;
  height: 20px;
}

.member-banner {
  text-align: center;
  width: 100%;
  border-radius: 10px;
  overflow: hidden;
}

.member-banner img {
  width: 100%;
}

.banner-content {
  display: flex;
  justify-content: space-between;
}

.banner-left {
  flex: 2;
}

.banner-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.banner-subtitle {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.banner-desc {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.banner-button {
  display: inline-block;
  background-color: #ff4e44;
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  margin-top: 10px;
}

.banner-right {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.shopping-basket {
  width: 60px;
  height: 60px;
  background-color: #e6a8d3;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.discount {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: #ff4e44;
  color: white;
  font-size: 12px;
  padding: 2px 5px;
  border-radius: 10px;
}

/* 应用图标区 */
.app-icons {
  display: flex;
  justify-content: space-between;
  padding: 15px 10px;
  background-color: white;
  margin: 10px;
  border-radius: 10px;
}

.app-icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 20%;
}

.app-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 5px;
}

.app-icon img {
  width: 100%;
  height: 100%;
}

.app-name {
  font-size: 12px;
  color: #666;
}

.taobao {
  background-color: #ff6a00;
}

.pinduoduo {
  background-color: #e02e24;
}

.vip {
  background-color: #7b00ff;
}

.jd {
  background-color: #d71c1c;
}

.tmall {
  background-color: #ff0036;
}

/* 收益排行 */
.earnings-ranking {
  margin: 10px;
  background-color: white;
  border-radius: 10px;
  display: flex;
}

.ranking-title {
  width: 20%;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.ranking-title img {
  width: 100%;
}

.ranking-list {
  width: 75%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.ranking-item {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  flex: 1;
  font-size: 14px;
}

.user-earnings {
  color: #ff4e44;
  font-size: 14px;
  margin-right: 10px;
}

.earnings-date {
  color: #999;
  font-size: 12px;
  margin-right: 10px;
}

.arrow-right {
  color: #ccc;
}

.arrow-right img {
  width: 8px;
}

/* 商品展示区 */
.product-showcase {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 10px;
  margin-bottom: 60px;
}

.product-item {
  width: calc(50% - 5px);
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  padding-bottom: 10px;
}

.product-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-brand {
  font-size: 14px;
  font-weight: bold;
  padding: 5px 10px;
  text-align: right;
}

.product-tag {
  display: inline-block;
  background-color: #ff4e44;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 3px;
  margin: 0 10px;
}

.product-delivery {
  font-size: 12px;
  color: #666;
  padding: 5px 10px;
}

.product-name {
  font-size: 14px;
  padding: 0 10px;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  font-size: 16px;
  color: #ff4e44;
  font-weight: bold;
  padding: 0 10px;
}

.product-market-price {
  font-size: 12px;
  color: #999;
  padding: 0 10px;
}

/* 底部导航栏 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  background-color: white;
  padding: 10px 0;
  border-top: 1px solid #eee;
  max-width: 450px;
  margin: 0 auto;
}

.bottom-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.bottom-nav-item.active {
  color: #ff4e44;
}

.bottom-nav-item i {
  font-size: 20px;
  margin-bottom: 3px;
}

/* 图标样式 */
.icon-home::before {
  content: "🏠";
}

.icon-message::before {
  content: "💬";
}

.icon-shopping::before {
  content: "🛒";
}

.icon-web::before {
  content: "🌐";
}

.icon-user::before {
  content: "👤";
}

.icon-more::before {
  content: "⋮";
}

.icon-arrow-right::before {
  content: ">";
}

.icon-signal::before {
  content: "📶";
}

.icon-wifi::before {
  content: "📡";
}

.icon-battery::before {
  content: "🔋";
}

@media screen and (min-width: 768px) {
  .ecommerce-container {
    width: 100%;
    max-width: 450px;
    margin: 0 auto;
    background-color: #f5f5f5;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
      Helvetica, Arial, sans-serif;
    color: #333;
    position: relative;
    overflow: hidden;
    min-height: 100vh;
  }

  /* 顶部状态栏 */
  .status-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px 15px;
    background-color: #ff4e44;
    color: white;
    height: 24px;
  }

  .time {
    font-weight: bold;
    font-size: 14px;
  }

  .status-icons {
    display: flex;
    align-items: center;
  }

  .camera-hole {
    width: 8px;
    height: 8px;
    background-color: #000;
    border-radius: 50%;
    margin-right: 10px;
  }

  .right-icons {
    display: flex;
    gap: 5px;
  }

  /* 顶部导航栏 */
  .top-nav {
    background-color: #ff4e44;
    padding: 10px 10px;
    overflow-x: auto;
    white-space: nowrap;
  }

  .nav-tabs {
    display: flex;
    justify-content: space-around;
  }

  .nav-tab {
    padding: 5px 10px;
    color: white;
    font-size: 14px;
    position: relative;
    cursor: pointer;
  }

  .nav-tab.active {
    font-weight: bold;
  }

  .nav-tab.active::after {
    content: "";
    position: absolute;
    bottom: -5px;
    left: 50%;
    transform: translateX(-50%);
    width: 20px;
    height: 2px;
    background-color: white;
  }

  .nav-tab-more {
    padding: 5px 10px;
    color: white;
  }

  .nav-tab-more img {
    width: 20px;
    height: 20px;
  }

  .member-banner {
    text-align: center;
    width: 100%;
    border-radius: 10px;
    overflow: hidden;
  }

  .member-banner img {
    width: 100%;
  }

  .banner-content {
    display: flex;
    justify-content: space-between;
  }

  .banner-left {
    flex: 2;
  }

  .banner-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
  }

  .banner-subtitle {
    font-size: 14px;
    color: #666;
    margin-top: 5px;
  }

  .banner-desc {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
  }

  .banner-button {
    display: inline-block;
    background-color: #ff4e44;
    color: white;
    padding: 5px 10px;
    border-radius: 15px;
    font-size: 12px;
    margin-top: 10px;
  }

  .banner-right {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .shopping-basket {
    width: 60px;
    height: 60px;
    background-color: #e6a8d3;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
  }

  .discount {
    position: absolute;
    top: 5px;
    right: 5px;
    background-color: #ff4e44;
    color: white;
    font-size: 12px;
    padding: 2px 5px;
    border-radius: 10px;
  }

  /* 应用图标区 */
  .app-icons {
    display: flex;
    justify-content: space-between;
    padding: 15px 10px;
    background-color: white;
    margin: 10px;
    border-radius: 10px;
  }

  .app-icon-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 20%;
  }

  .app-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 5px;
  }

  .app-icon img {
    width: 100%;
    height: 100%;
  }

  .app-name {
    font-size: 12px;
    color: #666;
  }

  .taobao {
    background-color: #ff6a00;
  }

  .pinduoduo {
    background-color: #e02e24;
  }

  .vip {
    background-color: #7b00ff;
  }

  .jd {
    background-color: #d71c1c;
  }

  .tmall {
    background-color: #ff0036;
  }

  /* 收益排行 */
  .earnings-ranking {
    margin: 10px;
    background-color: white;
    border-radius: 10px;
    display: flex;
  }

  .ranking-title {
    width: 20%;
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
  }

  .ranking-title img {
    width: 100%;
  }

  .ranking-list {
    width: 75%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 10px;
  }

  .ranking-item {
    display: flex;
    align-items: center;
  }

  .user-avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 10px;
  }

  .user-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .user-name {
    flex: 1;
    font-size: 14px;
  }

  .user-earnings {
    color: #ff4e44;
    font-size: 14px;
    margin-right: 10px;
  }

  .earnings-date {
    color: #999;
    font-size: 12px;
    margin-right: 10px;
  }

  .arrow-right {
    color: #ccc;
  }

  .arrow-right img {
    width: 8px;
  }

  /* 商品展示区 */
  .product-showcase {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    padding: 10px;
    margin-bottom: 60px;
  }

  .product-item {
    width: calc(50% - 5px);
    background-color: white;
    border-radius: 10px;
    overflow: hidden;
    padding-bottom: 10px;
  }

  .product-image {
    width: 100%;
    height: 150px;
    overflow: hidden;
  }

  .product-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .product-brand {
    font-size: 14px;
    font-weight: bold;
    padding: 5px 10px;
    text-align: right;
  }

  .product-tag {
    display: inline-block;
    background-color: #ff4e44;
    color: white;
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 3px;
    margin: 0 10px;
  }

  .product-delivery {
    font-size: 12px;
    color: #666;
    padding: 5px 10px;
  }

  .product-name {
    font-size: 14px;
    padding: 0 10px;
    margin-bottom: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .product-price {
    font-size: 16px;
    color: #ff4e44;
    font-weight: bold;
    padding: 0 10px;
  }

  .product-market-price {
    font-size: 12px;
    color: #999;
    padding: 0 10px;
  }

  /* 底部导航栏 */
  .bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    justify-content: space-around;
    background-color: white;
    padding: 10px 0;
    border-top: 1px solid #eee;
    max-width: 450px;
    margin: 0 auto;
  }

  .bottom-nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 12px;
    color: #999;
  }

  .bottom-nav-item.active {
    color: #ff4e44;
  }

  .bottom-nav-item i {
    font-size: 20px;
    margin-bottom: 3px;
  }

  /* 图标样式 */
  .icon-home::before {
    content: "🏠";
  }

  .icon-message::before {
    content: "💬";
  }

  .icon-shopping::before {
    content: "🛒";
  }

  .icon-web::before {
    content: "🌐";
  }

  .icon-user::before {
    content: "👤";
  }

  .icon-more::before {
    content: "⋮";
  }

  .icon-arrow-right::before {
    content: ">";
  }

  .icon-signal::before {
    content: "📶";
  }

  .icon-wifi::before {
    content: "📡";
  }

  .icon-battery::before {
    content: "🔋";
  }
}
</style>