<template>
  <div class="company-intro" ref="scrollContainer" @scroll="handleScroll">
    <HeaderBar :title="t('充值')" backcolor="#ece9ee" />
    <div class="transaction-list">
      <div v-for="(transaction, index) in transactions" :key="index" class="transaction-item">
        <div class="transaction-info">
          <div class="transaction-time">
            <span>{{ t("充值金额") }}:</span>
            <span>{{ transaction.amount }}</span>
          </div>
          <div class="transaction-amount" :class="{ negative: transaction.amount < 0 }">
            2025-01-01 00:00:00
            <span :style="{ color: transaction.status === 1 ? 'green' : 'red'}">
              {{
              transaction.status === 1
              ?"成功"
              : transaction.status === 2
              ? "失败"
              : "处理中"
              }}
            </span>
          </div>
          <div class="transaction-balance">{{ t("充值说明") }}:后台管理操作</div>
        </div>
      </div>

      <!-- 没有数据提示 -->
      <div v-if="transactions.length === 0" class="no-transaction">
        <div class="no-transaction-text">{{ t("还没有数据") }}</div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
import HeaderBar from "../../components/HeaderBar.vue";
import { getWithdrawRecord } from "../../api/index.js";
import { useInfoStore } from "../../store/useInfoStore";
const { t } = useI18n();

const transactions = ref([]);
const loading = ref(false);
const noMore = ref(false);
const currentPage = ref(1);
const pageSize = 20;
const pageSizeApi = ref(20);
const infoStore = useInfoStore();

const scrollContainer = ref(null);

// 格式化金额
const formatAmount = amount => {
  return amount.toFixed(2).replace(".", ",") + " $";
};

// 获取充值记录
const fetchTransactions = async () => {
  if (loading.value || noMore.value) return;
  loading.value = true;

  try {
    const res = await getWithdrawRecord({
      userId: infoStore.getUserinfo.uid,
      type: 1,
      pageSize: pageSizeApi.value,
      pageNum: currentPage.value
    });

    if (res.rows.length < pageSize) {
      noMore.value = true;
    }

    transactions.value = [...transactions.value, ...res.rows];
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 滚动加载下一页
const handleScroll = () => {
  const container = scrollContainer.value;
  if (
    container.scrollTop + container.clientHeight >=
    container.scrollHeight - 10
  ) {
    if (!loading.value && !noMore.value) {
      currentPage.value++;
      fetchTransactions();
    }
  }
};

onMounted(() => {
  fetchTransactions();
});
</script>

<style scoped>
.company-intro {
  background-color: #ece9ee;
  height: 100vh;
  overflow-y: auto;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding-top: 50px;
}

.transaction-item {
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 5px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #ccc;
}
.transaction-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.transaction-time {
  grid-column: 1;
  grid-row: 1;
  font-size: 14px;
}

.transaction-amount {
  grid-column: 2;
  grid-row: 1;
  text-align: right;
  font-size: 14px;
}

.transaction-amount.negative {
  color: #e74c3c;
}
.transaction-balance {
  grid-column: 1 / span 2;
  grid-row: 2;
  font-size: 14px;
}

.transaction-status {
  grid-column: 2;
  grid-row: 2;
  text-align: right;
  font-size: 14px;
}

.no-transaction {
  text-align: center;
}

.loading {
  text-align: center;
  padding: 10px;
  color: #888;
}

.no-more {
  text-align: center;
  padding: 10px;
  color: #888;
}

@media screen and (min-width: 768px) {
  .company-intro {
    background-color: #ece9ee;
    height: 100vh;
    overflow-y: auto;
    width: 450px;
    margin: 0 auto;
  }

  .transaction-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
    padding-top: 50px;
  }

  .transaction-item {
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 5px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid #ccc;
  }
  .transaction-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .transaction-time {
    grid-column: 1;
    grid-row: 1;
    font-size: 14px;
  }

  .transaction-amount {
    grid-column: 2;
    grid-row: 1;
    text-align: right;
    font-size: 14px;
  }

  .transaction-amount.negative {
    color: #e74c3c;
  }
  .transaction-balance {
    grid-column: 1 / span 2;
    grid-row: 2;
    font-size: 14px;
  }

  .transaction-status {
    grid-column: 2;
    grid-row: 2;
    text-align: right;
    font-size: 14px;
  }

  .no-transaction {
    text-align: center;
  }

  .loading {
    text-align: center;
    padding: 10px;
    color: #888;
  }

  .no-more {
    text-align: center;
    padding: 10px;
    color: #888;
  }
}
</style>
