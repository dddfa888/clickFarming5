<template>
  <div class="login-container">
    <!--分开的语言选择区域 -->
    <div class="language-selector">
      <div class="dropdown-wrapper" @click="toggleLangList">
        <!-- 当前语言图标 + 名称 -->
        <img
          v-if="selectedLanguage"
          class="flag-icon"
          :src="flagMap[langMap[selectedLanguage]]"
          alt
        />
        <span>{{ t(selectedLanguage) }}</span>

        <!-- 下拉菜单 -->
        <ul v-if="showLangList" class="lang-dropdown">
          <li
            v-for="(lang, index) in languageList"
            :key="index"
            @click.stop="selectLanguage(lang)"
            :class="{ active: lang === selectedLanguage }"
          >
            <img class="flag-icon" :src="flagMap[langMap[lang]]" alt />
            <span>{{ t(lang) }}</span>
          </li>
        </ul>
      </div>
    </div>

    <img
      width="120px"
      style="margin-top: 50px;border-radius: 10px;"
      src="../../assets/img/login.png"
      alt
    />

    <!-- 登录表单 -->
    <div class="login-form">
      <form>
        <!-- 用户名 -->
        <div class="form-group">
          <i class="icon user-icon"></i>
          <input type="text" v-model="form.loginAccount" :placeholder="t('请填写用户名')" />
        </div>

        <!-- 密码框（含明密文切换） -->
        <div class="form-group password-group">
          <i class="icon lock-icon"></i>
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model="form.loginPassword"
            :placeholder="t('请填写密码')"
          />
          <!-- 眼睛 SVG 图标 -->
          <span class="eye-icon" @click="togglePasswordVisibility">
            <svg
              v-if="showPassword"
              xmlns="http://www.w3.org/2000/svg"
              class="icon-eye"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M1 1l22 22" />
              <path
                d="M17.94 17.94A10.94 10.94 0 0112 20C7.03 20 2.73 16.11 1 12a18.84 18.84 0 014.21-5.69M9.88 9.88A3 3 0 0114.12 14.12"
              />
            </svg>

            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              class="icon-eye"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
          </span>
        </div>

        <!-- 登录按钮 -->
        <div class="login-btn">
          <button @click="onSubmit" type="submit">{{ t("登录") }}</button>
        </div>
        <div class="login-btn">
          <button @click="router.push('/register')">{{ t("注册") }}</button>
        </div>
      </form>
    </div>
    <img class="service-img" src="../../assets/img/service.png" alt />
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
  हिंदी: "hi"
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
  const langCode = langMap[lang] || "ko";
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
      showAlert(t(res.msg), 2000);
      localStorage.setItem("token", res.data.token);
      infoStore.setUserinfo(res.data.date);
      if (window._MEIQIA) {
        window._MEIQIA("client", {
          name: res.data.loginAccount, // 假设后端返回了用户名
          tel: res.data.phoneNumber // 假设后端返回了手机号
        });
      }
      router.push("/");
    } else {
      showAlert(t(res.msg), 2000);
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
  padding: 20px;
  box-sizing: border-box;
  background: url("../../assets/img/login-bg.png") no-repeat center center /
    cover;
}

/* 登录表单 */
.login-form {
  width: 100%;
  max-width: 500px;
  /* background: rgba(255, 255, 255, 0.08); */
  border-radius: 10px;
  padding: 10px;
  box-sizing: border-box;
  margin-top: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
  border-radius: 30px;
  width: 100%;
  border: 1px solid #181818;
  box-shadow: 0 6px 6px rgba(0, 0, 0, 0.1);
  position: relative;
  margin-bottom: 16px;
}

.form-group input {
  padding: 14px 16px;
  border-radius: 5px; /* 完全圆形输入框 */
  color: rgb(133, 133, 133);
  border: none;
  font-size: 14px;
  outline: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  background-color: #edfbff;
}

.login-btn {
  display: flex;
  justify-content: center;
}

.login-btn button {
  width: 100%;
  padding: 10px;
  margin-bottom: 16px;
  border: none;
  border-radius: 25px;
  background-color: #d6ba82;
  color: white;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.8);
}

.login-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
  max-width: 500px;
  align-items: center;
  color: #fff;
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
  color: #fff;
  user-select: none;
}
.lang-dropdown {
  position: absolute;
  top: 35px;
  right: 0;
  background: #0c0c0c;
  color: #fff;
  list-style: none;
  padding: 5px 0;
  margin: 0;
  border-radius: 4px;
  width: 100px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 999;
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

/* PC端适配 */
@media screen and (min-width: 768px) {
  .login-container {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 100vh;
    width: 450px;
    padding: 20px;
    margin: 0 auto;
    box-sizing: border-box;
    background: url("../../assets/img/login-bg.png") no-repeat center center /
      cover;
  }

  /* 登录表单 */
  .login-form {
    width: 100%;
    max-width: 500px;
    /* background: rgba(255, 255, 255, 0.08); */
    border-radius: 10px;
    padding: 10px;
    box-sizing: border-box;
    margin-top: 20px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 16px;
    border-radius: 30px;
    width: 100%;
    border: 1px solid #181818;
    box-shadow: 0 6px 6px rgba(0, 0, 0, 0.1);
    position: relative;
    margin-bottom: 16px;
  }

  .form-group input {
    padding: 14px 16px;
    border-radius: 5px; /* 完全圆形输入框 */
    color: rgb(133, 133, 133);
    border: none;
    font-size: 14px;
    outline: none;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    background-color: #edfbff;
  }

  .login-btn {
    display: flex;
    justify-content: center;
  }

  .login-btn button {
    width: 100%;
    padding: 10px;
    margin-bottom: 16px;
    border: none;
    border-radius: 25px;
    background-color: #d6ba82;
    color: white;
    font-size: 16px;
    cursor: pointer;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.8);
  }

  .login-footer {
    display: flex;
    justify-content: space-between;
    width: 100%;
    max-width: 500px;
    align-items: center;
    color: #fff;
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
    color: #fff;
    user-select: none;
  }
  .lang-dropdown {
    position: absolute;
    top: 35px;
    right: 0;
    background: #0c0c0c;
    color: #fff;
    list-style: none;
    padding: 5px 0;
    margin: 0;
    border-radius: 4px;
    width: 100px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    z-index: 999;
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
}
</style>
