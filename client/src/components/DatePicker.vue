<template>
  <div class="picker-mask">
    <div class="picker-box">
      <div class="picker-header">Date</div>

      <div class="picker-body">
        <!-- 年 -->
        <div class="picker-column" ref="yearCol" @scroll="onScroll('year')">
          <ul>
            <li v-for="y in years" :key="y" :class="{active: y===year}">{{ y }}</li>
          </ul>
        </div>

        <!-- 月 -->
        <div class="picker-column" ref="monthCol" @scroll="onScroll('month')">
          <ul>
            <li
              v-for="m in months"
              :key="m"
              :class="{active: m===month}"
            >{{ String(m).padStart(2,'0') }}</li>
          </ul>
        </div>

        <!-- 日 -->
        <div class="picker-column" ref="dayCol" @scroll="onScroll('day')">
          <ul>
            <li
              v-for="d in days"
              :key="d"
              :class="{active: d===day}"
            >{{ String(d).padStart(2,'0') }}</li>
          </ul>
        </div>

        <div class="highlight-bar"></div>
      </div>

      <div class="picker-footer">
        <button class="cancel" @click="$emit('cancel')">Cancel</button>
        <button class="confirm" @click="confirm">Confirm</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch, defineEmits } from "vue";

const now = new Date();
const year = ref(now.getFullYear());
const month = ref(now.getMonth() + 1);
const day = ref(now.getDate());

const years = Array.from(
  { length: now.getFullYear() - 2000 + 1 },
  (_, i) => 2000 + i
);

const months = computed(() => {
  if (year.value === now.getFullYear()) {
    return Array.from({ length: now.getMonth() + 1 }, (_, i) => i + 1);
  } else {
    return Array.from({ length: 12 }, (_, i) => i + 1);
  }
});

const daysIn = (y, m) => new Date(y, m, 0).getDate();
const days = computed(() => {
  if (year.value === now.getFullYear() && month.value === now.getMonth() + 1) {
    return Array.from({ length: now.getDate() }, (_, i) => i + 1);
  } else {
    return Array.from(
      { length: daysIn(year.value, month.value) },
      (_, i) => i + 1
    );
  }
});

const yearCol = ref(null);
const monthCol = ref(null);
const dayCol = ref(null);

const ROW_HEIGHT = 40;
const PADDING = ROW_HEIGHT; // ul padding-top = 40px

const scrollTimers = { year: null, month: null, day: null };

// 滚动到目标值居中
const scrollToValue = (type, value, smooth = true) => {
  let col, list;
  if (type === "year") {
    col = yearCol.value;
    list = years;
  } else if (type === "month") {
    col = monthCol.value;
    list = months.value;
  } else {
    col = dayCol.value;
    list = days.value;
  }
  if (!col || !list?.length) return;
  const idx = list.indexOf(value);
  if (idx < 0) return;
  col.scrollTo({
    top: idx * ROW_HEIGHT,
    behavior: smooth ? "smooth" : "auto"
  });

  // 强制对齐到中间位置
  const targetTop = idx * ROW_HEIGHT;
  // 增加微小延迟确保计算准确
  setTimeout(() => {
    col.scrollTo({
      top: targetTop,
      behavior: smooth ? "smooth" : "auto"
    });
  }, 0);
};

// 滚动事件处理
const onScroll = type => {
  let col, list, setter;
  if (type === "year") {
    col = yearCol.value;
    list = years;
    setter = v => (year.value = v);
  } else if (type === "month") {
    col = monthCol.value;
    list = months.value;
    setter = v => (month.value = v);
  } else {
    col = dayCol.value;
    list = days.value;
    setter = v => (day.value = v);
  }
  if (!col || !list?.length) return;

  clearTimeout(scrollTimers[type]);
  scrollTimers[type] = setTimeout(() => {
    const scrollTop = col.scrollTop;
    // 修正 padding 影响
    const idx = Math.round((scrollTop + PADDING) / ROW_HEIGHT) - 1;
    const index = Math.max(0, Math.min(idx, list.length - 1));
    setter(list[index]);
    col.scrollTo({ top: index * ROW_HEIGHT, behavior: "smooth" });
  }, 50);
};

// 年/月变化时夹紧月份和日期
watch([year, month], async () => {
  const maxMonth = year.value === now.getFullYear() ? now.getMonth() + 1 : 12;
  if (month.value > maxMonth) month.value = maxMonth;
  await nextTick();
  scrollToValue("month", month.value, false);

  const maxD =
    year.value === now.getFullYear() && month.value === now.getMonth() + 1
      ? now.getDate()
      : daysIn(year.value, month.value);
  if (day.value > maxD) day.value = maxD;
  await nextTick();
  scrollToValue("day", day.value, false);
});

// 初始化滚动到今天
onMounted(async () => {
  await nextTick();
  scrollToValue("year", year.value, false);
  scrollToValue("month", month.value, false);
  scrollToValue("day", day.value, false);
});

const emit = defineEmits(["cancel", "confirm"]);
const confirm = () => {
  emit(
    "confirm",
    `${year.value}-${String(month.value).padStart(2, "0")}-${String(
      day.value
    ).padStart(2, "0")}`
  );
};
</script>

