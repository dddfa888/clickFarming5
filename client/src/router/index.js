import { createWebHashHistory, createRouter } from 'vue-router'

// 路由表
const routes = [
  { path: '/', name: 'Home', component: () => import('../views/index/index.vue') },
  { path: '/login', name: 'login', component: () => import('../views/login/index.vue') },
  { path: '/register', name: 'register', component: () => import('../views/register/index.vue') },
  { path: '/warehouse', name: 'warehouse', component: () => import('../views/warehouse/index.vue') },
  { path: '/me', name: 'me', component: () => import('../views/me/index.vue') },
  { path: '/member', name: 'member', component: () => import('../views/me/member.vue') },
   { path: '/personal', name: 'personal', component: () => import('../views/me/PersonalInfo.vue') },
   {path: '/deposit', name: 'deposit', component: () => import('../views/me/deposit.vue') },
   {path: '/withdrawhistory', name: 'withdrawhistory', component: () => import('../views/me/withdrawHistory.vue') },
   {path: '/team', name: 'team', component: () => import('../views/me/team.vue') },
   {path: '/yubao', name: 'yubao', component: () => import('../views/me/yubao.vue') },
   {path: '/setup', name: 'setup', component: () => import('../views/me/setup.vue') },
   {path: '/baorecords', name: 'baorecords', component: () => import('../views/me/baorecords.vue') },
   {path: '/backcard', name: 'backcard', component: () => import('../views/me/backcard.vue') },
   {path: '/reflect', name: 'reflect', component: () => import('../views/me/reflect.vue') },
   {path: '/setpaypassword', name: 'setpaypassword', component: () => import('../views/me/SetPayPassword.vue') },
  {
    path: '/income',
    name: 'income',
    component: () => import('../views/index/income.vue')
  },
  {
    path: '/record',
    name: 'record',
    component: () => import('../views/record/index.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// 路由守卫：未登录禁止访问其他页面
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token') // 假设你是用 token 存储登录状态
  const whiteList = ['/login', '/register'] // 白名单路由

  if (whiteList.includes(to.path)) {
    // 登录页、注册页无需验证
    return next()
  }

  if (token) {
    // 有 token，放行
    return next()
  } else {
    // 无 token，重定向到登录页
    return next('/login')
  }
})

export default router
