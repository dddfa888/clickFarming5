import request from "../utils/index"

// 登录
export function login(data) {
    return request({
        url: '/api/login',
        method: 'post',
        data
    })
}

// 注册
export function register(data) {
    return request({
        url: '/api/register',
        method: 'post',
        data
    })
}

// 小组报告
export function getGroupReport() {
    return request({
        url: '/api/user/getUpToFourLevelInviters',
        method: 'get',
    })
}

// 获取个人的提现记录
export function getWithdrawRecord(params) {
    return request({
        url: `/api/withdraw/userList`, // data
        method: 'get',
        params
    })
}

// 获取个人账变记录
export function getDepositRecord() {
    return request({
        url: '/api/records/userList',
        method: 'get'
    })
}


// 获取用户个人信息
export function getUserInfo() {
    return request({
        url: '/api/user/userInfo',
        method: 'get',
    })
}

// 修改用户个人信息
export function updateUserFront(data) {
    return request({
        url: '/api/user/updateUserFront',
        method: 'post',
        data
    })
}


// 修改用户个人信息，不含校验密码
export function updateUserSimpleFront(data) {
    return request({
        url: '/api/user/updateUserSimpleFront',
        method: 'post',
        data
    })
}

// 创建订单
export function createOrder(data) {
    return request({
        url: '/api/order/insertOrderByUser',
        method: 'post',
        data
    })
}

// 订单列表
export function getOrderList(params) {
    return request({
        url: '/api/order/listByUser',
        method: 'get',
        params
    })
}

// 获取会员记录（包含当前的用户的等级）
export function getMemberRecord() {
    return request({
        url: '/api/grade/userList',
        method: 'get',
    })
}

// 订单历史
export function getOrderHistory() {
    return request({
        url: '/api/order/listByUser',
        method: 'get',
    })
}

// 奖励历史记录
export function getRewardHistory() {
    return request({
        url: '/api/rewardRecord/selectSimpleByUserId',
        method: 'get',
    })
}

// 用id查询订单
export function getOrderById(id) {
    return request({
        url: `/api/order/${id}`,
        method: 'get',
    })
}

// 用户等级、余额、折扣
export function getUserGradeAndBalanceAndDiscount() {
    return request({
        url: '/api/records/getInformation',
        method: 'get',
    })
}

// 总资产
export function getTotalAssets() {
    return request({
        url: '/api/records/totalAssets',
        method: 'get',
    })
}

// 发送分发（支付订单）
export function sendDistribution(id) {
    return request({
        url: `/api/order/payOrder/${id}`,
        method: 'put',
    })
}

// 获取用户通知 
export function getUserNotification() {
    return request({
        url: '/api/notify/userList',
        method: 'get',
    })
}


// 获取用户通知数量
export function getUserNotifyNum() {
    return request({
        url: '/api/notify/countNumByUser',
        method: 'get',
    })
}

//公司简介
export function getcompanyProfile() {
    return request({
        url: `/api/settingComProfile/getByLang`,
        method: "get"
    })
}

// 基本原则
export function getBasicPrinciple() {
    return request({
        url: `/api/settingFoundRule/getByLang`,
        method: "get"
    })
}

// 开发合作
export function getPartnership() {
    return request({
        url: `/api/settingDevCooperate/getByLang`,
        method: "get"
    })
}



// 公司资格
export function getCompanyQualification() {
    return request({
        url: `/api/settingGlobal/getByLang`,
        method: "get"
    })
}


// 更改会员等级 接口：

export function updateGrade(params) {
    return request({
        url: `/api/user/updateGrade?gradeId=${params}`,
        method: 'post'
    })
}


// 上传头像
export function updateAvatar(data) {
    return request({
        url: '/api/user/upload',
        method: 'post',
        data
    })
}

// 修改账户名
export function updateUser(data) {
    return request({
        url: '/api/user',
        method: 'put',
        data
    })
}

// 修改用户密码
export function updateUserPassword(data) {
    return request({
        url: '/api/user/updateLoginPassword',
        method: 'put',
        data
    })
}


// 修改资金密码
export function updateMoneyPassword(data) {
    return request({
        url: '/api/user/updateFundPassword',
        method: 'put',
        data
    })
}

// 充值
export function recharge(amount,userId) {
    return request({
        url: `/api/withdraw/recharge/${userId}/${amount}`,
        method: 'post',
    })
}

// 提款
export function withdraw(data) {
    return request({
        url: `/api/withdraw/draw`,
        method: 'post',
        data
    })
}




// 商品主页
export function getProductList(params) {
    return request({
        url: '/api/product/list',
        method: 'get',
        params
    })
}

// 排行榜单
export function getRankList(params) {
    return request({
        url: '/api/order/rankingList',
        method: 'get',
        params
    })
}

// 修改用户信息
export function updateUserInfo(data) {
    return request({
        url: '/api/user/updateUser',
        method: 'put',
        data
    })
}

// 团队
export function getTeamList(params) {
    return request({
        url: '/api/user/getUpToFourLevelInviters',
        method: 'get',
        params
    })
}


