<template>
  <div class="home">
    <div class="home-header">
      <div></div>
      <h3>{{ $t("主页") }}</h3>
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
    </div>
    <!--轮播图-->
    <div class="carousel">
      <div class="carousel-container" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
        <div class="carousel-slide" v-for="(slide, index) in slides" :key="index">
          <img :src="slide" alt />
        </div>
      </div>
    </div>

    <!-- nav导航-->
    <div class="user-nav">
      <ul v-for="item in navList" :key="item" @click="handleNavClick(item.path)">
        <li>
          <img :src="item.icon" alt />
        </li>
        <li>{{ $t(item.label) }}</li>
      </ul>
    </div>

    <!--消息提示-->
    <div class="marquee-container">
      <!-- 左侧固定图标 -->
      <div class="marquee-icon">
        <img style="width: 23px;" src="../../assets/img/index-gg.png" alt />
      </div>

      <!-- 滚动内容 -->
      <div class="marquee-wrapper">
        <div class="marquee-content" :style="{ animationDuration: speed + 's' }">
          <span
            v-for="(msg, index) in messages"
            :key="index"
          >{{ msg }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        </div>
      </div>
    </div>

    <h4 style="font-weight:bold;">{{ $t("COMPANY INTRODUCTION") }}</h4>
    <nav>
      <div
        class="nav-item"
        v-for="item in navList2"
        :key="item"
        @click="handleNavItemClick(item.path)"
      >
        <img :src="item.icon" alt />
        <span>{{ $t(item.label) }}</span>
      </div>
    </nav>

    <!-- 奖励获得者名单 -->
    <div class="reward">
      <div class="reward-list">
        <div v-for="(reward, index) in rewards" :key="index" class="reward-item">
          <span>
            <img style="width: 30px;" :src="reward.imgurl" alt />
          </span>
          <span class="reward-date">
            <p>{{$t("恭喜")}}{{reward.username }}</p>
            <p style="color: #A4A5AB;">{{ reward.data }}</p>
          </span>
          <span style="color: #DEBB7A;" class="reward-message">${{ reward.amount }}</span>
        </div>
      </div>
    </div>
    <PromotionModal />

    <BaseModal
      v-model="showModal"
      :title="t('确认升级')"
      @confirm="handleConfirm"
      @cancel="handleCancel"
    >
      <template #body>
        <p>{{ t("您想升级到这个级别吗?") }}</p>
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import {
  onMounted,
  ref,
  computed,
  reactive,
  watch,
  onBeforeUnmount
} from "vue";
import { useRouter } from "vue-router";
import PromotionModal from "../../components/PromotionModal.vue";
import BaseModal from "../../components/BaseModal.vue";
import company from "../../assets/img/company.png";
import rule from "../../assets/img/rule.png";
import cooperation from "../../assets/img/cooperation.png";
import notice from "../../assets/img/notice.png";
import nav1 from "../../assets/img/index-cz.png";
import nav2 from "../../assets/img/index-qk.png";
import nav3 from "../../assets/img/index-yq.png";
import {
  getMemberRecord,
  getUserNotifyNum,
  updateGrade,
  updateAvatar,
  updateUserSimpleFront
} from "../../api/index.js";
import { useI18n } from "vue-i18n";
import { showAlert } from "../../utils/notify.js";
import { useLangStore } from "../../store/useLangStore.js";
import banner1 from "../../assets/img/banner.jpg";
import banner2 from "../../assets/img/banner1.jpg";
import banner3 from "../../assets/img/banner2.jpg";
import banner4 from "../../assets/img/banner3.jpg";
import avatar1 from "../../assets/avatars/1.png";
import avatar2 from "../../assets/avatars/12.png";
import avatar3 from "../../assets/avatars/1.png";
import avatar4 from "../../assets/avatars/6.png";
import avatar5 from "../../assets/avatars/9.png";

const promoRef = ref();
const router = useRouter();
const Recordlist = ref([]);
const level = ref(null);
const { t } = useI18n();
const notifyNum = ref(0);
const showModal = ref(false);
const uid = ref(null);
const avatarUrl = ref(""); // 初始头像
const fileInput = ref(null);
const uploadFile = ref(null);
const showLangList = ref(false);
const langStore = useLangStore();
const { locale } = useI18n();

const navList = [
  { label: "充值", icon: nav1, path: "/recharge" },
  { label: "提款", icon: nav2, path: "/withdraw" },
  { label: "邀请", icon: nav3, path: "/invite" }
];

const navList2 = [
  { label: "公司介绍", icon: notice, path: "/company" },
  { label: "规格描述", icon: rule, path: "/rule" },
  { label: "代理合作", icon: cooperation, path: "/cooperation" },
  { label: "公司资格", icon: company, path: "/qualification" }
];

const handleNavItemClick = path => {
  router.push(path);
};

const messages = ref([
  "전 세계의 신사 숙녀 여러분! HBOmax 필름스에 오신 것을 환영합니다. ",
  "Ladies and gentlemen of the world! Welcome to HBOmax Movies.",
  "Mesdames et Messieurs du monde ! Bienvenue chez HBOmax Movies..",
  "Meine Damen und Herren dieser Welt! Willkommen bei HBOmax  Movies.",
  "Signore e signori di tutto il mondo! Benvenuti a HBOmax Movies."
]);
const speed = 50;

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
const slides = reactive([banner1, banner2, banner3, banner4]);
const languageList = Object.keys(langMap);
const reverseLangMap = Object.fromEntries(
  Object.entries(langMap).map(([k, v]) => [v, k])
);

const flagMap = {
  en: new URL("../../assets/img/en-ww.png", import.meta.url).href,
  zhTW: new URL("../../assets/img/zh-tw.png", import.meta.url).href,
  ko: new URL("../../assets/img/ko-kr.png", import.meta.url).href,
  ja: new URL("../../assets/img/jr-rbg.png", import.meta.url).href,
  th: new URL("../../assets/img/th-th.png", import.meta.url).href,
  es: new URL("../../assets/img/es-mx.png", import.meta.url).href,
  vi: new URL("../../assets/img/iv-vn.png", import.meta.url).href,
  tr: new URL("../../assets/img/tr-tr.png", import.meta.url).href,
  hi: new URL("../../assets/img/the.png", import.meta.url).href,
  pt: new URL("../../assets/img/pt-br.png", import.meta.url).href
};

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

const currentSlide = ref(0);
const autoplayInterval = ref(null);

const startAutoplay = () => {
  stopAutoplay();
  autoplayInterval.value = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.length;
  }, 3000); // 每3秒切换
};

