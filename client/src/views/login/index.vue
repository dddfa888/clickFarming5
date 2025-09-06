<template>
  <div class="login-container">
    <!-- 语言选择器 
    <div class="language-selector">
      <div class="dropdown-wrapper" @click="toggleLangList">
        <img :src="flagMap[langStore.locale]" class="flag-icon" />
        {{ selectedLanguage }}
      </div>
      <ul class="lang-dropdown" v-if="showLangList">
        <li
          v-for="lang in languageList"
          :key="lang"
          :class="{ active: selectedLanguage === lang }"
          @click="selectLanguage(lang)"
        >
          <img :src="flagMap[langMap[lang]]" class="flag-icon" />
          {{ lang }}
        </li>
      </ul>
    </div>-->

    <!-- 应用Logo和名称 -->
    <div class="app-logo">
      <div class="logo-icon">
        <img src="../../assets/image/logo.png" alt="聚品坊" />
      </div>
      <div class="app-name">聚品坊</div>
    </div>

    <!-- 登录表单 -->
    <div class="login-form">
      <form>
        <!-- 用户名输入框 -->
        <div class="form-group username-group">
          <div class="input-icon">
            <img src="../../assets/image/user.svg" alt />
          </div>
          <input type="text" v-model="form.loginAccount" placeholder="输入用户名" />
        </div>

        <!-- 密码输入框 -->
        <div class="form-group password-group">
          <div class="input-icon">
            <img src="../../assets/image/pwd.svg" alt />
          </div>
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model="form.loginPassword"
            placeholder="输入密码"
          />
          <div class="eye-icon" @click="togglePasswordVisibility">
            <i :class="showPassword ? 'eye-open' : 'eye-closed'"></i>
          </div>
        </div>

        <!-- 登录链接 -->
        <div class="login-link">
          <span>{{ "没有账号？" }}</span>
          <a @click="router.push('/register')">{{ "立即注册" }}</a>
        </div>

        <!-- 登录按钮 -->
        <div class="login-btn">
          <button @click.prevent="onSubmit" type="submit">{{ "登录" }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "../../api/index.js";
import { useI18n } from "vue-i18n";
import { useLangStore } from "../../store/useLangStore.js";
import { useInfoStore } from "../../store/useInfoStore.js";
import { showAlert } from "../../utils/notify";

const router = useRouter();
const form = reactive({
  loginAccount: "",
  loginPassword: ""
});

const showPassword = ref(false);
const agreedToTerms = ref(true); // 默认勾选用户协议

const flagMap = {
  en: new URL("../../assets/img/en-ww.png", import.meta.url).href,
  zhTW: new URL("../../assets/img/zh-tw.png", import.meta.url).href,
  ko: new URL("../../assets/img/ko-kr.png", import.meta.url).href,
  ja: new URL("../../assets/img/jr-rbg.png", import.meta.url).href,
  th: new URL("../../assets/img/th-th.png", import.meta.url).href,
  es: new URL("../../assets/img/es-mx.png", import.meta.url).href,
  vi: new URL("../../assets/img/iv-vn.png", import.meta.url).href,
  tr: new URL("../../assets/img/es-mx.png", import.meta.url).href,
  hi: new URL("../../assets/img/the.png", import.meta.url).href,
  pt: new URL("../../assets/img/pt-br.png", import.meta.url).href
};

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

const langStore = useLangStore();
const { locale } = useI18n();
const { t } = useI18n();
const showLangList = ref(false);
const langMap = {
  한국인: "ko",
  Português: "pt",
  English: "en",
  繁体中文: "zhTW",
  ภาษาไทย: "th",
  日本語: "ja",
  Español: "es",
  "Việt Nam": "vi",
  Turkey: "tr",
  हिंदी: "hi",
  中文: "zh"
};
const languageList = Object.keys(langMap);
const reverseLangMap = Object.fromEntries(
  Object.entries(langMap).map(([k, v]) => [v, k])
);

// 初始化选中语言
const selectedLanguage = ref(reverseLangMap[langStore.locale]);
locale.value = langStore.locale;

function selectLanguage(lang) {
  selectedLanguage.value = lang;
  const langCode = langMap[lang] || "zh";
  langStore.setLocale(langCode);
  locale.value = langCode;
  showLangList.value = !showLangList.value;
}

function toggleLangList() {
  showLangList.value = !showLangList.value;
}

const infoStore = useInfoStore();
function onSubmit(values) {
  login(form).then(res => {
    if (res.code === 200) {
      showAlert(res.msg, 2000);
      localStorage.setItem("token", res.data.token);
      infoStore.setUserinfo(res.data.date);
      router.push("/");
    } else {
      showAlert(res.msg, 2000);
    }
  });
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  max-width: 450px;
  margin: 0 auto;
  padding: 0;
  box-sizing: border-box;
  background-color: #f5f5f5;
}

/* 状态栏样式 */
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 5px 15px;
  background-color: #ffffff;
  height: 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
}

.status-icons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.camera-hole {
  width: 8px;
  height: 8px;
  background-color: #000;
  border-radius: 50%;
}

