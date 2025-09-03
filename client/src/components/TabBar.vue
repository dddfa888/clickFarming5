<template>
  <div class="tabbar">
    <div
      class="tabbar-item"
      v-for="(item, index) in tabs"
      :key="index"
      :class="{ active: currentRoute === item.path }"
      @click="navigate(item.path, item.name)"
    >
      <img :src="currentRoute === item.path ? item.iconActive : item.icon" class="tabbar-icon" />
      <span style="text-align: center;">{{ $t(item.name)}}</span>
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
  new URL(`../assets/image/${name}`, import.meta.url).href;

const router = useRouter();
const route = useRoute();
const showCustomerService = ref(false);

const tabs = computed(() => [
  {
    name: t("发现"),
    path: "/",
    icon: getImageUrl("home.svg"),
    iconActive: getImageUrl("homeactive.svg")
  },
  {
    name: t("聚品坊"),
    path: "/warehouse",
    icon: getImageUrl("shop.svg"),
    iconActive: getImageUrl("shopactive.svg")
  },
  {
    name: t("记录"),
    path: "/record",
    icon: getImageUrl("jl.svg"),
    iconActive: getImageUrl("jlactive.svg")
  },
  {
    name: t("个人中心"),
    path: "/me",
    icon: getImageUrl("gr.svg"),
    iconActive: getImageUrl("gractive.svg")
  }
]);

const currentRoute = computed(() => route.path);

const navigate = (path, name = "") => {
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
  background-color: #e4e2e2;
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
    background-color: #e4e2e2;
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
