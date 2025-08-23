<!-- src/views/CompanyIntro.vue -->
<template>
  <div class="company-intro">
    <HeaderBar :title="t('公司资格')" />
    <div class="content-box">
      <p v-html="qualification"></p>
    </div>
  </div>
</template>

<script setup>
import HeaderBar from "../../components/HeaderBar.vue";
import { ref } from "vue";
import { getCompanyQualification } from "../../api/index";
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const qualification = ref();
getCompanyQualification().then(res => {
  console.log(res.data.content);
  qualification.value = res.data.content;
});
</script>

<style scoped>
.company-intro {
  background: #121212;
  height: 100vh;
  overflow-y: auto;
  /* padding-bottom: 70px; */
}

.content-box {
  background-color: #252726;
  -webkit-backdrop-filter: blur(6px) !important;
  backdrop-filter: blur(6px) !important;
  margin: 12px;
  padding: 12px;
  border-radius: 5px;
  color: white;
  font-size: 15px;
  margin-top: 50px;
}
.content-box > p {
  margin: 5px;
}

.notice-box {
  border-bottom: 1px solid #dad8da;
}

.notice-title {
  display: flex;
}
.notice-title p {
  padding-right: 12px;
}

@media screen and (min-width: 768px) {
  .company-intro {
    background: #121212;
    height: 100vh;
    overflow-y: auto;
    width: 450px;
    margin: 0 auto;
    /* padding-bottom: 70px; */
  }

  .content-box {
    background-color: #252726;
    -webkit-backdrop-filter: blur(6px) !important;
    backdrop-filter: blur(6px) !important;
    margin: 12px;
    padding: 12px;
    border-radius: 5px;
    color: white;
    font-size: 15px;
    margin-top: 50px;
  }
  .content-box > p {
    margin: 5px;
  }

  .notice-box {
    border-bottom: 1px solid #dad8da;
  }

  .notice-title {
    display: flex;
  }
  .notice-title p {
    padding-right: 12px;
  }
}
</style>
