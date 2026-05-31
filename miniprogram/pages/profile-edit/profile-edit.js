const {get, put, post} = require('../../utils/request')
const {API} = require('../../utils/constants')

Page({
    data: {
        nickname: '',
        avatar: ''
    },

    onLoad() {
        const userInfo = wx.getStorageSync('userInfo')
        this.setData({
            nickname: userInfo.nickname || '',
            avatar: userInfo.avatar || ''
        })
    },

    onChooseAvatar() {
        wx.chooseImage({
            count: 1,
            sizeType: ['compressed'],
            sourceType: ['album', 'camera'],
            success: (res) => {
                const tempFilePath = res.tempFilePaths[0]
                this.setData({avatar: tempFilePath})
                this._uploadAvatar(tempFilePath)
            }
        })
    },

    _uploadAvatar(filePath) {
        wx.uploadFile({
            url: API.USER_UPDATE,
            filePath: filePath,
            name: 'avatar',
            header: {
                'Authorization': 'Bearer ' + wx.getStorageSync('token')
            },
            success: (res) => {
                const data = JSON.parse(res.data)
                if (data.code === 200) {
                    this.setData({avatar: data.data || this.data.avatar})
                    const userInfo = wx.getStorageSync('userInfo')
                    userInfo.avatar = this.data.avatar
                    wx.setStorageSync('userInfo', userInfo)
                } else {
                    wx.showToast({title: '头像上传失败', icon: 'none'})
                }
            },
            fail: () => {
                wx.showToast({title: '头像上传失败', icon: 'none'})
            }
        })
    },

    async onSave() {
        const {nickname} = this.data
        if (!nickname.trim()) {
            wx.showToast({title: '请输入昵称', icon: 'none'})
            return
        }
        try {
            await put(API.USER_UPDATE, {nickname: nickname.trim()})
            wx.showToast({title: '保存成功', icon: 'success'})
            setTimeout(() => wx.navigateBack(), 1500)
        } catch (e) {
            console.error('保存失败', e)
        }
    }
})