.right-icons {
  display: flex;
  gap: 5px;
}

.icon-signal::before {
  content: "📶";
  font-size: 12px;
}

.icon-wifi::before {
  content: "📡";
  font-size: 12px;
}

.icon-battery::before {
  content: "🔋";
  font-size: 12px;
}

/* 应用Logo和名称 */
.app-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 80px;
  margin-bottom: 40px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

.logo-icon img {
  width: 100%;
  height: 100%;
  border-radius: 10px;
}

.app-name {
  font-size: 24px;
  font-weight: bold;
  color: #e74c3c;
}

/* 登录表单 */
.login-form {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.form-group {
  position: relative;
  margin-bottom: 20px;
  background-color: #ffff;
  border-radius: 5px;
  display: flex;
  align-items: center;
  height: 50px;
}

.input-icon {
  width: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.input-icon img {
  width: 50%;
}

.user-icon::before {
  content: "";
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url("../../assets/img/user-icon.png") no-repeat center center /
    contain;
  opacity: 0.7;
}

.lock-icon::before {
  content: "";
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url("../../assets/img/lock-icon.png") no-repeat center center /
    contain;
  opacity: 0.7;
}

.form-group input {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  padding: 0 10px;
  font-size: 16px;
  color: #333;
  outline: none;
}

.eye-icon {
  width: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.eye-open::before {
  content: "";
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url("../../assets/img/eye-open.png") no-repeat center center /
    contain;
  opacity: 0.9;
}

.eye-closed::before {
  content: "";
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url("../../assets/img/eye-closed.png") no-repeat center center /
    contain;
  opacity: 0.7;
}

/* 登录按钮 */
.login-btn {
  margin-top: 30px;
  margin-bottom: 20px;
}

.login-btn button {
  width: 100%;
  height: 50px;
  background-color: #ffff;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  color: #f1c40f;
}

/* 用户协议 */
.agreement {
  display: flex;
  align-items: center;
  margin-top: 20px;
  font-size: 14px;
}

.checkbox-container {
  position: relative;
  display: inline-block;
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.checkbox-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  height: 18px;
  width: 18px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 3px;
}

.checkbox-container input:checked ~ .checkmark {
  background-color: #2196f3;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

.checkbox-container input:checked ~ .checkmark:after {
  display: block;
}

.checkbox-container .checkmark:after {
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.agreement-text {
  color: #666;
}

.agreement-text a {
  color: #2196f3;
  text-decoration: none;
}

.lang-dropdown li {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
  transition: background-color 0.3s;
  white-space: nowrap;
}
.lang-dropdown li:hover,
.lang-dropdown li.active {
  background-color: #d3d3d3;
  color: #000;
}

/* 样式 */
.flag-icon {
  width: 18px;
  height: 12px;
  margin-right: 6px;
  object-fit: cover;
  border-radius: 2px;
}
.lang-dropdown li {
  display: flex;
  align-items: center;
}
.dropdown-wrapper {
  display: flex;
  align-items: center;
}

/* 语言选择器 - 移动端 */
.language-selector {
  position: absolute;
  top: 30px;
  right: 15px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
  z-index: 20;
}
.language-selector .label {
  margin-right: 6px;
  color: #333;
}
.dropdown-wrapper {
  position: relative;
  background: rgba(245, 245, 245, 0.9);
  padding: 5px 10px;
  border-radius: 6px;
  cursor: pointer;
  color: #333;
  user-select: none;
}
.lang-dropdown {
  position: absolute;
  top: 35px;
  right: 0;
  background: #ffffff;
  color: #333;
  list-style: none;
  padding: 5px 0;
  margin: 0;
  border-radius: 4px;
  width: 120px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 999;
}

.icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
}

.eye-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  width: 24px;
  height: 24px;
}

.icon-eye {
  width: 24px;
  height: 24px;
  color: #999;
}

.service-img {
  position: absolute;
  bottom: 30px;
  right: 20px;
  width: 50px;
  height: 50px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #f1c40f;
  text-decoration: none;
  margin-left: 5px;
  cursor: pointer;
}

/* PC端适配 */
@media screen and (min-width: 768px) {
  .login-container {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 100vh;
    width: 100%;
    max-width: 450px;
    margin: 0 auto;
    padding: 0;
    box-sizing: border-box;
    background-color: #f5f5f5;
  }

  /* 状态栏样式 */
  .status-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 5px 15px;
    background-color: #ffffff;
    height: 24px;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 10;
  }

  .status-icons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }

  .camera-hole {
    width: 8px;
    height: 8px;
    background-color: #000;
    border-radius: 50%;
  }

  .right-icons {
    display: flex;
    gap: 5px;
  }

  .icon-signal::before {
    content: "📶";
    font-size: 12px;
  }

  .icon-wifi::before {
    content: "📡";
    font-size: 12px;
  }

  .icon-battery::before {
    content: "🔋";
    font-size: 12px;
  }

  /* 应用Logo和名称 */
  .app-logo {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 80px;
    margin-bottom: 40px;
  }

  .logo-icon {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 10px;
  }

  .logo-icon img {
    width: 100%;
    height: 100%;
    border-radius: 10px;
  }

  .app-name {
    font-size: 24px;
    font-weight: bold;
    color: #e74c3c;
  }

  /* 登录表单 */
  .login-form {
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;
  }

  .form-group {
    position: relative;
    margin-bottom: 20px;
    background-color: #ffff;
    border-radius: 5px;
    display: flex;
    align-items: center;
    height: 50px;
  }

  .input-icon {
    width: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .input-icon img {
    width: 50%;
  }

  .user-icon::before {
    content: "";
    display: inline-block;
    width: 20px;
    height: 20px;
    background: url("../../assets/img/user-icon.png") no-repeat center center /
      contain;
    opacity: 0.7;
  }

  .lock-icon::before {
    content: "";
    display: inline-block;
    width: 20px;
    height: 20px;
    background: url("../../assets/img/lock-icon.png") no-repeat center center /
      contain;
    opacity: 0.7;
  }

  .form-group input {
    flex: 1;
    height: 100%;
    border: none;
    background: transparent;
    padding: 0 10px;
    font-size: 16px;
    color: #333;
    outline: none;
  }

  .eye-icon {
    width: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }

  .eye-open::before {
    content: "";
    display: inline-block;
    width: 20px;
    height: 20px;
    background: url("../../assets/img/eye-open.png") no-repeat center center /
      contain;
    opacity: 0.9;
  }

  .eye-closed::before {
    content: "";
    display: inline-block;
    width: 20px;
    height: 20px;
    background: url("../../assets/img/eye-closed.png") no-repeat center center /
      contain;
    opacity: 0.7;
  }

  /* 登录按钮 */
  .login-btn {
    margin-top: 30px;
    margin-bottom: 20px;
  }

  .login-btn button {
    width: 100%;
    height: 50px;
    background-color: #ffff;
    border: none;
    border-radius: 5px;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    color: #f1c40f;
  }

  /* 用户协议 */
  .agreement {
    display: flex;
    align-items: center;
    margin-top: 20px;
    font-size: 14px;
  }

  .checkbox-container {
    position: relative;
    display: inline-block;
    width: 20px;
    height: 20px;
    margin-right: 10px;
  }

  .checkbox-container input {
    position: absolute;
    opacity: 0;
    cursor: pointer;
    height: 0;
    width: 0;
  }

  .checkmark {
    position: absolute;
    top: 0;
    left: 0;
    height: 18px;
    width: 18px;
    background-color: #f5f5f5;
    border: 1px solid #ddd;
    border-radius: 3px;
  }

  .checkbox-container input:checked ~ .checkmark {
    background-color: #2196f3;
  }

  .checkmark:after {
    content: "";
    position: absolute;
    display: none;
  }

  .checkbox-container input:checked ~ .checkmark:after {
    display: block;
  }

  .checkbox-container .checkmark:after {
    left: 6px;
    top: 2px;
    width: 5px;
    height: 10px;
    border: solid white;
    border-width: 0 2px 2px 0;
    transform: rotate(45deg);
  }

  .agreement-text {
    color: #666;
  }

  .agreement-text a {
    color: #2196f3;
    text-decoration: none;
  }

  .lang-dropdown li {
    padding: 8px 12px;
    cursor: pointer;
    font-size: 13px;
    transition: background-color 0.3s;
    white-space: nowrap;
  }
  .lang-dropdown li:hover,
  .lang-dropdown li.active {
    background-color: #d3d3d3;
    color: #000;
  }

  /* 样式 */
  .flag-icon {
    width: 18px;
    height: 12px;
    margin-right: 6px;
    object-fit: cover;
    border-radius: 2px;
  }
  .lang-dropdown li {
    display: flex;
    align-items: center;
  }
  .dropdown-wrapper {
    display: flex;
    align-items: center;
  }

  /* 语言选择器 - 移动端 */
  .language-selector {
    position: absolute;
    top: 30px;
    right: 15px;
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #333;
    z-index: 20;
  }
  .language-selector .label {
    margin-right: 6px;
    color: #333;
  }
  .dropdown-wrapper {
    position: relative;
    background: rgba(245, 245, 245, 0.9);
    padding: 5px 10px;
    border-radius: 6px;
    cursor: pointer;
    color: #333;
    user-select: none;
  }
  .lang-dropdown {
    position: absolute;
    top: 35px;
    right: 0;
    background: #ffffff;
    color: #333;
    list-style: none;
    padding: 5px 0;
    margin: 0;
    border-radius: 4px;
    width: 120px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    z-index: 999;
  }

  .icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
  }

  .eye-icon {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translateY(-50%);
    cursor: pointer;
    width: 24px;
    height: 24px;
  }

  .icon-eye {
    width: 24px;
    height: 24px;
    color: #999;
  }

  .service-img {
    position: absolute;
    bottom: 30px;
    right: 20px;
    width: 50px;
    height: 50px;
  }

  .login-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
    color: #666;
  }

  .login-link a {
    color: #f1c40f;
    text-decoration: none;
    margin-left: 5px;
    cursor: pointer;
  }
}
</style>
