<template>
  <div class="member-center-container">
    <!-- 头部区域 -->

    <HeaderBar title="会员中心" backcolor="#e6f2ff" />

    <!-- 会员卡片列表 -->
    <div class="member-cards">
      <!-- 铂金会员卡片 -->
      <div
        class="member-card"
        :style="{ backgroundImage: `url(${item.image})` }"
        v-for="item in memberCenter"
        :key="item.id"
        :class="item.level"
      >
        <div class="card-top">
          <div class="member-info">
            <div class="member-level">{{ item.gradeName }}</div>
            <div class="commission-rate">{{item.commissionRate}}%佣金比例</div>
          </div>
          <div class="price">¥{{item.withdrawAmount}}元</div>
        </div>
        <div class="card-bottom">
          <div class="item">
            <div class="label">提现次数</div>
            <div class="value">{{item.withdrawTimes}}次/天</div>
          </div>
          <div class="item">
            <div class="label">提现额度</div>
            <div class="value">{{ item.amountRange }}</div>
          </div>
          <div class="item">
            <div class="label">拼团次数</div>
            <div class="value">{{ item.buyProdNum }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { getMemberCenter } from "../../api/index";

const title = ref("会员中心");
const { t } = useI18n();
const memberCenter = ref([]);

onMounted(() => {
  getMemberCenter().then(res => {
    console.log(res);
    memberCenter.value = res.rows;
  });
});
</script>

<style scoped>
/* 容器样式 */
.member-center-container {
  background-color: #e6f2ff;
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  font-size: 18px;
  border-bottom: 1px solid #eee;
}
.back {
  cursor: pointer;
  margin-right: 10px;
}
.title {
  font-weight: bold;
}

/* 会员卡片样式 */
.member-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px;
  margin-top: 50px;
}
.member-card {
  border-radius: 8px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  width: 85%;
  margin-left: 1.5%;
  height: 130px;
  background-size: 100% 100%;
}

.silver {
  background-image: url(../../assets/image/level-1.png);
  background-size: cover;
}

.gold {
  background-image: url(../../assets/image/level-2.png);
  background-size: cover;
}
.platinum {
  background-image: url(../../assets/image/level-3.png);
  background-size: cover;
}
.card-top {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 15px;
}
.member-info {
  display: flex;
  flex-direction: column;
}
.member-level {
  font-weight: bold;
  margin-bottom: 5px;
}
.commission-rate {
  color: #666;
}
.price {
  font-weight: bold;
}
.card-icon img {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
}
.card-bottom {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.label {
  color: #999;
  font-size: 14px;
  margin-bottom: 3px;
}
.value {
  font-weight: 500;
}

/* 底部导航样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
  border-top: 1px solid #eee;
}
.nav-item {
  text-align: center;
}
.active {
  color: blue;
}
.icon {
  font-size: 14px;
}

@media screen and (min-width: 768px) {
  .member-center-container {
    background-color: #e6f2ff;
    min-height: 100vh;
    width: 450px;
    margin: 0 auto;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
  }

  /* 会员卡片样式 */
  .member-cards {
    display: flex;
    flex-direction: column;
    gap: 15px;
    padding: 10px;
    margin-top: 50px;
  }
  .member-card {
    border-radius: 8px;
    padding: 15px;
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    width: 85%;
    margin-left: 2%;
    height: 130px;
    background-size: 100% 100%;
  }

  .silver {
    background-image: url(../../assets/image/level-1.png);
    background-size: cover;
  }

  .gold {
    background-image: url(../../assets/image/level-2.png);
    background-size: cover;
  }
  .platinum {
    background-image: url(../../assets/image/level-3.png);
    background-size: cover;
  }
  .card-top {
    display: flex;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 15px;
  }
  .member-info {
    display: flex;
    flex-direction: column;
  }
  .member-level {
    font-weight: bold;
    margin-bottom: 5px;
  }
  .commission-rate {
    color: #666;
  }
  .price {
    font-weight: bold;
  }
  .card-icon img {
    width: 80px;
    height: 80px;
    margin-bottom: 15px;
  }
  .card-bottom {
    display: flex;
    justify-content: space-between;
    width: 100%;
  }
  .item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .label {
    color: #999;
    font-size: 14px;
    margin-bottom: 3px;
  }
  .value {
    font-weight: 500;
  }

  /* 底部导航样式 */
  .bottom-nav {
    display: flex;
    justify-content: space-around;
    padding: 10px 0;
    border-top: 1px solid #eee;
  }
  .nav-item {
    text-align: center;
  }
  .active {
    color: blue;
  }
  .icon {
    font-size: 14px;
  }
}
</style>