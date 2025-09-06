// src/i18n.js
import { createI18n } from 'vue-i18n'
import zh from './locales/zh';
import en from './locales/en';
import vi from './locales/vi';
import ko from './locales/ko';
import ja from "./locales/ja"
import pt from "./locales/pt"
import es from "./locales/es"
import hi from "./locales/hi"
import tr from "./locales/tr"
import th from "./locales/th"
import zhTW from "./locales/zh-TW"

const i18n = createI18n({
    legacy: false,
    globalInjection: true,
    locale: localStorage.getItem('locale') || 'zh', 
    fallbackLocale: 'en', 
     missing(locale, key) {
    console.warn(locale, key);
     },
    messages: {
        ko,
        vi,
        zh,
        en,
        ja,
        pt,
        es,
        hi,
        tr,
        th,
        zhTW
    },
});

export default i18n;
