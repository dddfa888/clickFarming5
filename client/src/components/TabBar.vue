<template>
  <div class="tabbar">
    <div
      class="tabbar-item"
      v-for="(item, index) in tabs.slice(0, 2)"
      :key="index"
      :class="{ active: currentRoute === item.path }"
      @click="navigate(item.path, item.name)"
    >
      <img :src="currentRoute === item.path ? item.iconActive : item.icon" class="tabbar-icon" />
      <span style="text-align: center;">{{ $t(item.name)}}</span>
    </div>

    <div class="tabbar-middle" @click="onCenterClick">
      <img :src="centerIcon" class="center-icon" />
    </div>

    <div
      class="tabbar-item"
      v-for="(item, index) in tabs.slice(2)"
      :key="index + 2"
      :class="{ active: currentRoute === item.path }"
      @click="navigate(item.path, item.name)"
    >
      <img :src="currentRoute === item.path ? item.iconActive : item.icon" class="tabbar-icon" />
      <span>{{ item.name }}</span>
    </div>
  </div>
  <!-- 客服弹窗组件 -->
</template>


<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { getUserInfo } from "../api/index";

const { t } = useI18n();
const getImageUrl = name =>
  new URL(`../assets/img/${name}`, import.meta.url).href;

const router = useRouter();
const route = useRoute();
const showCustomerService = ref(false);

const tabs = computed(() => [
  {
    name: t("首页"),
    path: "/",
    icon: getImageUrl("black-tb2.png"),
    iconActive: getImageUrl("black-tb22.png")
  },
  {
    name: t("仓库"),
    path: "/warehouse",
    icon: getImageUrl("black-tb3.png"),
    iconActive: getImageUrl("black-tb33.png")
  },
  {
    name: t("客服"), // 客服，不跳转
    path:
      "https://chatlink.ichatlinks.net/widget/standalone.html?eid=6df096f4e9b05ad245f542d63ed1c8d7&language=en",
    icon: getImageUrl("black-tb4.png"),
    iconActive: getImageUrl("black-tb44.png")
  },
  {
    name: t("我"),
    path: "/me",
    icon: getImageUrl("black-tb1.png"),
    iconActive: getImageUrl("black-tb11.png")
  }
]);

const centerIcon = getImageUrl("gif_1.png");
const currentRoute = computed(() => route.path);

const navigate = (path, name = "") => {
  if (name === "CSKH") {
    window.open(
      "https://chat.ichatlink.net/widget/standalone.html?eid=6df096f4e9b05ad245f542d63ed1c8d7&language=en",
      "_blank"
    );
    return;
  }

  if (path.startsWith("http")) {
    window.open(path, "_blank");
  } else if (path !== currentRoute.value) {
    router.push(path);
  }
};

const onCenterClick = () => {
  router.push("/orderdetail");
};
</script>


<style scoped>
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  max-width: 100%;
  height: 60px;
  background-color: #1d1d1f;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 999;
  padding: 0 10px;
  margin: 0 auto;
}

.tabbar-item {
  flex: 1;
  display: flex;
  font-size: 12px;
  flex-direction: column;
  align-items: center;
  color: #ccc;
  cursor: pointer;
}

.tabbar-item.active {
  color: #dcb671;
  font-weight: bold;
}

.tabbar-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}

.tabbar-middle {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  margin-top: -5px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.center-icon {
  width: 55px;
  height: 55px;
}

/* PC端适配 */
@media screen and (min-width: 768px) {
  .tabbar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    width: 450px;
    height: 60px;
    background-color: #1d1d1f;
    display: flex;
    justify-content: space-around;
    align-items: center;
    z-index: 999;
    padding: 0;
    margin: 0 auto;
  }

  .tabbar-item {
    flex: 1;
    display: flex;
    font-size: 12px;
    flex-direction: column;
    align-items: center;
    color: #ccc;
    cursor: pointer;
  }

  .tabbar-item.active {
    color: #dcb671;
    font-weight: bold;
  }

  .tabbar-icon {
    width: 24px;
    height: 24px;
    margin-bottom: 4px;
  }

  .tabbar-middle {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    margin-top: -5px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }

  .center-icon {
    width: 55px;
    height: 55px;
  }
}
</style>
