const {get} = require('../../utils/request')
const {API, TRANSLATE_STATUS} = require('../../utils/constants')
const {formatTime, formatMoney, getLangName} = require('../../utils/util')

Page({
    data: {
        id: null,
        detail: null,
        sourceLangName: '',
        targetLangName: '',
        statusText: '',
        createTime: '',
        costAmount: '',
        charCount: '',
        TRANSLATE_STATUS
    },

    onLoad(options) {
        this.setData({id: options.id})
        this.loadDetail()
    },

    async loadDetail() {
        try {
            const res = await get(API.TRANSLATE_DETAIL + this.data.id)
            this.setData({
                detail: res,
                sourceLangName: getLangName(res.sourceLang),
                targetLangName: getLangName(res.targetLang),
                statusText: TRANSLATE_STATUS[res.status] || '未知',
                createTime: formatTime(res.createTime),
                costAmount: formatMoney(res.costAmount),
                charCount: res.charCount
            })
        } catch (e) {
            console.error('加载详情失败', e)
        }
    },

    onCopySource() {
        if (!this.data.detail) return
        wx.setClipboardData({
            data: this.data.detail.sourceText,
            success: () => wx.showToast({title: '已复制原文', icon: 'success'})
        })
    },

    onCopyResult() {
        if (!this.data.detail) return
        wx.setClipboardData({
            data: this.data.detail.translatedText,
            success: () => wx.showToast({title: '已复制译文', icon: 'success'})
        })
    }
})
