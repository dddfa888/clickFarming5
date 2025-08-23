<!-- src/views/CompanyIntro.vue -->
<template>
  <div class="company-intro">
    <HeaderBar :title="t('系统消息')" />
    <!-- 系统公告卡片 -->
    <div class="card">
      <div class="card-content">
        <img class="icon-bell" src="../../assets/img/ld-black.png" alt />
        <span class="text">{{ $t("系统公告") }}</span>
      </div>
      <img class="arrow" src="../../assets/img/right.png" alt />
    </div>

    <!-- 暂无数据提示 -->
    <div class="empty-text" v-if="noticeList.length === 0">{{ $t("还没有数据") }}</div>
    <div class="content-box" v-else>
      <div v-for="item in noticeList" :key="item.id" class="notice-item">
        <p>{{ item.title }}</p>
        <p v-html="item.content"></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref } from "vue";
import { getUserNotification } from "../../api/index";
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const noticeList = ref([
  // {
  //   id: 1,
  //   title: "시스템 알림",
  //   content: "보증금 재충전 청소"
  // }
]);
getUserNotification().then(res => {
  console.log(res.data);
  // noticeList.value = res.data;
});
</script>

<style scoped>
.company-intro {
  background: #121212;
  height: 100vh;
  overflow-y: auto;
  /* padding-bottom: 70px; */
}

.content-box {
  margin: 8px;
  padding: 10px;
  color: white;
  font-size: 12px;
  overflow: auto;
  background-color: #252726;
  border-radius: 5px;
}

.notice-title {
  display: flex;
}
.notice-title p {
  padding-right: 12px;
}

/* 卡片 */
.card {
  background-color: #1e1e1e;
  border-radius: 6px;
  margin: 20px;
  margin-top: 50px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: calc(100% - 40px);
  box-sizing: border-box;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-bell {
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url("../../assets/img/ld-black.png") no-repeat center;
  background-size: cover;
}

.card .text {
  font-size: 14px;
  color: #fff;
}

.card .arrow {
  width: 10px;
  color: #888;
}

/* 暂无数据 */
.empty-text {
  color: #888;
  margin-top: 40px;
  font-size: 14px;
  text-align: center;
}

@media screen and (min-width: 768px) {
  .company-intro {
    background: #121212;
    width: 450px;
    height: 100vh;
    margin: 0 auto;
    overflow-y: auto;
    /* padding-bottom: 70px; */
  }

  .content-box {
    margin: 8px;
    padding: 10px;
    color: white;
    font-size: 12px;
    overflow: auto;
    border-radius: 5px;
  }

  .notice-title {
    display: flex;
  }
  .notice-title p {
    padding-right: 12px;
  }

  /* 卡片 */
  .card {
    background-color: #1e1e1e;
    border-radius: 6px;
    margin: 20px;
    margin-top: 50px;
    padding: 12px 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: calc(100% - 40px);
    box-sizing: border-box;
  }

  .card-content {
    display: flex;
    align-items: center;
    gap: 8px;
    text-align: center;
  }

  .icon-bell {
    display: inline-block;
    width: 16px;
    height: 16px;
    background: url("../../assets/img/ld-black.png") no-repeat center;
    background-size: cover;
  }

  .card .text {
    font-size: 14px;
    color: #fff;
  }

  .card .arrow {
    width: 10px;
    color: #888;
  }

  /* 暂无数据 */
  .empty-text {
    color: #888;
    margin-top: 40px;
    font-size: 14px;
  }
}
</style>
