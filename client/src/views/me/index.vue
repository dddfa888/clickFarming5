<template>
  <div class="personal-center">
    <!-- 头部信息区域 -->
    <div class="header">
      <div class="header-info">
        <img class="avatar" :src="userInfo.headImg" alt="用户头像" />
        <div class="text-info">
          <div class="user-base">
            <span class="nickname">{{ userInfo.loginAccount }}</span>
            <span class="credit">信用分: {{ userInfo.creditScore }}</span>
            <span class="member">{{ userInfo.levelName }}</span>
          </div>
          <div class="invite-code" @click="copyInvitationCode">
            邀请码: {{ userInfo.invitationCode }}
            <img src="../../assets/image/copy.svg" alt />
          </div>
        </div>
        <img src="../../assets/img/right.png" alt @click="router.push('/personal')" />
      </div>
      <div class="signature">个性签名: {{ userInfo.signature }}</div>
    </div>

    <!-- 资产卡片区域 -->
    <div class="asset-card">
      <div class="total-asset">
        <span class="icon">
          <!-- 替换为更贴近原图的图标（示例用红色背景+白色对勾） -->
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="white"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="w-6 h-6"
          >
            <rect x="2" y="6" width="20" height="12" rx="2" ry="2" fill="red" />
            <path d="M9 12l2 2 4-4" />
          </svg>
        </span>
        <span class="label">总资产</span>

        <!-- 新增“方块图标” -->
        <span class="grid-icon">
          <svg viewBox="0 0 24 24" fill="white" class="w-5 h-5">
            <rect x="3" y="3" width="4" height="4" rx="1" />
            <rect x="10" y="3" width="4" height="4" rx="1" />
            <rect x="17" y="3" width="4" height="4" rx="1" />
            <rect x="3" y="10" width="4" height="4" rx="1" />
            <rect x="10" y="10" width="4" height="4" rx="1" />
            <rect x="17" y="10" width="4" height="4" rx="1" />
          </svg>
        </span>
      </div>
      <div class="action-buttons">
        <div class="amount">{{ userGrade.userBalance }}</div>
        <div style="display: flex; justify-content: space-between;width: 45%">
          <button class="withdraw" @click="router.push('/reflect')">提现</button>
          <button class="recharge" @click="popupRef.openPopup">充值</button>
        </div>
      </div>
      <div class="profit">
        <div class="item">
          <span>{{userGrade.profit}}</span>
          <span>今日收益</span>
        </div>
        <div class="item">
          <span>{{userGrade.profit1}}</span>
          <span>昨日收益</span>
        </div>
        <div class="item">
          <span>{{Number(userGrade.profit)+Number(userGrade.profit1)}}</span>
          <span>总收益</span>
        </div>
      </div>
    </div>

    <!-- 功能列表区域 -->
    <div class="function-list">
      <div
        v-for="(item, index) in functionItems"
        :key="index"
        class="function-item"
        @click="handleFunctionClick(item.path)"
      >
        <span class="icon">
          <img :src="item.iconPath" alt />
        </span>
        <span class="label">{{ item.label }}</span>
        <span class="arrow">
          <img src="../../assets/img/right.png" alt />
        </span>
      </div>
    </div>

    <Popup ref="popupRef" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Popup from "../../components/Popup.vue";
import { getUserInfo, getTotalAssets } from "../../api/index";
import { showAlert } from "../../utils/notify";

const router = useRouter();
const popupRef = ref(null);

const getImageUrl = name =>
  new URL(`../../assets/image/${name}`, import.meta.url).href;

