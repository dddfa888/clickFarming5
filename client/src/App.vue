<template>
  <div>
    <RouterView />
    <!-- 只在指定页面显示 TabBar -->
    <TabBar v-if="showTabBar" />
  </div>
</template>
<script setup>
import { useRoute } from "vue-router";
import { computed } from "vue";
import TabBar from "./components/TabBar.vue";

const route = useRoute();
const tabbarPaths = ["/", "/warehouse", "/cskh", "/me", "/record"];

const showTabBar = computed(() => tabbarPaths.includes(route.path));
</script>


<style scoped>
/* 仅隐藏滚动条但仍能滚动 */
::-webkit-scrollbar {
  display: none;
}

html,
body,
#app {
  -ms-overflow-style: none; /* IE和Edge */
  scrollbar-width: none; /* Firefox */
}

body,
html,
* {
  cursor: default;
}
html,
body,
#app {
  margin: 0;
  padding: 0;
  height: 100vh;
  width: 100vw;
}

@media screen and (min-width: 768px) {
  html,
  body,
  #app {
    margin: 0 auto;
    padding: 0;
    height: 100vh;
    width: 450px;
  }

  .bottom-nav {
    width: 450px;
    margin: 0 auto;
  }
}
</style>
