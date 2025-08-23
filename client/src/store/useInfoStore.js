import { defineStore } from 'pinia';

export const useInfoStore = defineStore('userinfo', {
  state: () => ({
    userinfo: {},
  }),
  getters: {
    getUserinfo: (state) => {
      // 优先取 store 里的，如果为空再去 localStorage
      return Object.keys(state.userinfo).length
        ? state.userinfo
        : JSON.parse(localStorage.getItem('userinfo')) || {};
    }
  },
  actions: {
    setUserinfo(userinfo) {
      this.userinfo = userinfo;
      localStorage.setItem('userinfo', JSON.stringify(userinfo));
    },
    clearUserinfo() {
      this.userinfo = {};
      localStorage.removeItem('userinfo');
    }
  }
});