const stopAutoplay = () => {
  if (autoplayInterval.value) {
    clearInterval(autoplayInterval.value);
    autoplayInterval.value = null;
  }
};

onMounted(() => {
  startAutoplay();
});
onBeforeUnmount(() => {
  stopAutoplay();
});

getMemberRecord().then(res => {
  if (res.code === 200) {
    Recordlist.value = res.data.userGrade || "";
    level.value = res.data.level;
  }
});
getUserNotifyNum().then(res => {
  notifyNum.value = res.data;
});

const handleConfirm = () => {
  let gradeId = uid.value;
  updateGrade(uid.value).then(res => {
    console.log(res);
    if (res.code === 200) {
      showAlert(t(res.msg), 2000);
    } else {
      showAlert(t(res.msg), 2000);
    }
  });
};

const handleCancel = () => {
  console.log("取消了");
};

// 生成随机用户名
const generateUsername = () => {
  const prefix = [
    "rhu",
    "pea",
    "pon",
    "dur",
    "cra",
    "wil",
    "geo",
    "mar",
    "jan",
    "tom"
  ];
  const suffix = ["b", "r", "e", "n", "y", "k", "m", "s", "d", "f"];
  const randomPrefix = prefix[Math.floor(Math.random() * prefix.length)];
  const randomSuffix = suffix[Math.floor(Math.random() * suffix.length)];

  return `${randomPrefix}****${randomSuffix}`;
};

// 生成随机金额 (100-1999.99之间的随机金额)
const generateAmount = () => {
  const whole = 100 + Math.floor(Math.random() * 1900); // 100-1999
  const decimal = Math.floor(Math.random() * 100); // 0-99

  return `${whole.toLocaleString("de-DE")}.${decimal
    .toString()
    .padStart(2, "0")}`;
};

// 格式化金额显示
const formatAmount = amount => {
  return amount.replace(",", "."); // 转换为点分隔的格式
};

