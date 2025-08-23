<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-box">
      <!-- 关闭按钮 -->
      <div class="img-box">
        <img style="width: 47px;" src="../assets/img/下载.png" alt />
        <img style="height: 30px;" src="../assets/img/tankuang.png" alt />
        <img style="width: 47px;" src="../assets/img/下载2.png" alt />
      </div>
      <div class="logo-img">
        <img class="logo" src="../assets/img/d922de4da9b6094d.jpg" alt />
        <div style="color: black;font-size: 12px;">
          <p>
            <strong>We updated to the latest version on November 24, 2024, and it is now complete!</strong>
          </p>
          <p>
            <strong>Welcome to Warner Bros. Pictures!</strong>
          </p>
          <p>
            We are from the official promotional platform of
            <strong>Warner Bros</strong>
            . Films! We are committed to increasing movie awareness and boosting box office sales!
          </p>
        </div>
      </div>
    </div>
    <span class="close-btn" @click="close">
      <img src="../assets/img/bi.png" alt />
    </span>
  </div>
</template>

<script setup>
import { ref } from "vue";

const visible = ref(true);

const close = () => {
  visible.value = false;
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  z-index: 1000;
}

.modal-box {
  background: -webkit-gradient(
    linear,
    left top,
    left bottom,
    from(#121212 /*主题修改*/),
    color-stop(60%, #fff)
  );
  border-radius: 10px;
  max-width: 320px;
  width: 90%;
  height: 68vh;
  padding: 15px;
  text-align: center;
}

.img-box {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.logo-img {
  height: 58vh;
  background-color: #fff;
  border-radius: 5px;
  margin-top: 20px;
  padding: 10px;
}
.logo-img img {
  width: 100%;
  height: 40vh;
}

.close-btn {
  width: 40px;
  margin-top: 10px;
  font-weight: bold;
}

.close-btn img {
  width: 40px;
}

.logo {
  width: 80%;
}

.logo-text {
  font-size: 36px;
  font-weight: bold;
  margin: 5px 0;
}

.sub-logo {
  font-size: 14px;
  letter-spacing: 2px;
  margin-bottom: 10px;
}

.desc {
  font-size: 12px;
  margin: 4px 0;
  color: #000;
}

.vip-table {
  width: 100%;
  margin-top: 10px;
  border-collapse: collapse;
}

.vip-table th {
  font-size: 12px;
  padding: 6px;
  background: #000;
  color: #000;
}

.vip-table td {
  font-size: 12px;
  padding: 6px;
  border: 1px solid #ddd;
}

.confirm-btn {
  width: 100%;
  padding: 10px;
  background: #222;
  color: #000;
  font-size: 14px;
  border: none;
  border-radius: 8px;
  margin-top: 12px;
  cursor: pointer;
}
@media screen and (min-width: 768px) {
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    z-index: 1000;
  }

  .modal-box {
    background: -webkit-gradient(
      linear,
      left top,
      left bottom,
      from(#121212 /*主题修改*/),
      color-stop(60%, #fff)
    );
    border-radius: 10px;
    max-width: 380px;
    width: 90%;
    height: 68vh;
    padding: 15px;
    text-align: center;
  }

  .img-box {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-around;
  }

  .logo-img {
    height: 58vh;
    background-color: #fff;
    border-radius: 5px;
    margin-top: 20px;
    padding: 10px;
  }
  .logo-img img {
    width: 100%;
    height: 40vh;
  }

  .close-btn {
    width: 40px;
    margin-top: 10px;
    font-weight: bold;
  }

  .close-btn img {
    width: 40px;
  }

  .logo {
    width: 80%;
  }

  .logo-text {
    font-size: 36px;
    font-weight: bold;
    margin: 5px 0;
  }

  .sub-logo {
    font-size: 14px;
    letter-spacing: 2px;
    margin-bottom: 10px;
  }

  .desc {
    font-size: 12px;
    margin: 4px 0;
    color: #000;
  }

  .vip-table {
    width: 100%;
    margin-top: 10px;
    border-collapse: collapse;
  }

  .vip-table th {
    font-size: 12px;
    padding: 6px;
    background: #000;
    color: #000;
  }

  .vip-table td {
    font-size: 12px;
    padding: 6px;
    border: 1px solid #ddd;
  }

  .confirm-btn {
    width: 100%;
    padding: 10px;
    background: #222;
    color: #000;
    font-size: 14px;
    border: none;
    border-radius: 8px;
    margin-top: 12px;
    cursor: pointer;
  }
}
</style>
