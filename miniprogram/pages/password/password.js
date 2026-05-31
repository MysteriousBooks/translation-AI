const {put} = require('../../utils/request')
const {API} = require('../../utils/constants')

Page({
    data: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    },

    onOldPasswordInput(e) {
        this.setData({oldPassword: e.detail.value})
    },

    onNewPasswordInput(e) {
        this.setData({newPassword: e.detail.value})
    },

    onConfirmPasswordInput(e) {
        this.setData({confirmPassword: e.detail.value})
    },

    async onSubmit() {
        const {oldPassword, newPassword, confirmPassword} = this.data
        if (!oldPassword) {
            wx.showToast({title: '请输入原密码', icon: 'none'})
            return
        }
        if (!newPassword) {
            wx.showToast({title: '请输入新密码', icon: 'none'})
            return
        }
        if (newPassword.length < 6) {
            wx.showToast({title: '密码至少6位', icon: 'none'})
            return
        }
        if (newPassword !== confirmPassword) {
            wx.showToast({title: '两次密码不一致', icon: 'none'})
            return
        }
        try {
            await put(API.USER_PASSWORD, {
                oldPassword,
                newPassword
            })
            wx.showToast({title: '修改成功', icon: 'success'})
            setTimeout(() => wx.navigateBack(), 1500)
        } catch (e) {
            console.error('修改密码失败', e)
        }
    }
})
