<template>
  <div class="lazy-image" :style="wrapperStyle">
    <!-- 占位层 -->
    <div
      v-if="!isLoaded && !hasError"
      class="lazy-image__placeholder"
      :style="{ backgroundImage: placeholder ? `url(${placeholder})` : '' }"
    />

    <!-- 图片本体 -->
    <img
      ref="imgEl"
      :alt="alt"
      :src="imgSrc"
      :style="imgStyle"
      :loading="supportsNativeLazy ? 'lazy' : undefined"
      @load="onLoad"
      @error="onError"
    />

    <!-- 错误回退 -->
    <div v-if="hasError && !error" class="lazy-image__error">
      <slot name="error">加载失败</slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed, watch } from "vue";

const props = defineProps({
  src: { type: String, required: true },
  alt: { type: String, default: "" },
  placeholder: { type: String, default: "" },
  error: { type: String, default: "" },
  fit: { type: String, default: "cover" }, // cover | contain | fill ...
  ratio: { type: Number, default: 0 }, // 宽高比
  transition: { type: Boolean, default: true },
  lazy: { type: Boolean, default: true }
});

const imgEl = ref(null);
const isLoaded = ref(false);
const hasError = ref(false);
const isVisible = ref(!props.lazy);
const observer = ref(null);

const supportsNativeLazy = "loading" in HTMLImageElement.prototype;

const imgSrc = computed(() => {
  if (!isVisible.value && props.lazy) return props.placeholder || "";
  if (hasError.value && props.error) return props.error;
  return props.src;
});

const wrapperStyle = computed(() => {
  const style = { position: "relative", overflow: "hidden" };
  if (props.ratio) style.aspectRatio = String(props.ratio);
  return style;
});

const imgStyle = computed(() => ({
  width: "100%",
  height: "100%",
  objectFit: props.fit,
  display: "block",
  transition: props.transition ? "opacity 0.3s ease" : undefined,
  opacity: isLoaded.value ? "1" : "0"
}));

function onLoad() {
  isLoaded.value = true;
  hasError.value = false;
}
function onError() {
  hasError.value = true;
}

function observe() {
  if (!props.lazy || isVisible.value || !("IntersectionObserver" in window))
    return;
  unobserve();
  observer.value = new IntersectionObserver(
    entries => {
      if (entries.some(e => e.isIntersecting)) {
        isVisible.value = true;
        unobserve();
      }
    },
    { rootMargin: "200px" }
  );
  if (imgEl.value) observer.value.observe(imgEl.value);
}

function unobserve() {
  if (observer.value) {
    observer.value.disconnect();
    observer.value = null;
  }
}

onMounted(() => observe());
onBeforeUnmount(() => unobserve());

watch(
  () => props.src,
  () => {
    isLoaded.value = false;
    hasError.value = false;
    if (!props.lazy) {
      isVisible.value = true;
    } else {
      observe();
    }
  }
);
</script>

<style scoped>
.lazy-image {
  position: relative;
}
.lazy-image__placeholder {
  position: absolute;
  inset: 0;
  background: #f2f3f5 no-repeat center/cover;
  filter: blur(8px);
}
.lazy-image__error {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  color: #999;
  font-size: 14px;
}
</style>