const rewards = ref([
  {
    data: new Date().toISOString().split("T")[0],
    username: generateUsername(),
    amount: generateAmount(),
    imgurl: avatar1
  },
  {
    data: new Date().toISOString().split("T")[0],
    username: generateUsername(),
    amount: generateAmount(),
    imgurl: avatar2
  },
  {
    data: new Date().toISOString().split("T")[0],
    username: generateUsername(),
    amount: generateAmount(),
    imgurl: avatar3
  },
  {
    data: new Date().toISOString().split("T")[0],
    username: generateUsername(),
    amount: generateAmount(),
    imgurl: avatar4
  },
  {
    data: new Date().toISOString().split("T")[0],
    username: generateUsername(),
    amount: generateAmount(),
    imgurl: avatar5
  }
]);

// 定时更新数据
onMounted(() => {
  promoRef.value?.show();
});

const handleButtonClick = icon => {
  if (icon === notice) {
    router.push({ path: "/company" });
  } else if (icon === rule) {
    router.push({ path: "/rule" });
  } else if (icon === cooperation) {
    router.push({ path: "/cooperation" });
  } else {
    router.push({ path: "/notice" });
  }
};

const handleNavClick = path => {
  console.log(path);
  router.push(path);
};

const onDeposit = () => {
  //console.log("执行提款操作");
  window.open(
    "https://chat.ichatlink.net/widget/standalone.html?eid=6df096f4e9b05ad245f542d63ed1c8d7&language=en",
    "_blank"
  );
};

function withTimeout(promise, timeout = 5000) {
  return Promise.race([
    promise,
    new Promise((_, reject) =>
      setTimeout(() => reject(new Error("请求超时")), timeout)
    )
  ]);
}
</script>

<style scoped>
.home {
  padding: 10px;
  min-height: 100vh;
  color: white;
  background-color: #121212;
  padding-bottom: 120px;
}

.home-header {
  width: 100%;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

nav {
  width: 100%;
  height: 50px;
  display: flex;
  align-items: center;
  color: #575757;
  margin-bottom: 30px;
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background-color: #1d1d1f;
  cursor: pointer;
  padding: 5px;
  border-radius: 8px;
  margin: 5px;
}

.nav-item img {
  width: 25px;
  height: 25px;
}

.nav-item span {
  font-size: 10px;
  font-weight: bold;
  margin-top: 5px;
  text-align: center;
}

.home-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.home-content-left {
  width: 50%;
}

.home-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.home-header-right img {
  width: 30px;
  height: 30px;
}

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

.home-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.home-header-left-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #ccc;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}

.user-info-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  line-height: 1.4;
}

.username {
  font-size: 18px;
  font-weight: bold;
}

.user-info-balance img {
  width: 25px;
  height: 25px;
  cursor: pointer;
}
.notifyNum {
  position: relative;
  top: -20px;
  left: -12px;
  display: inline-block;
  background-color: rgb(244, 67, 54);
  height: 20px;
  text-align: center;
  border-radius: 10px;
  line-height: 21px;
  padding: 0px 5px;
}

.balance-price {
  display: flex;
  max-width: 100%;
  margin: 10px auto;
  align-items: center;
  justify-content: space-between;
}

.balance-display {
  width: 110px;
  height: 90%;
  text-align: center;
  border: 1px solid #fff;
  border-radius: 20px;
}

.reward {
  background-color: #1d1d1f;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 20px;
  font-size: 10px;
  max-width: 600px;
  margin: 0 auto;
}

.reward-list {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.reward-item {
  width: 100%;
  background-color: transparent;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  transition: all 0.3s ease;
  animation: fadeIn 0.5s ease-out;
  font-size: 12px;
  color: #fff;
  padding: 2px;
}

.reward-date {
  color: #fff;
}

.reward-message {
  color: #fff;
  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 超出隐藏 */
  text-overflow: ellipsis; /* 超出显示省略号 */
  max-width: 220px; /* 根据布局调整宽度 */
}

.carousel-container {
  height: 150px;
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  min-width: 100%;
  height: 100%;
  position: relative;
}

.carousel-slide img {
  width: 100%;
  height: 100%;
  display: block;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-nav {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-top: 20px;
}
.user-nav ul {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  align-items: center;
}
.user-nav ul li img {
  width: 40px;
}

.marquee-container {
  width: 100%;
  display: flex;
  align-items: center;
  background: #111;
  color: #fff;
  padding: 6px 10px;
  box-sizing: border-box;
  overflow: hidden;
}

.marquee-icon {
  margin-right: 10px;
  font-size: 18px;
}

.marquee-wrapper {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  position: relative;
  font-size: 12px;
  background-color: #1d1d1f;
}

.marquee-content {
  display: inline-block;
  padding-left: 100%;
  animation: marquee linear infinite;
  padding: 5px;
}

@keyframes marquee {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-100%);
  }
}

