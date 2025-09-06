<template>
  <div class="modal-backdrop" @click.self="close">
    <div class="modal-box">
      <!-- 顶部标题 -->
      <div class="modal-header">
        <p>匹配到{{ platform }}的订单</p>
      </div>

      <!-- 时间 & 订单号 -->
      <div class="order-info">
        <p>{{ t("订单编号") }}: {{ data.productId }}</p>
      </div>

      <!-- 商品信息 -->
      <div class="product">
        <img :src="data.productImageUrl" class="product-img" />
        <div class="product-desc">
          <p class="product-name">{{ data.productName }}</p>
          <p>{{ data.unitPrice }} × {{ data.number }}</p>
        </div>
      </div>

      <!-- 价格明细 -->
      <div class="price-info">
        <div class="row">
          <span>订单总数</span>
          <span>{{ data.totalAmount }}</span>
        </div>
        <div class="row">
          <span>返现</span>
          <span>{{ data.profit }}</span>
        </div>
        <div class="row">
          <span>合计金额</span>
          <span style="color: #ff0000">{{ data.totalAmount }}+{{ data.profit }}</span>
        </div>
        <div class="row">
          <span>订单日期</span>
          <span>{{ data.createTime }}</span>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="actions">
        <button class="btn btn-cancel" @click="close">取消</button>
        <button class="btn btn-pay" @click="onPay">提交订单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, toRefs, ref } from "vue";
import { getOrderById } from "../api/index.js";
import { useI18n } from "vue-i18n";
const props = defineProps({
  id: {
    type: [String, Number],
    required: true
  }
});

const { id } = toRefs(props);
const { t } = useI18n();
const platform = ref("");
const data = ref({});
getOrderById(id.value).then(res => {
  data.value = res.data.orderRecord;
  platform.value = res.data.platform;
});

const emit = defineEmits(["close", "pay"]);
const close = () => emit("close");
const onPay = () => emit("pay");
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-box {
  width: 90%;
  max-width: 380px;
  background-color: #eeeaea;
  border-radius: 12px;
  overflow: hidden;
  color: #000;
  font-size: 14px;
}

/* 顶部标题条 */
.modal-header {
  text-align: center;
  background: url("../assets/img/ddb4.b6d5e63.png") no-repeat;
  background-size: cover;
  padding: 12px;
  font-weight: bold;
  font-size: 16px;
  position: relative;
}
.modal-header img {
  width: 50%;
}

/* 订单信息 */
.order-info {
  padding: 10px 16px;
  font-size: 12px;
  color: #000;
}

/* 商品 */
.product {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  margin: 0 16px;
  border-radius: 6px;
  padding: 10px;
}

.product-img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  margin-right: 10px;
}

.product-desc .product-name {
  font-weight: bold;
  margin-bottom: 4px;
  color: #000;
}

/* 价格信息 */
.price-info {
  margin: 16px;
}

.price-info .row {
  display: flex;
  justify-content: space-between;
  margin: 6px 0;
  color: #000;
}

/* 底部按钮 */
.actions {
  display: flex;
  border-top: 1px solid #eee;
  margin-top: 10px;
}

.btn {
  flex: 1;
  padding: 12px;
  border: none;
  font-size: 15px;
  cursor: pointer;
}

.btn-cancel {
  background: #f0f0f0;
  color: #333;
}

.btn-pay {
  background: #2f3848;
  color: #f2d8be;
}

@media screen and (min-width: 768px) {
  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
  }

  .modal-box {
    width: 90%;
    max-width: 380px;
    background-color: #eeeaea;
    border-radius: 12px;
    overflow: hidden;
    color: #000;
    font-size: 14px;
  }

  /* 顶部标题条 */
  .modal-header {
    text-align: center;
    background: url("../assets/img/ddb4.b6d5e63.png") no-repeat;
    background-size: cover;
    padding: 12px;
    font-weight: bold;
    font-size: 16px;
    position: relative;
  }
  .modal-header img {
    width: 50%;
  }

  /* 订单信息 */
  .order-info {
    padding: 10px 16px;
    font-size: 12px;
    color: #000;
  }

  /* 商品 */
  .product {
    display: flex;
    align-items: center;
    background: #f9f9f9;
    margin: 0 16px;
    border-radius: 6px;
    padding: 10px;
  }

  .product-img {
    width: 60px;
    height: 60px;
    border-radius: 6px;
    object-fit: cover;
    margin-right: 10px;
  }

  .product-desc .product-name {
    font-weight: bold;
    margin-bottom: 4px;
    color: #000;
  }

  /* 价格信息 */
  .price-info {
    margin: 16px;
  }

  .price-info .row {
    display: flex;
    justify-content: space-between;
    margin: 6px 0;
    color: #000;
  }

  /* 底部按钮 */
  .actions {
    display: flex;
    border-top: 1px solid #eee;
    margin-top: 10px;
  }

  .btn {
    flex: 1;
    padding: 12px;
    border: none;
    font-size: 15px;
    cursor: pointer;
  }

  .btn-cancel {
    background: #f0f0f0;
    color: #333;
  }

  .btn-pay {
    background: #2f3848;
    color: #f2d8be;
  }
}
</style>