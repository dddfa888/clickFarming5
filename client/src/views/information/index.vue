<template>
  <div class="app-container">
    <!-- 顶部导航栏 -->
    <header class="top-bar">
      <button class="menu-btn">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <line x1="3" y1="12" x2="21" y2="12" />
          <line x1="3" y1="6" x2="21" y2="6" />
          <line x1="3" y1="18" x2="21" y2="18" />
        </svg>
      </button>
      <h1 class="title">聊天</h1>
      <button class="user-btn">
        <!--<svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="8.5" cy="7" r="4" />
          <line x1="20" y1="8" x2="20" y2="14" />
          <line x1="23" y1="11" x2="17" y2="11" />
        </svg>-->
      </button>
    </header>

    <!-- 搜索框 
    <div class="search-container">
      <input type="text" placeholder="搜索" class="search-input" />
    </div>-->

    <!-- 聊天列表 -->
    <div class="chat-list">
      <!--<div class="chat-item new-friend">
        <svg
          class="friend-icon"
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="8.5" cy="7" r="4" />
          <line x1="20" y1="8" x2="20" y2="14" />
          <line x1="23" y1="11" x2="17" y2="11" />
        </svg>
        <span>新朋友</span>
      </div>-->
      <div class="chat-item customer-service">
        <img src="../../assets/image/images.png" alt="客服头像" class="cs-avatar" />
        <div class="cs-info">
          <div class="cs-name">
            客服8801
            <span class="official-tag">官方</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 这里可以编写 Vue3 的组合式 API 逻辑，本布局主要是静态结构，暂无需复杂逻辑
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.menu-btn,
.user-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #333;
}

.title {
  font-size: 16px;
  margin: 0;
}

.search-container {
  padding: 10px 15px;
  background-color: #f5f5f5;
}

.search-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f5f5;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
}

.new-friend {
  justify-content: flex-start;
}

.friend-icon {
  margin-right: 10px;
  color: #ff672b;
}

.cs-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.cs-info {
  flex: 1;
}

.cs-name {
  font-weight: bold;
  font-size: 14px;
}

.official-tag {
  display: inline-block;
  background-color: #00b42a;
  color: #fff;
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 3px;
  margin-left: 5px;
  font-size: 10px;
  font-weight: 100;
}

.cs-message {
  font-size: 12px;
  color: #666;
  margin: 2px 0 0 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cs-time {
  font-size: 12px;
  color: #999;
}

.bottom-nav {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 10px 0;
  background-color: #f5f5f5;
  border-top: 1px solid #eee;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #666;
}

.nav-item.active {
  color: #ff672b;
}

.nav-item svg {
  margin-bottom: 3px;
}

.nav-item span {
  font-size: 12px;
}

@media screen and (min-width: 768px) {
  .app-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    width: 450px;
    margin: 0 auto;
    background-color: #f5f5f5;
  }

  .top-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 15px;
    background-color: #f5f5f5;
    border-bottom: 1px solid #eee;
  }

  .menu-btn,
  .user-btn {
    background: none;
    border: none;
    cursor: pointer;
    color: #333;
  }

  .title {
    font-size: 16px;
    margin: 0;
  }

  .search-container {
    padding: 10px 15px;
    background-color: #fff;
  }

  .search-input {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
  }

  .chat-list {
    flex: 1;
    overflow-y: auto;
    background-color: #f5f5f5;
  }

  .chat-item {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    border-bottom: 1px solid #eee;
  }

  .new-friend {
    justify-content: flex-start;
  }

  .friend-icon {
    margin-right: 10px;
    color: #ff672b;
  }

  .cs-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
  }

  .cs-info {
    flex: 1;
  }

  .cs-name {
    font-weight: bold;
    font-size: 14px;
  }

  .official-tag {
    display: inline-block;
    background-color: #00b42a;
    color: #fff;
    font-size: 10px;
    padding: 2px 4px;
    border-radius: 3px;
    margin-left: 5px;
    font-size: 10px;
    font-weight: 100;
  }

  .cs-message {
    font-size: 12px;
    color: #666;
    margin: 2px 0 0 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .cs-time {
    font-size: 12px;
    color: #999;
  }

  .bottom-nav {
    display: flex;
    justify-content: space-around;
    align-items: center;
    padding: 10px 0;
    background-color: #fff;
    border-top: 1px solid #eee;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
    color: #666;
  }

  .nav-item.active {
    color: #ff672b;
  }

  .nav-item svg {
    margin-bottom: 3px;
  }

  .nav-item span {
    font-size: 12px;
  }
}
</style>