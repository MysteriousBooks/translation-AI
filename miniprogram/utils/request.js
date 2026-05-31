const {BASE_URL} = require('./constants')

/**
 * 统一请求封装
 * 自动携带Token、统一错误处理、401自动重新登录
 */
const request = (options) => {
    const {url, method = 'GET', data, header = {}, loading = true} = options

    if (loading) {
        wx.showLoading({title: '加载中...', mask: true})
    }

    const token = wx.getStorageSync('token')
    if (token) {
        header['Authorization'] = `Bearer ${token}`
    }

    return new Promise((resolve, reject) => {
        wx.request({
            url: `${BASE_URL}${url}`,
            method,
            data,
            header: {
                'Content-Type': 'application/json',
                ...header
            },
            success: (res) => {
                if (loading) {
                    wx.hideLoading()
                }
                if (res.statusCode === 200) {
                    if (res.data.code === 200) {
                        resolve(res.data.data)
                    } else if (res.data.code === 401) {
                        handleTokenExpired()
                        reject(res.data)
                    } else {
                        wx.showToast({title: res.data.message || '请求失败', icon: 'none'})
                        reject(res.data)
                    }
                } else {
                    wx.showToast({title: '网络请求失败', icon: 'none'})
                    reject(res)
                }
            },
            fail: (err) => {
                if (loading) {
                    wx.hideLoading()
                }
                wx.showToast({title: '网络连接失败', icon: 'none'})
                reject(err)
            }
        })
    })
}

// GET请求
const get = (url, data, options = {}) => {
    return request({url, method: 'GET', data, ...options})
}

// POST请求
const post = (url, data, options = {}) => {
    return request({url, method: 'POST', data, ...options})
}

// PUT请求
const put = (url, data, options = {}) => {
    return request({url, method: 'PUT', data, ...options})
}

// 处理Token过期
function handleTokenExpired() {
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    const app = getApp()
    app.doWechatLogin()
}

module.exports = {
    request,
    get,
    post,
    put
}