.balance-label {
  font-size: 14px;
}

.balance-amount {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
  padding: 10px 0;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.withdraw {
  background-color: #4caf50;
  color: white;
}

.deposit {
  background-color: #2196f3;
  color: white;
}

.action-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.action-btn:active {
  transform: translateY(0);
}

.videos {
  width: 100%;
  border-radius: 8px;
  margin: 15px 0;
  z-index: 1;
  position: relative;
}

.info-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin: 20px 0;
  padding: 10px;
  font-size: 14px;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 10px;
  gap: 10px;
}
.btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s;
  padding: 8px;
  background-color: #1d1d1f;
  border-radius: 10px;
}

.btn:hover {
  transform: translateY(-3px);
}

.icon-img {
  width: 22px;
  height: 22px;
  margin-bottom: 5px;
}

.member-level {
  font-size: 15px;
  background-color: transparent;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 10px;
  border: 1px solid #7a797d;
  margin-bottom: 15px;
  padding: 15px;
}

.title {
  font-weight: bold;
  margin-bottom: 10px;
}

.level-info {
  display: flex;
  padding: 10px;
  justify-content: space-between;
}

.col {
  position: relative;
  text-align: center;
  flex: 1;
}

.card {
  position: relative;
  border-radius: 8px;
  /* 其他样式 */
}

.badge-box {
  position: absolute;
  top: -6.86667vw;
  right: -4.86667vw;
  display: flex;
  align-items: center;
  gap: 1.6vw;
  z-index: 1;
}

.badge {
  background-color: #b13330;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
}

.lock-icon {
  background-color: #ffe747;
  color: #000;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.carousel {
  position: relative;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  overflow: hidden;
}

