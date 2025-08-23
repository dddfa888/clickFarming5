<template>
  <!-- 页面整体容器 -->
  <div class="set-avatar-page">
    <!-- 顶部区域 -->
    <HeaderBar :title="t('设置头像')" />
    <div class="top-section">
      <div class="background">
        <!-- 可替换为实际背景图地址，这里用纯色模拟星空效果 -->
        <div class="avatar-preview">
          <LazyImage
            v-if="currentAvatar"
            :src="currentAvatar"
            alt="当前头像"
            :ratio="16/9"
            placeholder="../../../assets/img/images.png"
            class="current-avatar"
          />
          <LazyImage
            class="current-avatar"
            :ratio="16/9"
            v-else
            src="../../../assets/img/black-tx.png"
            placeholder="../../../assets/img/images.png"
            alt
          />
          <button class="set-btn" @click="handleSet">{{ $t("立即设定") }}</button>
        </div>
      </div>
    </div>
    <!-- 头像列表区域 -->
    <div class="avatar-list">
      <div
        v-for="(avatar, index) in avatarList"
        :key="index"
        class="avatar-item"
        @click="handleSelectAvatar(avatar)"
      >
        <LazyImage
          :src="avatar"
          :ratio="16/9"
          alt="头像"
          placeholder="../../../assets/img/images.png"
          class="avatar"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import HeaderBar from "../../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { updateAvatar, getUserInfo } from "../../../api/index";
import { showAlert } from "../../../utils/notify.js";
import router from "../../../router";
import LazyImage from "../../../components/LazyImage.vue";

const { t } = useI18n();

// 批量引入 assets/img 下的 png 图片
const modules = import.meta.glob("../../../assets/avatars/*.png", {
  eager: true
});

// 把对象转成数组，并按照文件名中的数字排序
const avatarList = ref(
  Object.keys(modules)
    .sort((a, b) => {
      const numA = parseInt(a.match(/(\d+)\.png$/)?.[1] || 0, 10);
      const numB = parseInt(b.match(/(\d+)\.png$/)?.[1] || 0, 10);
      return numA - numB;
    })
    .map(key => modules[key].default)
);

// 当前选中头像
const currentAvatar = ref();

// 选择头像
const handleSelectAvatar = avatar => {
  currentAvatar.value = avatar;
};

// 确认设定
const handleSet = async () => {
  // 把图片 URL 转成 Blob
  const response = await fetch(currentAvatar.value);
  const blob = await response.blob();
  const file = new File([blob], "avatar.png", { type: blob.type });

  // 放进 FormData
  const formData = new FormData();
  formData.append("file", file);

  // 调用接口
  updateAvatar(formData).then(res => {
    console.log("上传结果：", res.url);
    if (res.code == 200) {
      showAlert(t("上传成功"), 2000);
      router.back();
    } else {
      showAlert(t("上传失败"), 2000);
    }
  });
};

onMounted(async () => {
  getUserInfo().then(res => {
    currentAvatar.value = res.data.headImg;
  });
});
</script>

<style scoped>
/* 页面整体样式 */
.set-avatar-page {
  width: 100%;
  height: 100vh;
  background-color: #121212; /* 深色背景模拟星空外的底色 */
  overflow: auto;
}

/* 顶部区域样式 */
.top-section {
  position: relative;
  width: 100%;
  height: 180px;
  /* 背景图可替换，这里用渐变模拟星空效果，实际可换成星空背景图 */
  background: linear-gradient(135deg, #0b0f2b, #1b224b);
}
.background {
  width: 100%;
  height: 100%;
  background: url("../../../assets/img/txbj.png") no-repeat center center;
  background-size: cover;
}
.avatar-preview {
  width: 60px;
  height: 60px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
.current-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid #fff;
}
.set-btn {
  margin-top: 10px;
  background-color: transparent;
  color: #fff;
  white-space: nowrap;
  font-size: 14px;
  border-radius: 4px;
  padding: 5px;
  border: 1px solid #dcc897;
  cursor: pointer;
}

/* 头像列表样式 */
.avatar-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  padding: 10px;
  gap: 10px;
}
.avatar-item {
  width: 60px;
  height: 60px;
  cursor: pointer;
}
.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid transparent;
  transition: border-color 0.3s;
}
.avatar-item:hover .avatar {
  border-color: #409eff;
}

@media screen and (min-width: 768px) {
  /* 页面整体样式 */
  .set-avatar-page {
    width: 450px;
    height: 100vh;
    background-color: #121212; /* 深色背景模拟星空外的底色 */
    margin: 0 auto;
    overflow: auto;
  }

  /* 顶部区域样式 */
  .top-section {
    position: relative;
    width: 100%;
    height: 180px;
    /* 背景图可替换，这里用渐变模拟星空效果，实际可换成星空背景图 */
    background: linear-gradient(135deg, #0b0f2b, #1b224b);
  }
  .background {
    width: 100%;
    height: 100%;
    background: url("../../../assets/img/txbj.png") no-repeat center center;
    background-size: cover;
  }
  .avatar-preview {
    width: 60px;
    height: 60px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
  }
  .current-avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 2px solid #fff;
  }
  .set-btn {
    margin-top: 10px;
    background-color: transparent;
    color: #fff;
    white-space: nowrap;
    font-size: 14px;
    border-radius: 4px;
    padding: 5px;
    border: 1px solid #dcc897;
    cursor: pointer;
  }
  /* 头像列表样式 */
  .avatar-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    padding: 10px;
    gap: 10px;
  }
  .avatar-item {
    width: 60px;
    height: 60px;
    cursor: pointer;
  }
  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 2px solid transparent;
    transition: border-color 0.3s;
  }
  .avatar-item:hover .avatar {
    border-color: #409eff;
  }
}
</style>