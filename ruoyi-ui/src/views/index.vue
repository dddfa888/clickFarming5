<template>
  <div id="app" class="container">
    <h3>商城统计</h3>
    <p class="time">{{ localTime }}</p>
    <div class="stats-grid">
      <!-- 循环渲染所有统计卡片 -->
      <div v-for="(card, index) in cards" :key="index" :class="['stat-card', card.cardClass]">
        <p>{{ card.title }}</p>
        <p class="big-num">{{ card.bigNum }}</p>
        <p>
          {{ card.detail1Label }}
          <span class="highlight">{{ card.detail1Value }}</span>
        </p>
        <p>{{ card.detail2 }}</p>
        <i :class="card.icon"></i>
      </div>
    </div>
  </div>
</template>

  <script>
export default {
  data() {
    return {
      localTime: "",
      cards: [
        {
          title: "首冲人数",
          bigNum: 2,
          detail1Label: "今日首冲人数",
          detail1Value: 0,
          detail2: "昨日首冲人数 0",
          icon: "fas fa-layer-group",
          cardClass: "card-blue"
        },
        {
          title: "用户总量",
          bigNum: 4,
          detail1Label: "今日新增用户",
          detail1Value: 0,
          detail2: "昨日新增用户 0",
          icon: "fas fa-users",
          cardClass: "card-pink"
        },
        {
          title: "订单总量",
          bigNum: 30,
          detail1Label: "今日新增订单",
          detail1Value: 3,
          detail2: "昨日新增订单 18",
          icon: "fas fa-book",
          cardClass: "card-purple"
        },
        {
          title: "订单总金额",
          bigNum: 29244.48,
          detail1Label: "今日新增订单总金额",
          detail1Value: 3282.6,
          detail2: "昨日新增订单总金额 18519.61",
          icon: "fas fa-yen-sign",
          cardClass: "card-darkblue"
        },
        {
          title: "用户充值",
          bigNum: 1229,
          detail1Label: "今日新增充值",
          detail1Value: 0,
          detail2: "昨日新增充值 220",
          icon: "fas fa-question-circle",
          cardClass: "card-yellow"
        },
        {
          title: "充值人数",
          bigNum: 2,
          detail1Label: "今日充值人数",
          detail1Value: 0,
          detail2: "昨日充值人数 1",
          icon: "fas fa-question-circle",
          cardClass: "card-orange"
        },
        {
          title: "用户提现",
          bigNum: 111,
          detail1Label: "今日新增提现",
          detail1Value: 111,
          detail2: "昨日新增提现 0",
          icon: "fas fa-dollar-sign",
          cardClass: "card-red"
        },
        {
          title: "提现人数",
          bigNum: 1,
          detail1Label: "今日提现人数",
          detail1Value: 1,
          detail2: "昨日提现人数 0",
          icon: "fas fa-dollar-sign",
          cardClass: "card-deeporange"
        },
        {
          title: "抢单佣金",
          bigNum: 247.94,
          detail1Label: "今日新增佣金",
          detail1Value: 18.06,
          detail2: "昨日新增佣金 110.8",
          icon: "fas fa-question-circle",
          cardClass: "card-purple-red"
        },
        {
          title: "利息宝转入",
          bigNum: 0,
          detail1Label: "今日新增利息宝",
          detail1Value: 0,
          detail2: "昨日新增利息宝 0",
          icon: "fas fa-dollar-sign",
          cardClass: "card-green"
        },
        {
          title: "下级返佣",
          bigNum: 39.68,
          detail1Label: "今日新增佣金",
          detail1Value: 2.89,
          detail2: "昨日新增佣金 17.73",
          icon: "fas fa-question-circle",
          cardClass: "card-lightbrown"
        },
        {
          title: "用户总余额",
          bigNum: "2405.62(0)",
          detail1Label: "今日利息宝转出",
          detail1Value: 0,
          detail2: "今日利息宝收益 0",
          icon: "fas fa-dollar-sign",
          cardClass: "card-dark"
        }
      ]
    };
  },
  mounted() {
    // 初始化时间并每秒更新
    this.updateLocalTime();
    this.timer = setInterval(() => {
      this.updateLocalTime();
    }, 1000);
  },
  beforeDestroy() {
    // 清除定时器
    clearInterval(this.timer);
  },
  methods: {
    updateLocalTime() {
      const now = new Date();
      const year = now.getFullYear();
      const month = this.padZero(now.getMonth() + 1);
      const day = this.padZero(now.getDate());
      const hours = this.padZero(now.getHours());
      const minutes = this.padZero(now.getMinutes());
      const seconds = this.padZero(now.getSeconds());

      this.localTime = `当地时间：${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    padZero(num) {
      // 数字补零
      return num < 10 ? "0" + num : num;
    }
  }
};
</script>

  <style>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 20px;
  background-color: #f5f5f5;
}

.container {
  max-width: 100%;
  margin: 0 auto;
}

.time {
  text-align: right;
  margin-bottom: 20px;
  color: #666;
}

.stats-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card i {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 40px;
  opacity: 0.3;
}

.big-num {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.highlight {
  font-weight: bold;
  font-size: 30px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 卡片颜色 */
.card-blue {
  background: linear-gradient(-125deg, #57bdbf, #2f9de2);
}

.card-pink {
  background: linear-gradient(-125deg, #ff7d7d, #fb2c95);
}

.card-purple {
  background: linear-gradient(-113deg, #c543d8, #925cc3);
}

.card-darkblue {
  background: linear-gradient(-113deg, #8e8cb3, #2219d0);
}

.card-yellow {
  background: linear-gradient(-141deg, #ecca1b, #f39526);
}

.card-orange {
  background: linear-gradient(-141deg, #ecca1b, #f39526);
}

.card-red {
  background: linear-gradient(-141deg, #ec4b1b, #f39526);
}

.card-deeporange {
  background: linear-gradient(-141deg, #ec4b1b, #f39526);
}

.card-purple-red {
  background: linear-gradient(-141deg, #963064, #8a0920);
}

.card-green {
  background: linear-gradient(-141deg, #1bec78, #155623);
}

.card-lightbrown {
  background: linear-gradient(-141deg, #ea7575, #abd6c6);
}

.card-dark {
  background: linear-gradient(-141deg, #2f3331, #565115);
}
</style>
