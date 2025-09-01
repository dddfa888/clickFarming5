<template>
  <div class="app-container">
    <HeaderBar :title="$t('团队记录')" backcolor="#ece9ee" />
    <!-- 顶部蓝色信息卡片区域 -->
    <div class="blue-card">
      <div class="main-stats">
        <div class="stat-item">
          <p class="stat-label">总资产(元)</p>
          <p class="stat-value">10210.62</p>
        </div>
        <div class="stat-item">
          <p class="stat-label">下级佣金</p>
          <p class="stat-value">+0.0000</p>
        </div>
        <div class="stat-item">
          <p class="stat-label">团队总充值</p>
          <p class="stat-value">0.00</p>
        </div>
        <div class="stat-item">
          <p class="stat-label">团队总收益</p>
          <p class="stat-value">0.00</p>
        </div>
      </div>

      <div class="sub-stats">
        <div class="sub-stat-item">
          <p class="sub-stat-value">0</p>
          <p class="sub-stat-label">团队人数</p>
        </div>
        <div class="sub-stat-item">
          <p class="sub-stat-value">0</p>
          <p class="sub-stat-label">直推人数</p>
        </div>
        <div class="sub-stat-item">
          <p class="sub-stat-value">0</p>
          <p class="sub-stat-label">今日新增</p>
        </div>
        <div class="sub-stat-item">
          <p class="sub-stat-value">0</p>
          <p class="sub-stat-label">活跃人数</p>
        </div>
      </div>
    </div>

    <!-- 选项卡区域 -->
    <div class="tabs-container">
      <button
        v-for="(tab, index) in tabs"
        :key="index"
        :class="['tab-button', { 'active': activeTab === index }]"
        @click="activeTab = index"
      >{{ tab }}</button>
    </div>

    <!-- 主内容区域 - 无数据状态 -->
    <div class="content-area">
      <div class="no-data">
        <div class="no-data-icon">
          <!-- 使用简单的SVG替代图片 -->
        </div>
        <p class="no-data-text">{{ $t("还没有数据") }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { getTeamList } from "../../api/index";

const { t } = useI18n();

// 选项卡数据和状态
const tabs = ["一级(0人)", "二级(0人)", "三级(0人)"];
const activeTab = ref(0);
const teamData = ref([
  {
    name: "一级(0人)",
    data: [
      {
        name: "23242",
        phone: "18888888888"
      }
    ]
  },
  {
    name: "二级(0人)",
    data: [
      {
        name: "gfdgb",
        phone: "18888888888"
      }
    ]
  },
  { name: "三级(0人)", data: [] }
]);

onMounted(async () => {
  getTeamList().then(res => {
    console.log(res);
  });
});
</script>

<style scoped>
.app-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #ece9ee;
  padding-bottom: 50px; /* 为底部导航预留空间 */
  box-sizing: border-box;
}

/* 蓝色卡片样式 */
.blue-card {
  background: linear-gradient(135deg, #4a90e2 0%, #007bff 100%);
  color: white;
  border-radius: 12px;
  padding: 16px;
  margin: 16px;
  margin-top: 50px;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
}

.main-stats {
  display: flex;
  gap: 46px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
}

.stat-item .stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 4px 0;
}

.stat-item .stat-value {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.sub-stats {
  display: flex;
  justify-content: space-between;
}

.sub-stat-item {
  text-align: center;
  flex: 1;
}

.sub-stat-value {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.sub-stat-label {
  font-size: 12px;
  opacity: 0.9;
  margin: 0;
}

/* 选项卡样式 */
.tabs-container {
  display: flex;
  padding: 0 16px;
  margin-bottom: 16px;
  gap: 8px;
}

.tab-button {
  flex: 1;
  padding: 10px 0;
  border: none;
  border-radius: 8px;
  background-color: white;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-button.active {
  background-color: #007bff;
  color: white;
}

/* 内容区域样式 */
.content-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.no-data-text {
  margin-top: 12px;
  font-size: 16px;
}
</style>