<style scoped>
.picker-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}
.picker-box {
  background: #fff;
  border-radius: 8px;
  width: 80%;
  max-width: 360px;
  overflow: hidden;
}
.picker-header {
  text-align: center;
  padding: 10px;
  background: #2c3e50;
  color: #fff;
  font-weight: bold;
}
.picker-body {
  display: flex;
  justify-content: space-around;
  margin: 10px;
  border-radius: 8px;
  position: relative;
  height: 120px;
  overflow: hidden;
}
.picker-column {
  flex: 1;
  text-align: center;
  height: 100%;
  overflow-y: scroll;
  scroll-snap-type: y mandatory;
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.picker-column::-webkit-scrollbar {
  display: none;
}
.picker-column ul {
  list-style: none;
  padding: 40px 0;
  margin: 0;
}
.picker-column li {
  height: 40px;
  line-height: 40px;
  cursor: pointer;
  color: #666;
  scroll-snap-align: center;
}
.picker-column li.active {
  color: #000;
  font-weight: 700;
}
.highlight-bar {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 40px;
  background: rgba(57, 61, 73, 0.08);
  border-top: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
  transform: translateY(-50%);
  pointer-events: none;
}

.picker-column ul {
  min-height: 200px; /* 确保列表有足够高度 */
}
.picker-footer {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: #fff;
}
.picker-footer button {
  flex: 1;
  margin: 0 5px;
  padding: 8px 0;
  border: none;
  border-radius: 6px;
}
.cancel {
  background: #dcdddd;
  color: #666;
}
.confirm {
  background: #393d49;
  color: #fff;
}

@media screen and (min-width: 768px) {
  .picker-mask {
    width: 450px;
    margin: 0 auto;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .picker-box {
    background: #fff;
    border-radius: 8px;
    width: 80%;
    max-width: 360px;
    overflow: hidden;
  }
  .picker-header {
    text-align: center;
    padding: 10px;
    background: #2c3e50;
    color: #fff;
    font-weight: bold;
  }
  .picker-body {
    display: flex;
    justify-content: space-around;
    margin: 10px;
    border-radius: 8px;
    position: relative;
    height: 120px;
    overflow: hidden;
  }
  .picker-column {
    flex: 1;
    text-align: center;
    height: 100%;
    overflow-y: scroll;
    scroll-snap-type: y mandatory;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
  .picker-column::-webkit-scrollbar {
    display: none;
  }
  .picker-column ul {
    list-style: none;
    padding: 40px 0;
    margin: 0;
  }
  .picker-column li {
    height: 40px;
    line-height: 40px;
    cursor: pointer;
    color: #666;
    scroll-snap-align: center;
  }
  .picker-column li.active {
    color: #000;
    font-weight: 700;
  }
  .highlight-bar {
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 40px;
    background: rgba(57, 61, 73, 0.08);
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc;
    transform: translateY(-50%);
    pointer-events: none;
  }

  .picker-column ul {
    min-height: 200px; /* 确保列表有足够高度 */
  }
  .picker-footer {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    background: #fff;
  }
  .picker-footer button {
    flex: 1;
    margin: 0 5px;
    padding: 8px 0;
    border: none;
    border-radius: 6px;
  }
  .cancel {
    background: #dcdddd;
    color: #666;
  }
  .confirm {
    background: #393d49;
    color: #fff;
  }
  .picker-mask {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .picker-box {
    background: #fff;
    border-radius: 8px;
    width: 80%;
    max-width: 360px;
    overflow: hidden;
  }
  .picker-header {
    text-align: center;
    padding: 10px;
    background: #2c3e50;
    color: #fff;
    font-weight: bold;
  }
  .picker-body {
    display: flex;
    justify-content: space-around;
    margin: 10px;
    border-radius: 8px;
    position: relative;
    height: 120px;
    overflow: hidden;
  }
  .picker-column {
    flex: 1;
    text-align: center;
    height: 100%;
    overflow-y: scroll;
    scroll-snap-type: y mandatory;
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
  .picker-column::-webkit-scrollbar {
    display: none;
  }
  .picker-column ul {
    list-style: none;
    padding: 40px 0;
    margin: 0;
  }
  .picker-column li {
    height: 40px;
    line-height: 40px;
    cursor: pointer;
    color: #666;
    scroll-snap-align: center;
  }
  .picker-column li.active {
    color: #000;
    font-weight: 700;
  }
  .highlight-bar {
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 40px;
    background: rgba(57, 61, 73, 0.08);
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc;
    transform: translateY(-50%);
    pointer-events: none;
  }

  .picker-column ul {
    min-height: 200px; /* 确保列表有足够高度 */
  }
  .picker-footer {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    background: #fff;
  }
  .picker-footer button {
    flex: 1;
    margin: 0 5px;
    padding: 8px 0;
    border: none;
    border-radius: 6px;
  }
  .cancel {
    background: #dcdddd;
    color: #666;
  }
  .confirm {
    background: #393d49;
    color: #fff;
  }
}
</style>
