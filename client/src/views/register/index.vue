<template>
  <div class="register-container">
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
        <img src="../../assets/image/shoplogo.png" />
      </div>
      <div class="app-name">多元优选</div>
    </div>

    <!-- 注册表单 -->
    <div class="register-form">
      <form>
        <!-- 用户名输入框 -->
        <div class="form-group username-group">
          <div class="input-icon">
            <img src="../../assets/image/user.svg" alt />
          </div>
          <input type="text" v-model="form.loginAccount" placeholder="输入用户名" />
        </div>

        <!-- 手机号带区号 -->
        <div class="form-group phone-group">
          <div class="input-icon">
            <img src="../../assets/image/phone.svg" alt />
          </div>
          <select v-model="form.areaCode" class="area-select">
            <option value="+84">+84</option>
            <option value="+65">+65</option>
            <option value="+66">+66</option>
            <option value="+62">+62</option>
            <option value="+60">+60</option>
            <option value="+63">+63</option>
            <option value="+27">+27</option>
            <option value="+20">+20</option>
            <option value="+234">+234</option>
            <option value="+1">+1</option>
            <option value="+44">+44</option>
            <option value="+61">+61</option>
            <option value="+82">+82</option>
            <option value="+35">+55</option>
            <option value="+52">+52</option>
            <option value="+7">+7</option>
            <option value="+39">+39</option>
            <option value="+34">+34</option>
            <option value="+86">+86</option>
            <option value="+91">+91</option>
            <option value="+49">+49</option>
            <option value="+33">+33</option>
            <option value="+81">+81</option>
            <option value="+886">+886</option>
            <option value="+852">+852</option>
            <option value="+64">+64</option>
            <option value="+996">+996</option>
            <option value="+971">+971</option>
            <option value="+90">+90</option>
          </select>
          <input v-model="form.phone" type="tel" placeholder="请输入电话号码" class="phone-input" />
        </div>

        <!-- 密码输入框 -->
        <div class="form-group password-group">
          <div class="input-icon">
            <img src="../../assets/image/pwd.svg" alt />
          </div>
          <input
            :type="passwordVisible.loginPassword ? 'text' : 'password'"
            v-model="form.loginPassword"
            placeholder="请输入密码"
          />
          <div class="eye-icon" @click="togglePassword('loginPassword')">
            <i :class="passwordVisible.loginPassword ? 'eye-open' : 'eye-closed'"></i>
          </div>
        </div>

        <!-- 确认密码输入框 -->
        <div class="form-group password-group">
          <div class="input-icon">
            <img src="../../assets/image/pwd.svg" alt />
          </div>
          <input
            :type="passwordVisible.fundPassword ? 'text' : 'password'"
            v-model="form.fundPassword"
            placeholder="请输入确认密码"
          />
          <div class="eye-icon" @click="togglePassword('fundPassword')">
            <i :class="passwordVisible.fundPassword ? 'eye-open' : 'eye-closed'"></i>
          </div>
        </div>

        <!-- 邀请码输入框 -->
        <div class="form-group invite-group">
          <div class="input-icon">
            <img src="../../assets/image/init.svg" alt />
          </div>
          <input type="text" v-model="form.invitationCode" placeholder="请输入邀请码" />
        </div>

        <!-- 登录链接 -->
        <div class="login-link">
          <span>已有账号？</span>
          <a @click="router.push('/login')">立即登录</a>
        </div>

        <!-- 注册按钮 -->
        <div class="register-btn">
          <button @click.prevent="onSubmit" type="submit">注册</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { register } from "../../api/index.js";
import { useRouter, useRoute } from "vue-router";
import { showAlert } from "../../utils/notify.js";
import { useLangStore } from "../../store/useLangStore.js";
import { useI18n } from "vue-i18n";

const { t, locale } = useI18n();
const router = useRouter();
const route = useRoute();
const langStore = useLangStore();
const form = reactive({
  loginAccount: "",
  areaCode: "+86",
  phone: "",
  loginPassword: "",
  fundPassword: "",
  invitationCode: ""
});

// 用户协议勾选状态
const agreedToTerms = ref(false);

// 监听路由变化，更新语言
onMounted(() => {
  if (route.query.invite) {
    form.invitationCode = route.query.invite;
  }
});

const passwordVisible = reactive({
  loginPassword: false,
  fundPassword: false
});

function togglePassword(key) {
  passwordVisible[key] = !passwordVisible[key];
}

// 语言选择相关
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

// 表单提交
function onSubmit() {
  // 验证密码是否一致
  if (form.loginPassword !== form.fundPassword) {
    showAlert("两次输入的密码不一致", 2000);
    return;
  }

  // 验证必填字段
  if (
    !form.loginAccount ||
    !form.phone ||
    !form.loginPassword ||
    !form.fundPassword
  ) {
    showAlert("请填写完整信息", 2000);
    return;
  }

  const payload = {
    ...form,
    phone: form.areaCode + form.phone // 拼接完整手机号
  };

  register(payload).then(res => {
    if (res.code === 200) {
      showAlert("注册成功", 2000);
      router.push("/login");
    } else {
      showAlert(res.msg, 2000);
    }
  });
}
</script>

