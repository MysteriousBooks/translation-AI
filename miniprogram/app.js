const {checkLogin, wechatLogin} = require('./utils/auth')

App({
    globalData: {
        userInfo: null,
        token: null
    },

    onLaunch() {
        this.checkLoginStatus()
    },

    async checkLoginStatus() {
        try {
            const token = wx.getStorageSync('token')
            if (token) {
                this.globalData.token = token
                const userInfo = wx.getStorageSync('userInfo')
                if (userInfo) {
                    this.globalData.userInfo = userInfo
                }
            } else {
                await this.doWechatLogin()
            }
        } catch (e) {
            console.error('检查登录态失败', e)
        }
    },

    async doWechatLogin() {
        try {
            const {code} = await wx.login()
            const res = await wechatLogin(code)
            if (res) {
                this.globalData.token = res.token
                this.globalData.userInfo = {
                    userId: res.userId,
                    nickname: res.nickname,
                    avatar: res.avatar
                }
                wx.setStorageSync('token', res.token)
                wx.setStorageSync('userInfo', this.globalData.userInfo)
            }
        } catch (e) {
            console.error('微信登录失败', e)
        }
    }
})