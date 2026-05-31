const {get, post} = require('../../utils/request')
const {API, LANGUAGES} = require('../../utils/constants')
const {checkLogin} = require('../../utils/auth')

Page({
    data: {
        sourceLang: 'zh',
        targetLang: 'en',
        languageNames: LANGUAGES.map(l => l.name),
        sourceLangName: '中文',
        targetLangName: '英语',
        sourceIndex: 0,
        targetIndex: 1,
        sourceText: '',
        charCount: 0,
        maxChars: 5000,
        translating: false,
        result: null,
        translatedText: ''
    },

    onLoad() {
        if (!checkLogin()) return
    },

    onSourceLangChange(e) {
        const idx = Number(e.detail.value)
        this.setData({
            sourceLang: LANGUAGES[idx].code,
            sourceLangName: LANGUAGES[idx].name,
            sourceIndex: idx
        })
    },

    onTargetLangChange(e) {
        const idx = Number(e.detail.value)
        this.setData({
            targetLang: LANGUAGES[idx].code,
            targetLangName: LANGUAGES[idx].name,
            targetIndex: idx
        })
    },

    onSwapLangs() {
        const {sourceLang, targetLang, sourceLangName, targetLangName, sourceIndex, targetIndex} = this.data
        this.setData({
            sourceLang: targetLang,
            targetLang: sourceLang,
            sourceLangName: targetLangName,
            targetLangName: sourceLangName,
            sourceIndex: targetIndex,
            targetIndex: sourceIndex
        })
    },

    onInputChange(e) {
        const text = e.detail.value
        this.setData({
            sourceText: text,
            charCount: text.length
        })
    },

    onClear() {
        this.setData({sourceText: '', charCount: 0, result: null, translatedText: ''})
    },

    async onTranslate() {
        const {sourceText, sourceLang, targetLang} = this.data
        if (!sourceText.trim()) {
            wx.showToast({title: '请输入要翻译的文本', icon: 'none'})
            return
        }
        this.setData({translating: true})
        try {
            const res = await post(API.TRANSLATE_SUBMIT, {
                sourceLang,
                targetLang,
                sourceText: sourceText.trim()
            })
            this.setData({
                result: res,
                translatedText: res.translatedText || ''
            })
        } catch (e) {
            console.error('翻译失败', e)
        } finally {
            this.setData({translating: false})
        }
    },

    onCopyResult() {
        const {translatedText} = this.data
        if (!translatedText) return
        wx.setClipboardData({
            data: translatedText,
            success: () => {
                wx.showToast({title: '已复制', icon: 'success'})
            }
        })
    },

    onViewDetail() {
        const {result} = this.data
        if (!result) return
        wx.navigateTo({url: `/pages/history-detail/history-detail?id=${result.id}`})
    }
})