<style scoped>
.register-container {
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

/* 语言选择器 */
.language-selector {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #fff;
  z-index: 20;
}

.language-selector .label {
  margin-right: 6px;
  color: #fff;
}

.dropdown-wrapper {
  position: relative;
  background: rgba(255, 255, 255, 0.15);
  padding: 5px 10px;
  border-radius: 6px;
  cursor: pointer;
  color: #333;
  user-select: none;
  display: flex;
  align-items: center;
}

.flag-icon {
  width: 20px;
  height: 15px;
  margin-right: 5px;
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

.lang-dropdown li {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
}

.lang-dropdown li:hover,
.lang-dropdown li.active {
  background-color: #f5f5f5;
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

/* 注册表单 */
.register-form {
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

.phone-icon::before {
  content: "";
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url("../../assets/img/phone-icon.png") no-repeat center center /
    contain;
  opacity: 0.7;
}

.invite-icon::before {
  content: "";
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url("../../assets/img/invite-icon.png") no-repeat center center /
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

/* 手机号输入框 */
.phone-group {
  display: flex;
  align-items: center;
  background-color: #ffff;
  border-radius: 5px;
  overflow: hidden;
  position: relative;
}

.area-select {
  border: none;
  outline: none;
  background: transparent;
  padding: 0 5px;
  font-size: 14px;
  appearance: none;
  width: 70px;
  text-align: center;
  color: #333;
}

.phone-input {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  padding: 0 10px;
  font-size: 16px;
  color: #333;
  outline: none;
}

/* 用户协议 */
.agreement {
  display: flex;
  align-items: center;
  margin: 20px 0;
  font-size: 14px;
  color: #666;
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

.agreement a {
  color: #2196f3;
  text-decoration: none;
}

/* 注册按钮 */
.register-btn {
  margin-top: 20px;
  margin-bottom: 20px;
}

.register-btn button {
  width: 100%;
  height: 50px;
  background-color: #ffff;
  color: #f1c40f;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
}

/* 登录链接 */
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

/* 响应式调整 */
@media screen and (min-width: 768px) {
  .register-container {
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

  /* 语言选择器 */
  .language-selector {
    position: absolute;
    top: 20px;
    right: 20px;
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #fff;
    z-index: 20;
  }

  .language-selector .label {
    margin-right: 6px;
    color: #fff;
  }

  .dropdown-wrapper {
    position: relative;
    background: rgba(255, 255, 255, 0.15);
    padding: 5px 10px;
    border-radius: 6px;
    cursor: pointer;
    color: #333;
    user-select: none;
    display: flex;
    align-items: center;
  }

  .flag-icon {
    width: 20px;
    height: 15px;
    margin-right: 5px;
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

  .lang-dropdown li {
    padding: 8px 12px;
    cursor: pointer;
    transition: background-color 0.3s;
    display: flex;
    align-items: center;
  }

  .lang-dropdown li:hover,
  .lang-dropdown li.active {
    background-color: #f5f5f5;
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

  /* 注册表单 */
  .register-form {
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

  .phone-icon::before {
    content: "";
    display: inline-block;
    width: 20px;
    height: 20px;
    background: url("../../assets/img/phone-icon.png") no-repeat center center /
      contain;
    opacity: 0.7;
  }

  .invite-icon::before {
    content: "";
    display: inline-block;
    width: 20px;
    height: 20px;
    background: url("../../assets/img/invite-icon.png") no-repeat center center /
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

  /* 手机号输入框 */
  .phone-group {
    display: flex;
    align-items: center;
    background-color: #ffff;
    border-radius: 5px;
    overflow: hidden;
    position: relative;
  }

  .area-select {
    border: none;
    outline: none;
    background: transparent;
    padding: 0 5px;
    font-size: 14px;
    appearance: none;
    width: 70px;
    text-align: center;
    color: #333;
  }

  .phone-input {
    flex: 1;
    height: 100%;
    border: none;
    background: transparent;
    padding: 0 10px;
    font-size: 16px;
    color: #333;
    outline: none;
  }

  /* 用户协议 */
  .agreement {
    display: flex;
    align-items: center;
    margin: 20px 0;
    font-size: 14px;
    color: #666;
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

  .agreement a {
    color: #2196f3;
    text-decoration: none;
  }

  /* 注册按钮 */
  .register-btn {
    margin-top: 20px;
    margin-bottom: 20px;
  }

  .register-btn button {
    width: 100%;
    height: 50px;
    background-color: #ffff;
    color: #f1c40f;
    border: none;
    border-radius: 5px;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
  }

  /* 登录链接 */
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
