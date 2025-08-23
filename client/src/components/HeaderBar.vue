<!-- src/components/HeaderBar.vue -->
<template>
  <div :style="`background-color:${backcolor};`" class="header-bar">
    <van-icon name="arrow-left" class="back-icon" @click="toback" />
    <span class="title">{{ title }}</span>
    <span class="right-placeholder"></span>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";
defineProps({
  title: String,
  backcolor: {
    type: String,
    default: "#121212"
  }
});
const router = useRouter();
function toback() {
  router.go(-1);
}
</script>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  height: 50px;
  width: 100%;
  background-color: #1d1d1f;
  color: white;
  font-size: 16px;
  position: fixed;
  top: 0;
  z-index: 999;
}

.back-icon {
  position: absolute;
  left: 10px;
  font-size: 20px;
}

.title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.right-placeholder {
  width: 24px; /* 预留和返回按钮差不多的宽度，保证标题真居中 */
}

@media screen and (min-width: 768px) {
  .header-bar {
    display: flex;
    align-items: center;
    height: 50px;
    width: 450px;
    background-color: #1d1d1f;
    color: white;
    font-size: 16px;
    position: fixed;
    top: 0;
    z-index: 999;
  }

  .back-icon {
    position: absolute;
    left: 10px;
    font-size: 20px;
  }

  .title {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .right-placeholder {
    width: 24px; /* 预留和返回按钮差不多的宽度，保证标题真居中 */
  }
}
</style>
