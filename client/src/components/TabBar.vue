<template>
  <div class="tabbar">
    <!-- tablist（上面的那条） -->
    <div
      v-if="showCustomerService"
      style="display:flex;justify-content:space-between;width:100%;height:50px;"
    >
      <div
        class="tabbar-item"
        v-for="(item, index) in tablist"
        :key="index"
        :class="{ active: activeListIndex === index }"
        @click="onClickList(item, index)"
      >
        <img :src="activeListIndex === index ? item.iconActive : item.icon" class="tabbar-icon" />
        <span style="text-align:center;">{{ item.name }}</span>
      </div>
    </div>

    <!-- tabs（底部那条，包含“网页版”） -->
    <div style="display:flex;justify-content:space-between;width:100%;height:50px;">
      <div
        class="tabbar-item"
        v-for="(item, index) in tabs"
        :key="index"
        :class="{ active: activeTabsIndex === index }"
        @click="onClickTabs(item, index)"
      >
        <img :src="activeTabsIndex === index ? item.iconActive : item.icon" class="tabbar-icon" />
        <span style="text-align:center;">{{ item.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const getImageUrl = name =>
  new URL(`../assets/image/${name}`, import.meta.url).href;

// tabs 和 tablist 配置（与你原来相同）
const tabs = computed(() => [
  {
    name: "发现",
    path: "/",
    icon: getImageUrl("home.svg"),
    iconActive: getImageUrl("homeactive.svg")
  },
  {
    name: "信息",
    path: "/information",
    icon: getImageUrl("xinxi.svg"),
    iconActive: getImageUrl("xinxiactive.svg")
  },
  {
    name: "选好物",
    path: "/warehouse",
    icon: getImageUrl("shop.svg"),
    iconActive: getImageUrl("shopactive.svg")
  },
  {
    name: "网页版",
    path: "web",
    icon: getImageUrl("wj.svg"),
    iconActive: getImageUrl("wjactive.svg")
  },
  {
    name: "个人中心",
    path: "/me",
    icon: getImageUrl("gr.svg"),
    iconActive: getImageUrl("gractive.svg")
  }
]);

const tablist = computed(() => [
  {
    name: "发现",
    path: "/",
    icon: getImageUrl("home.svg"),
    iconActive: getImageUrl("homeactive.svg")
  },
  {
    name: "选好物",
    path: "/warehouse",
    icon: getImageUrl("shop.svg"),
    iconActive: getImageUrl("shopactive.svg")
  },
  {
    name: "记录",
    path: "/record",
    icon: getImageUrl("jl.svg"),
    iconActive: getImageUrl("jlactive.svg")
  },
  {
    name: "个人中心",
    path: "/me",
    icon: getImageUrl("gr.svg"),
    iconActive: getImageUrl("gractive.svg")
  }
]);

// 控制两条导航显示/高亮的状态
const showCustomerService = ref(false); // 是否显示上方 tablist（点击 "网页版" 会打开）
const activeTabsIndex = ref(0); // 底部 tabs 的高亮索引（初始可改）
const activeListIndex = ref(-1); // 上方 tablist 的高亮索引（-1 表示无高亮）

// 点击底部 tabs（带 "网页版" 的那条）
function onClickTabs(item, index) {
  // 只高亮底部被点中的项，清除上方高亮
  activeTabsIndex.value = index;
  activeListIndex.value = -1;

  // 特殊逻辑：点击 "网页版" 只显示 tablist（并不导航）
  if (item.path === "web") {
    showCustomerService.value = true;
    return;
  }

  // 其它项：隐藏 tablist（只显示底部）
  showCustomerService.value = false;

  // 外链处理或路由跳转
  if (item.path && typeof item.path === "string") {
    if (item.path.startsWith("http")) {
      window.open(item.path, "_blank");
    } else {
      router.push(item.path).catch(() => {});
    }
  }
}

// 点击上方 tablist
function onClickList(item, index) {
  // 只高亮上方被点中的项，清除底部高亮
  activeListIndex.value = index;
  activeTabsIndex.value = -1;

  // 点击上方项后（你之前的逻辑）若想只显示上方可保留 showCustomerService = true
  // 这里保持 showCustomerService 为 true（两条同时显示的状态下点击上方不隐藏）
  // 若希望点击上方后只显示上方，请改为： showCustomerService.value = true; 并隐藏底部（不推荐）

  if (item.path && typeof item.path === "string") {
    if (item.path.startsWith("http")) {
      window.open(item.path, "_blank");
    } else {
      router.push(item.path).catch(() => {});
    }
  }
}
</script>

<style scoped>
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  max-width: 100%;
  background-color: #e4e2e2;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 999;
  margin: 0 auto;
  flex-direction: column;
}
.tabbar-item {
  flex: 1;
  display: flex;
  font-size: 12px;
  flex-direction: column;
  align-items: center;
  color: #000;
  cursor: pointer;
}
.tabbar-item.active {
  color: #1296db;
  font-weight: bold;
}
.tabbar-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}
@media screen and (min-width: 768px) {
  .tabbar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    max-width: 450px;
    background-color: #e4e2e2;
    display: flex;
    justify-content: space-around;
    align-items: center;
    z-index: 999;
    margin: 0 auto;
    flex-direction: column;
  }
  .tabbar-item {
    flex: 1;
    display: flex;
    font-size: 12px;
    flex-direction: column;
    align-items: center;
    color: #000;
    cursor: pointer;
  }
  .tabbar-item.active {
    color: #1296db;
    font-weight: bold;
  }
  .tabbar-icon {
    width: 24px;
    height: 24px;
    margin-bottom: 4px;
  }
}
</style>