@media screen and (min-width: 768px) {
  .home {
    padding: 10px;
    width: 450px;
    height: 100vh;
    color: white;
    background-color: #121212;
    padding-bottom: 120px;
    margin: 0 auto;
  }

  .home-header {
    width: 100%;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  nav {
    width: 100%;
    height: 50px;
    display: flex;
    align-items: center;
    color: #575757;
    margin-bottom: 30px;
  }

  .nav-item {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 100%;
    height: 100%;
    background-color: #1d1d1f;
    cursor: pointer;
    padding: 5px;
    border-radius: 8px;
    margin: 5px;
  }

  .nav-item img {
    width: 25px;
    height: 25px;
  }

  .nav-item span {
    font-size: 12px;
    font-weight: bold;
    margin-top: 5px;
    text-align: center;
  }

  .home-content {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .home-content-left {
    width: 50%;
  }

  .home-header-right {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .home-header-right img {
    width: 30px;
    height: 30px;
  }

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
    right: 38vw;
    top: 0;
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

  .home-header-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .home-header-left-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-color: #ccc;
    display: flex;
    align-items: center;
  }

  .user-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px;
  }

  .user-info-avatar {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
  }

  .user-details {
    line-height: 1.4;
  }

  .username {
    font-size: 18px;
    font-weight: bold;
  }

  .user-info-balance img {
    width: 25px;
    height: 25px;
    cursor: pointer;
  }
  .notifyNum {
    position: relative;
    top: -20px;
    left: -12px;
    display: inline-block;
    background-color: rgb(244, 67, 54);
    height: 20px;
    text-align: center;
    border-radius: 10px;
    line-height: 21px;
    padding: 0px 5px;
  }

  .balance-price {
    display: flex;
    max-width: 100%;
    margin: 10px auto;
    align-items: center;
    justify-content: space-between;
  }

  .balance-display {
    width: 110px;
    height: 90%;
    text-align: center;
    border: 1px solid #fff;
    border-radius: 20px;
  }

  .reward {
    background-color: #1d1d1f;
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    border-radius: 20px;
    font-size: 10px;
    max-width: 600px;
    margin: 0 auto;
  }

  .reward-list {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .reward-item {
    width: 100%;
    background-color: transparent;
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: space-around;
    transition: all 0.3s ease;
    animation: fadeIn 0.5s ease-out;
    font-size: 12px;
    color: #fff;
    padding: 2px;
  }

  .reward-date {
    color: #fff;
  }

  .reward-message {
    color: #fff;
    white-space: nowrap; /* 不换行 */
    overflow: hidden; /* 超出隐藏 */
    text-overflow: ellipsis; /* 超出显示省略号 */
    max-width: 220px; /* 根据布局调整宽度 */
  }

  .carousel-container {
    height: 150px;
    display: flex;
    transition: transform 0.5s ease-in-out;
  }

  .carousel-slide {
    min-width: 100%;
    height: 100%;
    position: relative;
  }

  .carousel-slide img {
    width: 100%;
    height: 100%;
    display: block;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }

    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .user-nav {
    display: flex;
    align-items: center;
    justify-content: space-around;
    margin-top: 20px;
  }
  .user-nav ul {
    display: flex;
    flex-direction: column;
    font-size: 12px;
    align-items: center;
  }
  .user-nav ul li img {
    width: 40px;
  }

  .marquee-container {
    width: 100%;
    display: flex;
    align-items: center;
    background: #111;
    color: #fff;
    padding: 6px 10px;
    box-sizing: border-box;
    overflow: hidden;
  }

  .marquee-icon {
    margin-right: 10px;
    font-size: 18px;
  }

  .marquee-wrapper {
    flex: 1;
    overflow: hidden;
    white-space: nowrap;
    position: relative;
    font-size: 12px;
    background-color: #1d1d1f;
  }

  .marquee-content {
    display: inline-block;
    padding-left: 100%;
    animation: marquee linear infinite;
    padding: 5px;
  }

  @keyframes marquee {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-100%);
    }
  }

  .balance-label {
    font-size: 14px;
  }

  .balance-amount {
    font-size: 14px;
    font-weight: bold;
    color: #fff;
  }

  .action-buttons {
    display: flex;
    gap: 10px;
  }

  .action-btn {
    flex: 1;
    padding: 10px 0;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.2s;
  }

  .withdraw {
    background-color: #4caf50;
    color: white;
  }

  .deposit {
    background-color: #2196f3;
    color: white;
  }

  .action-btn:hover {
    opacity: 0.9;
    transform: translateY(-1px);
  }

  .action-btn:active {
    transform: translateY(0);
  }

  .videos {
    width: 100%;
    border-radius: 8px;
    margin: 15px 0;
    z-index: 1;
    position: relative;
  }

  .info-buttons {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    margin: 20px 0;
    padding: 10px;
    font-size: 14px;
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    border-radius: 10px;
    gap: 10px;
  }
  .btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
    transition: transform 0.2s;
    padding: 8px;
    background-color: #1d1d1f;
    border-radius: 10px;
  }

  .btn:hover {
    transform: translateY(-3px);
  }

  .icon-img {
    width: 22px;
    height: 22px;
    margin-bottom: 5px;
  }

  .member-level {
    font-size: 15px;
    background-color: transparent;
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
    border-radius: 10px;
    border: 1px solid #7a797d;
    margin-bottom: 15px;
    padding: 15px;
  }

  .title {
    font-weight: bold;
    margin-bottom: 10px;
  }

  .level-info {
    display: flex;
    padding: 10px;
    justify-content: space-between;
  }

  .col {
    position: relative;
    text-align: center;
    flex: 1;
  }

  .card {
    position: relative;
    border-radius: 8px;
    /* 其他样式 */
  }

  .badge-box {
    position: absolute;
    top: -6.86667vw;
    right: -4.86667vw;
    display: flex;
    align-items: center;
    gap: 1.6vw;
    z-index: 1;
  }

  .badge {
    background-color: #b13330;
    color: #fff;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 4px;
    white-space: nowrap;
  }

  .lock-icon {
    background-color: #ffe747;
    color: #000;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 4px;
    white-space: nowrap;
  }

  .content {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .carousel {
    position: relative;
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
    overflow: hidden;
  }
}
</style>
