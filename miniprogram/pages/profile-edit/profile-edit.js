const {get, put} = require('../../utils/request')
const {API} = require('../../utils/constants')

Page({
    data: {
        userInfo: null,
        nickname: '',
        avatar: ''
    },

    onLoad() {
        this.loadUserInfo()
    },

    async loadUserInfo() {
        try {
            const res = await get(API.USER_INFO, null, {loading: false})
            this.setData({
                userInfo: res,
                nickname: res.nickname || '',
                avatar: res.avatar || ''
            })
        } catch (e) {
            console.error('加载用户信息失败', e)
        }
    },

    onNicknameInput(e) {
        this.setData({nickname: e.detail.value})
    },

    onChooseAvatar() {
        wx.chooseImage({
            count: 1,
            sizeType: ['compressed'],
            sourceType: ['album', 'camera'],
            success: (res) => {
                this.setData({avatar: res.tempFilePaths[0]})
                // TODO: 上传头像到服务器
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
