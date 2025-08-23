<template>
  <transition name="fade">
    <div v-if="show" class="mobile-alert">
      <div class="alert-content">{{ message }}</div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  show: Boolean,
  message: String
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.mobile-alert {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 12px 20px;
  border-radius: 6px;
  z-index: 9999;
  font-size: 14px;
  text-align: center;
}

@media screen and (min-width: 768px) {
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.3s;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
  .mobile-alert {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(0, 0, 0, 0.8);
    color: #fff;
    padding: 12px 20px;
    border-radius: 6px;
    z-index: 9999;
    font-size: 14px;
    text-align: center;
  }
}
</style>
