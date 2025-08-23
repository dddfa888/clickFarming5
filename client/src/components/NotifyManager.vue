<script setup>
import { ref } from 'vue';
import Notify from './Notify.vue';

const notices = ref([]);

function addNotice(options) {
  const id = Date.now() + Math.random(); // 确保唯一
  notices.value.push({ id, ...options });

  setTimeout(() => {
    notices.value = notices.value.filter((n) => n.id !== id);
  }, options.duration || 3000);
}

defineExpose({ addNotice }); // 必须暴露给外部
</script>

<template>
  <div class="notify-container">
    <Notify
      v-for="item in notices"
      :key="item.id"
      v-bind="item"
      @close="notices = notices.filter(n => n.id !== item.id)"
    />
  </div>
</template>
