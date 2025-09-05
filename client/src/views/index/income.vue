<template>
  <div class="profit-rank-page">
    <!-- 头部区域 -->
    <HeaderBar title="收益榜单" backcolor="transparent" headercolor="#fff" />
    <div class="header-section">
      <p class="desc">当前收益榜随机展示100人，数据动态更新</p>
    </div>

    <!-- TOP3 展示区域 - 调整排序，中间是冠军 -->
    <div class="top-three-section">
      <!-- 第二名在左侧 -->
      <div
        class="top-item second-place"
        :style="{ 
          backgroundImage: `url(${back2})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          height: '190px'
        }"
      >
        <div class="medal">
          <img :src="topThreeData[1].headImg || defaultAvatar" alt="用户头像" class="avatar" />
        </div>
        <div class="name">{{ topThreeData[1].userName }}</div>
        <div class="profit">¥{{ topThreeData[1].totalAmount }}</div>
      </div>

      <!-- 冠军在中间（最大最高） -->
      <div
        class="top-item first-place"
        :style="{ 
          backgroundImage: `url(${back1})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          height: '240px',
          width: '100px'
        }"
      >
        <div class="medal">
          <img :src="topThreeData[0].headImg || defaultAvatar" alt="用户头像" class="avatar" />
        </div>
        <div class="name">{{ topThreeData[0].userName }}</div>
        <div class="profit">¥{{ topThreeData[0].totalAmount }}</div>
      </div>

      <!-- 第三名在右侧 -->
      <div
        class="top-item third-place"
        :style="{ 
          backgroundImage: `url(${back3})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          height: '160px'
        }"
      >
        <div class="medal">
          <img :src="topThreeData[2].headImg || defaultAvatar" alt="用户头像" class="avatar" />
        </div>
        <div class="name">{{ topThreeData[2].userName }}</div>
        <div class="profit">¥{{ topThreeData[2].totalAmount }}</div>
      </div>
    </div>

    <!-- 榜单列表区域 -->
    <div class="rank-list-section">
      <template v-if="rankListData.length > 0">
        <div
          v-for="(item, index) in rankListData"
          :key="index"
          class="list-item"
          @click="handleItemClick(item)"
        >
          <div class="rank">{{ item.rank }}</div>
          <img :src="item.headImg" class="avatar" />
          <div class="name">{{ item.userName }}</div>
          <div class="profit">{{ item.totalAmount }}</div>
        </div>
      </template>
      <div v-else class="empty-tip">今日暂无收益榜单</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import back1 from "../../assets/image/back1.png";
import back2 from "../../assets/image/back2.png";
import back3 from "../../assets/image/back3.png";
import { getRankList } from "../../api/index.js";
import defaultAvatar from "../../assets/image/logo.png";

// 模拟TOP3数据 - 添加了backgroundImage属性
const topThreeData = ref([
  { headImg: "", userName: "", totalAmount: 0 },
  { headImg: "", userName: "", totalAmount: 0 },
  { headImg: "", userName: "", totalAmount: 0 }
]);

// 模拟榜单列表数据
const rankListData = ref([]);

// 导航数据

// 当前激活的导航项
const activeNav = ref(4); // 默认激活"我的"

// 交互方法
const handleBack = () => {
  console.log("返回上一页");
  // 实际应用中可以使用router.go(-1)
};

const handleRefresh = () => {
  console.log("刷新榜单数据");
};
const handleItemClick = item => {
  console.log("点击了榜单项:", item);
};
const loadMore = () => {
  console.log("加载更多数据");
};

onMounted(() => {
  getRankList().then(res => {
    console.log(res.data);
    topThreeData.value = res.data.slice(0, 3);
    rankListData.value = res.data.slice(3);
  });
});
</script>

<style scoped>
.profit-rank-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #165dff;
  color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  padding-bottom: 60px; /* 为底部导航留出空间 */
}

/* 头部样式 */
.header-section {
  width: 90%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #165dff;
  position: fixed;
  top: 10px;
  left: 5%;
}

.back-btn,
.refresh-btn {
  background: transparent;
  border: none;
  color: #fff;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.back-btn:hover,
.refresh-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.back-btn {
  position: absolute;
  left: 16px;
  top: 20px;
}

.refresh-btn {
  position: absolute;
  right: 16px;
  top: 20px;
}

.title {
  font-size: 22px;
  font-weight: bold;
  margin: 0 0 8px;
}

.desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 40px 0 0;
}

/* TOP3 样式 */
.top-three-section {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  margin-top: 110px;
  width: 100%;
  position: fixed;
  background: #165dff;
}

.top-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 90px;
  border-radius: 10px;
  position: relative;
  transition: transform 0.3s;
}

.top-item:hover {
  transform: translateY(-5px);
}

.medal {
  position: absolute;
  top: -15px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.medal svg {
  width: 24px;
  height: 24px;
}

.top-item:nth-child(1) .medal svg {
  color: #ffd700;
}

.top-item:nth-child(2) .medal svg {
  color: #e6e6fa;
}

.top-item:nth-child(3) .medal svg {
  color: #cd7f32;
}

.rank {
  font-size: 18px;
  font-weight: bold;
  margin-top: 25px;
  margin-bottom: 5px;
}

.top-item .name {
  font-size: 14px;
  color: #333;
  margin-top: 80px;
  margin-bottom: 8px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.profit {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 榜单列表样式 */
.rank-list-section {
  background-color: #fff;
  border-radius: 16px 16px;
  margin: 340px 16px 0;
  overflow: auto;
  flex-grow: 1;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: background-color 0.2s;
}

.list-item:last-child {
  border-bottom: none;
}

.list-item:hover {
  background-color: #f9f9f9;
}

.list-item .rank {
  font-size: 16px;
  color: #666;
  width: 36px;
  text-align: center;
  font-weight: 500;
  margin-top: 0;
}

.list-item .rank.highlight {
  color: #ff4d4f;
  font-weight: bold;
}

.list-item .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: 0 12px;
}

.list-item .name {
  flex: 1;
  font-size: 16px;
  color: #333;
  margin-bottom: 0;
}

.list-item .profit {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
  margin-bottom: 0;
}

/* 底部样式 */
.bottom-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
}

.load-more-btn {
  background-color: #fff;
  color: #165dff;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.2);
  transition: all 0.2s;
}

.load-more-btn:hover {
  background-color: #f5f5f5;
  transform: translateY(-2px);
}

.nav-bar {
  display: flex;
  justify-content: space-around;
  width: 100%;
  background-color: #fff;
  padding: 8px 0;
  border-radius: 12px 12px 0 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  cursor: pointer;
  flex: 1;
  transition: color 0.2s;
}

.nav-item.active {
  color: #165dff;
}

.nav-item svg {
  width: 22px;
  height: 22px;
}

.nav-item .label {
  font-size: 12px;
  margin-top: 3px;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

@media screen and (min-width: 768px) {
  .profit-rank-page {
    max-width: 450px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background: #165dff;
    color: #fff;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
      Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
    padding-bottom: 60px; /* 为底部导航留出空间 */
  }

  /* 头部样式 */
  .header-section {
    width: 450px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background: #165dff;
    position: fixed; /* ✅ 改成 fixed */
    left: 50%;
    transform: translateX(-50%); /* ✅ 居中 */
    top: 50px;
    z-index: 1000; /* ✅ 确保在上层 */
  }
  .back-btn,
  .refresh-btn {
    background: transparent;
    border: none;
    color: #fff;
    cursor: pointer;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: background-color 0.2s;
  }

  .back-btn:hover,
  .refresh-btn:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  .back-btn {
    position: absolute;
    left: 16px;
    top: 20px;
  }

  .refresh-btn {
    position: absolute;
    right: 16px;
    top: 20px;
  }

  .title {
    font-size: 22px;
    font-weight: bold;
    margin: 0 0 8px;
  }

  .desc {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
    margin: 0 0 0;
  }

  /* TOP3 样式 */
  .top-three-section {
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    margin-top: 230px;
    max-width: 450px;
    height: 150px;
    position: fixed;
    background: #165dff;
  }

  .top-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 90px;
    border-radius: 10px;
    position: relative;
    transition: transform 0.3s;
  }

  .top-item:hover {
    transform: translateY(-5px);
  }

  .medal {
    position: absolute;
    top: -15px;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  .medal svg {
    width: 24px;
    height: 24px;
  }

  .top-item:nth-child(1) .medal svg {
    color: #ffd700;
  }

  .top-item:nth-child(2) .medal svg {
    color: #e6e6fa;
  }

  .top-item:nth-child(3) .medal svg {
    color: #cd7f32;
  }

  .rank {
    font-size: 18px;
    font-weight: bold;
    margin-top: 25px;
    margin-bottom: 5px;
  }

  .top-item .name {
    font-size: 14px;
    color: #333;
    margin-top: 80px;
    margin-bottom: 8px;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 100%;
  }

  .profit {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 10px;
  }

  .avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    border: 2px solid #fff;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  }

  /* 榜单列表样式 */
  .rank-list-section {
    background-color: #fff;
    border-radius: 16px 16px;
    margin: 400px 16px 0;
    overflow: auto;
    flex-grow: 1;
  }

  .list-item {
    display: flex;
    align-items: center;
    padding: 14px 16px;
    border-bottom: 1px solid #f5f5f5;
    transition: background-color 0.2s;
  }

  .list-item:last-child {
    border-bottom: none;
  }

  .list-item:hover {
    background-color: #f9f9f9;
  }

  .list-item .rank {
    font-size: 16px;
    color: #666;
    width: 36px;
    text-align: center;
    font-weight: 500;
    margin-top: 0;
  }

  .list-item .rank.highlight {
    color: #ff4d4f;
    font-weight: bold;
  }

  .list-item .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin: 0 12px;
  }

  .list-item .name {
    flex: 1;
    font-size: 16px;
    color: #333;
    margin-bottom: 0;
  }

  .list-item .profit {
    font-size: 16px;
    color: #ff4d4f;
    font-weight: bold;
    margin-bottom: 0;
  }

  /* 底部样式 */
  .bottom-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px;
  }

  .load-more-btn {
    background-color: #fff;
    color: #165dff;
    border: none;
    padding: 10px 20px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    margin-bottom: 16px;
    box-shadow: 0 2px 8px rgba(22, 93, 255, 0.2);
    transition: all 0.2s;
  }

  .load-more-btn:hover {
    background-color: #f5f5f5;
    transform: translateY(-2px);
  }

  .nav-bar {
    display: flex;
    justify-content: space-around;
    width: 100%;
    background-color: #fff;
    padding: 8px 0;
    border-radius: 12px 12px 0 0;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #999;
    cursor: pointer;
    flex: 1;
    transition: color 0.2s;
  }

  .nav-item.active {
    color: #165dff;
  }

  .nav-item svg {
    width: 22px;
    height: 22px;
  }

  .nav-item .label {
    font-size: 12px;
    margin-top: 3px;
  }

  .empty-tip {
    text-align: center;
    color: #999;
    padding: 40px 0;
  }
}
</style>
