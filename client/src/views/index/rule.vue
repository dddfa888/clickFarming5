<!-- src/views/CompanyIntro.vue -->
<template>
  <div class="company-intro">
    <HeaderBar :title="t('规格描述')" />
    <div class="content-box">
      <p v-html="companyProfile"></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { useI18n } from "vue-i18n";
import { getBasicPrinciple } from "../../api/index";
import { useLangStore } from "../../store/useLangStore";

const { t } = useI18n();
const langStore = useLangStore();
const companyProfile = ref("");

getBasicPrinciple().then(res => {
  companyProfile.value = res.data.content;
  console.log(res.data.content);
});
</script>

<style scoped>
.company-intro {
  background-color: #121212;
  height: 100vh;
  overflow-y: auto;
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

@media screen and (min-width: 768px) {
  .company-intro {
    background-color: #121212;
    height: 100vh;
    overflow-y: auto;
    width: 450px;
    margin: 0 auto;
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
}
</style>
