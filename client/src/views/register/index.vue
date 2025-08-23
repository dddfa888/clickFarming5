<template>
  <div class="register-container">
    <!-- 多语言选择器 -->
    <img
      width="120px"
      style="margin-top: 50px;border-radius: 10px;"
      src="../../assets/img/login.png"
      alt
    />

    <!-- 表单 -->
    <div class="register-form">
      <form>
        <!-- 账号 -->
        <div class="form-group">
          <i class="iconfont icon-user input-icon"></i>
          <input v-model="form.loginAccount" type="text" :placeholder="t('请输入账号')" />
        </div>

        <!-- 手机号带区号 -->
        <div class="form-group phone-group">
          <i class="iconfont icon-phone input-icon"></i>
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
          <input v-model="form.phone" type="tel" :placeholder="t('请输入电话号码')" class="phone-input" />
        </div>

        <!-- 动态字段 -->
        <div class="form-group" v-for="(item, key) in fields" :key="key">
          <i :class="item.icon" class="input-icon"></i>
          <input
            v-model="form[key]"
            :type="passwordVisible[key] ? 'text' : item.type"
            :placeholder="t(item.placeholder)"
          />
          <!-- 仅 password 字段显示切换图标 -->
          <!-- 明文图标（eye-open） -->
          <svg
            v-if="item.type === 'password' && passwordVisible[key]"
            @click="togglePassword(key)"
            xmlns="http://www.w3.org/2000/svg"
            class="toggle-eye"
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

          <!-- 密文图标（eye-off） -->
          <svg
            v-else-if="item.type === 'password'"
            @click="togglePassword(key)"
            xmlns="http://www.w3.org/2000/svg"
            class="toggle-eye"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              d="M17.94 17.94A10.94 10.94 0 0 1 12 20C5 20 1 12 1 12A21.84 21.84 0 0 1 6.29 6.29M9.88 9.88A3 3 0 0 0 12 15a3 3 0 0 0 2.12-5.12"
            />
            <line x1="1" y1="1" x2="23" y2="23" />
          </svg>
        </div>

        <!-- 注册按钮 -->
        <div class="btn-wrapper">
          <button @click="onSubmit" type="submit" class="register-btn">{{ t("注册") }}</button>
        </div>
        <div class="btn-wrapper">
          <button @click="router.push('/login')" type="submit" class="register-btn">{{ t("登录") }}</button>
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
  areaCode: "+84",
  phone: "",
  loginPassword: "",
  fundPassword: "",
  invitationCode: ""
});

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

// 动态渲染字段（不包含 loginAccount 和 phone）
const fields = {
  loginPassword: {
    placeholder: "请输入密码",
    type: "password",
    icon: "iconfont icon-lock"
  },
  fundPassword: {
    placeholder: "请输入确认密码",
    type: "password",
    icon: "iconfont icon-lock"
  },
  invitationCode: {
    placeholder: "请输入邀请码",
    type: "text",
    icon: "iconfont icon-invite"
  }
};

// 表单提交
function onSubmit() {
  const payload = {
    ...form,
    phone: form.areaCode + form.phone // 拼接完整手机号
  };

  register(payload).then(res => {
    if (res.code === 200) {
      showAlert(t("操作成功"), 2000);
      router.push("/login");
    } else {
      showAlert(t(res.msg), 2000);
    }
  });
}
</script>

<style scoped>
.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: url("../../assets/img/login-bg.png") no-repeat center center;
  min-height: 100vh;
  width: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.toggle-eye {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  stroke: #888;
  cursor: pointer;
}

.phone-group {
  display: flex;
  align-items: center;
  background: #c5c5c5;
  border-radius: 999px;
  border: 1px solid #181818;
  box-shadow: 0 6px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
}

.toggle-eye {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #888;
  cursor: pointer;
}

.area-select {
  border: none;
  outline: none;
  background: transparent;
  padding: 12px 10px;
  font-size: 14px;
  appearance: none;
  width: 80px;
  text-align: center;
  color: #000;
  background-color: transparent;
}

.phone-input {
  flex: 1;
  padding: 12px;
  border: none;
  outline: none;
  font-size: 16px;
  background-color: transparent;
  color: #000;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #888;
}

.register-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  color: #fff;
  margin-bottom: 20px;
}

.language-selector {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 14px;
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
  background: #e5e5e5;
  color: #000;
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
  transition: background-color 0.3s;
}
.lang-dropdown li:hover,
.lang-dropdown li.active {
  background-color: #d3d3d3;
}

.register-form {
  width: 100%;
  max-width: 400px;
  margin: 20px auto;
}

.form-group {
  width: 100%;
  position: relative;
  margin-bottom: 16px;
  border: 1px solid #181818;
  box-shadow: 0 6px 6px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  background: #edfbff;
}

.form-group input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  background: #edfbff;
  box-sizing: border-box;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #888;
}

.register-btn {
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

.btn-wrapper {
  display: flex;
  justify-content: center;
}

@media screen and (min-width: 768px) {
  .register-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    background: url("../../assets/img/login-bg.png") no-repeat center center;
    min-height: 100vh;
    width: 450px;
    padding: 20px;
    margin: 0 auto;
    box-sizing: border-box;
  }

  .toggle-eye {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    width: 20px;
    height: 20px;
    stroke: #888;
    cursor: pointer;
  }

  .phone-group {
    display: flex;
    align-items: center;
    background: #c5c5c5;
    border-radius: 999px;
    border: 1px solid #181818;
    box-shadow: 0 6px 6px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    position: relative;
  }

  .toggle-eye {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: #888;
    cursor: pointer;
  }

  .area-select {
    border: none;
    outline: none;
    background: transparent;
    padding: 12px 10px;
    font-size: 14px;
    appearance: none;
    width: 80px;
    text-align: center;
    color: #000;
    background-color: transparent;
  }

  .phone-input {
    flex: 1;
    padding: 12px;
    border: none;
    outline: none;
    font-size: 16px;
    background-color: transparent;
    color: #000;
  }

  .input-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: #888;
  }

  .register-footer {
    display: flex;
    justify-content: space-between;
    width: 100%;
    align-items: center;
    color: #fff;
    margin-bottom: 20px;
  }

  .language-selector {
    position: absolute;
    top: 20px;
    right: 20px;
    font-size: 14px;
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
    background: #e5e5e5;
    color: #000;
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
    transition: background-color 0.3s;
  }
  .lang-dropdown li:hover,
  .lang-dropdown li.active {
    background-color: #d3d3d3;
  }

  .register-form {
    width: 100%;
    max-width: 400px;
    margin: 20px auto;
  }

  .form-group {
    width: 100%;
    position: relative;
    margin-bottom: 16px;
    border: 1px solid #181818;
    box-shadow: 0 6px 6px rgba(0, 0, 0, 0.1);
    border-radius: 4px;
    background: #edfbff;
  }

  .form-group input {
    width: 100%;
    padding: 12px 12px 12px 40px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    background: #edfbff;
    box-sizing: border-box;
  }

  .input-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: #888;
  }

  .register-btn {
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

  .btn-wrapper {
    display: flex;
    justify-content: center;
  }
}
</style>
