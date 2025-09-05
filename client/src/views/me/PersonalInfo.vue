<template>
  <div class="personal-info">
    <HeaderBar title="编辑信息" backcolor="#ece9ee" />
    <ul class="info-list">
      <!-- 头像 -->
      <li class="list-item">
        <div class="left">
          <span class="item-text">头像</span>
        </div>
        <div class="right">
          <img class="arrow-avatar" :src="avatarimg" alt @click="triggerUpload" />
          <input
            type="file"
            id="avatar"
            ref="avatarInput"
            accept="image/*"
            style="display: none"
            @change="handleFileChange"
          />
          <img class="arrow-icon" src="../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 昵称 -->
      <li class="list-item" @click="showNicknameDialog = true">
        <div class="left">
          <span class="item-text">昵称</span>
        </div>
        <div class="right">
          <span class="item-value">{{ loginAccount }}</span>
          <img class="arrow-icon" src="../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 性别 -->
      <li class="list-item" @click="showGenderDialog = true">
        <div class="left">
          <span class="item-text">性别</span>
        </div>
        <div class="right">
          <span class="item-value">{{ sex== 0 ? "女": sex === 1 ? "男" : "保密" }}</span>
          <img class="arrow-icon" src="../../assets/img/right.png" alt />
        </div>
      </li>

      <!-- 签名 -->
      <li class="list-item" @click="showSignatureDialog = true">
        <div class="left">
          <span class="item-text">签名</span>
        </div>
        <div class="right">
          <img class="arrow-icon" src="../../assets/img/right.png" alt />
        </div>
      </li>
    </ul>

    <!-- 修改昵称弹框 -->
    <div v-if="showNicknameDialog" class="dialog-mask">
      <div class="dialog">
        <h3>修改昵称</h3>
        <input v-model="newNickname" class="dialog-input" placeholder="请输入新昵称" />
        <div class="dialog-actions">
          <button @click="showNicknameDialog = false">取消</button>
          <button @click="confirmNickname">确定</button>
        </div>
      </div>
    </div>

    <!-- 修改性别弹框 -->
    <div v-if="showGenderDialog" class="dialog-mask">
      <div class="dialog">
        <h3>选择性别</h3>
        <div class="gender-options">
          <label>
            <input type="radio" value="0" v-model="newGender" />
            女
          </label>
          <label>
            <input type="radio" value="1" v-model="newGender" />
            男
          </label>
          <label>
            <input type="radio" value="2" v-model="newGender" />
            保密
          </label>
        </div>
        <div class="dialog-actions">
          <button @click="showGenderDialog = false">取消</button>
          <button @click="confirmGender">确定</button>
        </div>
      </div>
    </div>

    <!-- 修改签名弹框 -->
    <div v-if="showSignatureDialog" class="dialog-mask">
      <div class="dialog">
        <h3>修改签名</h3>
        <input v-model="newSignature" class="dialog-input" placeholder="请输入新签名" />
        <div class="dialog-actions">
          <button @click="showSignatureDialog = false">取消</button>
          <button @click="confirmSignature">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>


       
<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { getUserInfo, updateAvatar, updateUserInfo } from "../../api/index";
import { useInfoStore } from "../../store/useInfoStore";
import { onMounted, ref, computed } from "vue";
import { showAlert } from "../../utils/notify";

const { t } = useI18n();
const router = useRouter();
const sex = ref("");
const loginAccount = ref("");
const InfoStore = useInfoStore();
const avatarimg = ref("../../assets/image/logo.png");
const gender = ref("0"); // 默认性别

const avatarInput = ref(null);
// 签名
const showSignatureDialog = ref(false);
const newSignature = ref("");

// 修改昵称弹框
const showNicknameDialog = ref(false);
const newNickname = ref("");

// 修改性别弹框
const showGenderDialog = ref(false);
const newGender = ref("0");

const genderLabel = computed(() => (gender.value === "1" ? t("男") : t("女")));

// 触发文件选择
const triggerUpload = () => {
  avatarInput.value && avatarInput.value.click();
};

// 处理文件上传
const handleFileChange = async e => {
  const file = e.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await updateAvatar(formData);
    if (res.code === 200) {
      avatarimg.value = res.data;
      userinfo();
      showAlert(res.msg, 2000);
    } else {
      showAlert(res.msg, 2000);
    }
  } catch (error) {
    console.error("上传异常:", error);
  }
};

// 确认修改昵称
const confirmNickname = async () => {
  if (!newNickname.value.trim()) return;
  try {
    const res = await updateUserInfo({ loginAccount: newNickname.value });
    if (res.code === 200) {
      loginAccount.value = newNickname.value;
      showNicknameDialog.value = false;
      userinfo();
      showAlert(res.msg, 2000);
    } else {
      showNicknameDialog.value = false;
      showAlert(res.msg, 2000);
    }
  } catch (err) {
    console.error("修改昵称失败:", err);
  }
};

