const {post} = require('../../utils/request')
const {API} = require('../../utils/constants')

Page({
    data: {
        content: '',
        submitting: false
    },

    onContentInput(e) {
        this.setData({content: e.detail.value})
    },

    async onSubmit() {
        const {content} = this.data
        if (!content.trim()) {
            wx.showToast({title: '请输入反馈内容', icon: 'none'})
            return
        }
        this.setData({submitting: true})
        try {
            await post(API.FEEDBACK_SUBMIT, {content: content.trim()})
            wx.showToast({title: '提交成功', icon: 'success'})
            setTimeout(() => wx.navigateBack(), 1500)
        } catch (e) {
            console.error('提交反馈失败', e)
        } finally {
            this.setData({submitting: false})
        }
    }
})
