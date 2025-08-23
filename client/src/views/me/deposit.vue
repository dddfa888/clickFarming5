<template>
  <div class="company-intro">
    <HeaderBar :title="t('充值记录')" />
    <div class="transaction-list">
      <div v-for="(transaction, index) in transactions" :key="index" class="transaction-item">
        <div class="transaction-info">
          <div class="transaction-time">{{ t("充值金额") }}:${{ transaction.amount }}</div>
          <div class="transaction-amount" :class="{ negative: transaction.amount < 0 }">
            {{ t("订单号") }}:
            {{ transaction.uuid }}
          </div>
          <div class="transaction-balance">
            {{ t("充值状态") }}:
            <span :style="{ color: transaction.status === 1 ? 'green' : 'red'}">
              {{
              transaction.status === 1
              ? t("经核准的")
              : transaction.status === 2
              ? t("稲核失败")
              : t("待准予")
              }}
            </span>
          </div>
        </div>
      </div>
      <div>
        <div v-if="transactions.length === 0" class="no-transaction">
          <div class="no-transaction-text">{{ t("还没有数据") }}</div>
        </div>
      </div>
    </div>

    <!-- 分页器 -->
    <div v-if="transactions.length!=0" class="pagination-wrapper">
      <div class="pagination">
        <span
          class="pagination-info"
        >{{ t('paginationSummary', { total: totalOrders, size: pageSize }) }}</span>
        <div class="page-buttons">
          <button class="pagination-button" @click="previousPage" :disabled="currentPage === 1">«</button>
          <span class="page-number">{{ currentPage }} / {{ totalPages }}</span>
          <button
            class="pagination-button"
            @click="nextPage"
            :disabled="currentPage === totalPages"
          >»</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import HeaderBar from "../../components/HeaderBar.vue";
import { getDepositRecord } from "../../api/index.js";
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const transactions = ref([]);

const formatAmount = amount => {
  return amount.toFixed(2).replace(".", ",") + " $";
};

getDepositRecord().then(res => {
  console.log(res.rows);
  transactions.value = res.rows;
  console.log(transactions.value, 21);
});

// 分页
const currentPage = ref(1);
const pageSize = 20;

// 不再用 filteredOrders，因为数据直接由接口返回
const totalPages = computed(() =>
  Math.ceil(transactions.value.length / pageSize)
);
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return transactions.value.slice(start, start + pageSize);
});
const totalOrders = computed(() => transactions.value.length);

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};
const previousPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};
</script>

<style scoped>
.company-intro {
  background-color: #1e201f;
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
  color: #fff;
  font-size: 14px;
}

.transaction-amount {
  grid-column: 2;
  grid-row: 1;
  color: #fff;
  text-align: right;
  font-size: 14px;
}

.transaction-amount.negative {
  color: #e74c3c;
}
.transaction-balance {
  grid-column: 1 / span 2;
  grid-row: 2;
  color: #fff;
  font-size: 14px;
}

.transaction-status {
  grid-column: 2;
  grid-row: 2;
  text-align: right;
  color: #fff;
  font-size: 14px;
}

.no-transaction {
  color: #fff;
  text-align: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 10px;
  margin-bottom: 50px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 480px;
}

.pagination-info {
  font-size: 12px;
  color: #888;
}

.page-buttons {
  display: flex;
  align-items: center;
}

.pagination-button {
  padding: 5px 10px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #ccc;
  margin: 0 5px;
  color: #000;
}

.pagination-button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
  color: #fff;
}

.page-number {
  font-size: 14px;
  color: #888;
}

@media screen and (min-width: 768px) {
  .company-intro {
    background-color: #1e201f;
    height: 100vh;
    width: 450px;
    margin: 0 auto;
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
    color: #fff;
    font-size: 14px;
  }

  .transaction-amount {
    grid-column: 2;
    grid-row: 1;
    color: #fff;
    text-align: right;
    font-size: 14px;
  }

  .transaction-amount.negative {
    color: #e74c3c;
  }
  .transaction-balance {
    grid-column: 1 / span 2;
    grid-row: 2;
    color: #fff;
    font-size: 14px;
  }

  .transaction-status {
    grid-column: 2;
    grid-row: 2;
    text-align: right;
    color: #fff;
    font-size: 14px;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    padding: 10px;
  }

  .pagination {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    max-width: 480px;
  }

  .pagination-info {
    font-size: 12px;
    color: #888;
  }

  .page-buttons {
    display: flex;
    align-items: center;
  }

  .pagination-button {
    padding: 5px 10px;
    font-size: 14px;
    cursor: pointer;
    border: 1px solid #ccc;
    margin: 0 5px;
    color: #000;
  }

  .pagination-button:disabled {
    cursor: not-allowed;
    opacity: 0.5;
    color: #fff;
  }

  .page-number {
    font-size: 14px;
    color: #888;
  }
}
</style>
