const {post} = require('./request')
const {API} = require('./constants')

/**
 * 微信登录
 */
const wechatLogin = (code) => {
    return post(API.AUTH_WECHAT_LOGIN, {code}, {loading: false})
        .then(res => {
            if (res && res.token) {
                return res
            }
            return null
        })
        .catch(err => {
            console.error('微信登录失败', err)
            return null
        })
}

/**
 * 检查登录态
 */
const checkLogin = () => {
    const token = wx.getStorageSync('token')
    return !!token
}

/**
 * 获取Token
 */
const getToken = () => {
    return wx.getStorageSync('token') || ''
}

/**
 * 清除登录信息
 */
const clearAuth = () => {
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    const app = getApp()
    if (app) {
        app.globalData.token = null
        app.globalData.userInfo = null
    }
}

module.exports = {
    wechatLogin,
    checkLogin,
    getToken,
    clearAuth
}