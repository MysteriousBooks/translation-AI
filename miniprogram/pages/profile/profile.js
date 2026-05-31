const {get} = require('../../utils/request')
const {API} = require('../../utils/constants')
const {formatMoney} = require('../../utils/util')
const {checkLogin} = require('../../utils/auth')

Page({
    data: {
        userInfo: null,
        balance: '0.00'
    },

    onShow() {
        if (checkLogin()) {
            this.loadUserInfo()
        }
    },

    async loadUserInfo() {
        try {
            const res = await get(API.USER_INFO, null, {loading: false})
            this.setData({
                userInfo: res,
                balance: formatMoney(res.balance)
            })
            wx.setStorageSync('userInfo', {
                userId: res.id,
                nickname: res.nickname,
                avatar: res.avatar
            })
        } catch (e) {
            console.error('加载用户信息失败', e)
        }
    },

    onEditProfile() {
        wx.navigateTo({url: '/pages/profile-edit/profile-edit'})
    },

    onChangePassword() {
        wx.navigateTo({url: '/pages/password/password'})
    },

    onFeedback() {
        wx.navigateTo({url: '/pages/feedback/feedback'})
    },

    onNoticeList() {
        wx.navigateTo({url: '/pages/notice-list/notice-list'})
    },

    onWallet() {
        wx.switchTab({url: '/pages/wallet/wallet'})
    }
})