// 功能项数据
const functionItems = ref([
  {
    label: "会员中心",
    iconPath: getImageUrl("zs.svg"),
    path: "/member"
  },
  {
    label: "充值记录",
    iconPath: getImageUrl("cz.svg"),
    path: "/deposit"
  },
  {
    label: "提现记录",
    iconPath: getImageUrl("tx.svg"),
    path: "/withdrawhistory"
  },
  {
    label: "我的团队",
    iconPath: getImageUrl("td.svg"),
    path: "/team"
  },
  {
    label: "余额宝",
    iconPath: getImageUrl("ye.svg"),
    path: "/yubao"
  },
  {
    label: "银行卡绑定",
    iconPath: getImageUrl("sk.svg"),
    path: "/backcard"
  },
  {
    label: "订单记录",
    iconPath: getImageUrl("jl.svg"),
    path: "/record"
  },
  {
    label: "设置",
    iconPath: getImageUrl("sz.svg"),
    path: "/setup"
  }
]);

const copyInvitationCode = () => {
  const invitationCode = userInfo.value.invitationCode;
  navigator.clipboard.writeText(invitationCode);
  showAlert("复制成功", 2000);
};

// 当前激活的导航项
const activeNav = ref(4);
const userInfo = ref({});
const userGrade = ref({});

// 功能项点击处理
const handleFunctionClick = path => {
  router.push(path);
  // 这里可以添加对应的路由跳转或功能处理
};

onMounted(() => {
  getUserInfo().then(res => {
    console.log(res.data);
    userInfo.value = res.data;
  });

  getTotalAssets().then(res => {
    console.log(res.data, "jhjih");
    userGrade.value = res.data;
  });
});
</script>

<style scoped>
.personal-center {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #eeeeee;
  position: relative;
}

.header {
  padding: 16px;
}

/* 头部信息样式 */
.header-info {
  display: flex;
  align-items: center;
  padding: 20px 0;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-bottom: 12px;
  border: 2px solid #eee;
  align-self: center;
}

.text-info {
  margin-bottom: 10px;
  width: 100%;
}

.user-base {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.nickname {
  font-weight: bold;
  font-size: 18px;
  color: #333;
}

.credit,
.member {
  font-size: 14px;
  color: #666;
  padding: 2px 8px;
  border-radius: 12px;
  background-color: #f5f5f5;
}

.account,
.invite-code {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
}
.invite-code {
  display: flex;
  align-items: center;
}

.invite-code img {
  width: 20px;
  height: 20px;
  margin-left: 4px;
}

.account::before,
.invite-code::before {
  content: "•";
  margin-right: 6px;
  color: #ccc;
  font-size: 8px;
}

.signature {
  font-size: 15px;
  color: #333;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

/* 资产卡片样式 */
.asset-card {
  background-color: #fff; /* 蓝色背景 */
  color: #000;
  border-radius: 10px;
  margin: 0 16px 16px;
}
.total-asset {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 让图标和文字两端对齐 */
  margin-bottom: 8px;
  background-color: #2563eb;
  color: #fff;
  padding: 10px;
  border-radius: 10px 10px 0 0;
}
.icon img {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  padding: 2px;
}
.label {
  font-size: 16px;
}
.amount {
  font-size: 30px;
  color: #ffc107;
}
.grid-icon svg {
  width: 20px;
  height: 20px;
}
.profit {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  padding: 20px;
}

.profit .item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: space-around;
  align-items: center;
  padding: 10px;
}
.withdraw,
.recharge {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
}
.withdraw {
  background-color: #2563eb;
  color: #fff;
}
.recharge {
  background-color: #ef4444;
}

/* 功能列表样式 */
.function-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 0 16px 100px; /* 底部留出导航栏空间 */
  background-color: #eee;
  overflow: hidden;
}

.function-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  border-radius: 12px;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.function-item:hover {
  background-color: #f9f9f9;
}

.function-item .label {
  flex: 1;
  margin: 0 12px;
  font-size: 14px;
  color: #333;
}

.arrow svg {
  color: #ccc;
  width: 20px;
  height: 20px;
}

/* 底部导航样式 */
.bottom-nav {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 8px 0 4px;
  background-color: #fff;
  border-top: 1px solid #eee;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  font-size: 12px;
  cursor: pointer;
  transition: color 0.2s ease;
  width: 20%;
}