// 确认修改性别
const confirmGender = async () => {
  try {
    const res = await updateUserInfo({ sex: Number(newGender.value) });
    if (res.code === 200) {
      gender.value = newGender.value;
      showGenderDialog.value = false;
      userinfo();
      showAlert(res.msg, 2000);
    } else {
      showGenderDialog.value = false;
      showAlert(res.msg, 2000);
    }
  } catch (err) {
    console.error("修改性别失败:", err);
  }
};

// 确认修改签名
const confirmSignature = async () => {
  if (!newSignature.value.trim()) return;
  try {
    const res = await updateUserInfo({ signature: newSignature.value });
    if (res.code === 200) {
      showSignatureDialog.value = false;
      userinfo();
      showAlert(res.msg, 2000);
    } else {
      showSignatureDialog.value = false;
      showAlert(res.msg, 2000);
    }
  } catch (err) {
    console.error("修改签名失败:", err);
  }
};

onMounted(() => {
  userinfo();
});

function userinfo() {
  getUserInfo().then(res => {
    loginAccount.value = res.data.loginAccount;
    avatarimg.value = res.data.headImg || avatarimg.value;
    gender.value = res.data.gender || "female";
    newGender.value = gender.value;
    sex.value = res.data.sex;
  });
}
</script>



<style scoped>
.personal-info {
  background-color: #ece9ee;
  height: 100vh;
}

/* 列表 */
.info-list {
  list-style: none;
  margin: 0;
  padding: 0;
  padding-top: 50px;
}

/* 每一行 */
.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px;
  background-color: #fff;
  cursor: pointer;
  margin: 8px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

/* 左右区块 */
.left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 图标 */
.icon-img {
  width: 20px;
  height: 22px;
}

/* 文字 */
.item-text {
  font-size: 15px;
}

.item-value {
  font-size: 14px;
  color: #aaa;
}

/* 头像 */
.avatar-img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
}

/* 箭头 */
.arrow-icon {
  height: 14px;
  opacity: 0.7;
}

.arrow-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 10px;
}

/* 弹框样式 */
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 280px;
}

.dialog h3 {
  margin: 0 0 12px;
  font-size: 16px;
}

.dialog-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 12px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.dialog-actions button:first-child {
  background: #ddd;
}

.dialog-actions button:last-child {
  background: #4caf50;
  color: #fff;
}

/* 弹框样式 */
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 280px;
}

.dialog h3 {
  margin: 0 0 12px;
  font-size: 16px;
}

.dialog-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 12px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.dialog-actions button:first-child {
  background: #ddd;
}

.dialog-actions button:last-child {
  background: #4caf50;
  color: #fff;
}

.gender-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

@media screen and (min-width: 768px) {
  .personal-info {
    background-color: #ece9ee;
    height: 100vh;
    width: 450px;
    margin: 0 auto;
  }

  /* 列表 */
  .info-list {
    list-style: none;
    margin: 0;
    padding: 0;
    padding-top: 50px;
  }

  /* 每一行 */
  .list-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 20px;
    background-color: #fff;
    cursor: pointer;
    margin: 8px 12px;
    border-radius: 8px;
    transition: background 0.2s;
  }

  /* 左右区块 */
  .left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .right {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  /* 图标 */
  .icon-img {
    width: 20px;
    height: 22px;
  }

  /* 文字 */
  .item-text {
    font-size: 15px;
  }

  .item-value {
    font-size: 14px;
    color: #aaa;
  }

  /* 头像 */
  .avatar-img {
    width: 38px;
    height: 38px;
    border-radius: 50%;
  }

  /* 箭头 */
  .arrow-icon {
    height: 14px;
    opacity: 0.7;
  }

  .arrow-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    margin-right: 10px;
  }

  /* 弹框样式 */
  .dialog-mask {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .dialog {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 280px;
  }

  .dialog h3 {
    margin: 0 0 12px;
    font-size: 16px;
  }

  .dialog-input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 6px;
    margin-bottom: 12px;
  }

  .dialog-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }

  .dialog-actions button {
    padding: 6px 12px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }

  .dialog-actions button:first-child {
    background: #ddd;
  }

  .dialog-actions button:last-child {
    background: #4caf50;
    color: #fff;
  }

  /* 弹框样式 */
  .dialog-mask {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .dialog {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 280px;
  }

  .dialog h3 {
    margin: 0 0 12px;
    font-size: 16px;
  }

  .dialog-input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 6px;
    margin-bottom: 12px;
  }

  .dialog-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }

  .dialog-actions button {
    padding: 6px 12px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }

  .dialog-actions button:first-child {
    background: #ddd;
  }

  .dialog-actions button:last-child {
    background: #4caf50;
    color: #fff;
  }

  .gender-options {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 12px;
  }
}
</style>