.nav-item.active {
  color: #2563eb;
}

.nav-item .icon svg {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}

.nav-item .label {
  font-size: 12px;
}

@media screen and (min-width: 768px) {
  .personal-center {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #eeeeee;
    position: relative;
    margin: 0 auto;
    max-width: 450px;
  }

  .header {
    padding: 16px;
  }

  /* 头部信息样式 */
  .header-info {
    display: flex;
    align-items: center;
    padding: 20px 0;
  }

  .avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    margin-bottom: 12px;
    border: 2px solid #eee;
    align-self: center;
  }

  .text-info {
    margin-bottom: 10px;
    width: 100%;
  }

  .user-base {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
    flex-wrap: wrap;
  }

  .nickname {
    font-weight: bold;
    font-size: 18px;
    color: #333;
  }

  .credit,
  .member {
    font-size: 14px;
    color: #666;
    padding: 2px 8px;
    border-radius: 12px;
    background-color: #f5f5f5;
  }

  .account,
  .invite-code {
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
    display: flex;
    align-items: center;
  }

  .account::before,
  .invite-code::before {
    content: "•";
    margin-right: 6px;
    color: #ccc;
    font-size: 8px;
  }

  .invite-code {
    display: flex;
    align-items: center;
  }

  .invite-code img {
    width: 20px;
    height: 20px;
    margin-left: 4px;
  }

  .signature {
    font-size: 15px;
    color: #333;
    padding-top: 8px;
    border-top: 1px solid #f0f0f0;
  }

  /* 资产卡片样式 */
  .asset-card {
    background-color: #fff; /* 蓝色背景 */
    color: #000;
    border-radius: 10px;
    margin: 0 16px 16px;
  }
  .total-asset {
    display: flex;
    align-items: center;
    justify-content: space-between; /* 让图标和文字两端对齐 */
    margin-bottom: 8px;
    background-color: #2563eb;
    color: #fff;
    padding: 10px;
    border-radius: 10px 10px 0 0;
  }
  .icon img {
    width: 24px;
    height: 24px;
    border-radius: 4px;
    padding: 2px;
  }
  .label {
    font-size: 16px;
  }
  .amount {
    font-size: 30px;
    color: #ffc107;
  }
  .grid-icon svg {
    width: 20px;
    height: 20px;
  }
  .profit {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 14px;
    padding: 20px;
  }

  .profit .item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    justify-content: space-around;
    align-items: center;
    padding: 10px;
  }
  .withdraw,
  .recharge {
    padding: 8px 16px;
    border: none;
    border-radius: 4px;
    color: #fff;
    cursor: pointer;
  }
  .withdraw {
    background-color: #2563eb;
    color: #fff;
  }
  .recharge {
    background-color: #ef4444;
  }

  /* 功能列表样式 */
  .function-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin: 0 16px 80px; /* 底部留出导航栏空间 */
    background-color: #eee;
    overflow: hidden;
  }

  .function-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px;
    border-radius: 12px;
    background-color: #fff;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .function-item:hover {
    background-color: #f9f9f9;
  }

  .function-item .label {
    flex: 1;
    margin: 0 12px;
    font-size: 14px;
    color: #333;
  }

  .arrow svg {
    color: #ccc;
    width: 20px;
    height: 20px;
  }

  /* 底部导航样式 */
  .bottom-nav {
    display: flex;
    justify-content: space-around;
    align-items: center;
    padding: 8px 0 4px;
    background-color: #fff;
    border-top: 1px solid #eee;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 100;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #999;
    font-size: 12px;
    cursor: pointer;
    transition: color 0.2s ease;
    width: 20%;
  }

  .nav-item.active {
    color: #2563eb;
  }

  .nav-item .icon svg {
    width: 24px;
    height: 24px;
    margin-bottom: 4px;
  }

  .nav-item .label {
    font-size: 12px;
  }
}
</style>
