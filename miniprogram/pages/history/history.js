const {get} = require('../../utils/request')
const {API} = require('../../utils/constants')

Page({
    data: {
        list: [],
        page: 1,
        size: 10,
        total: 0,
        loading: false,
        finished: false,
        isEmpty: false
    },

    onLoad() {
        this.loadList()
    },

    onPullDownRefresh() {
        this.setData({page: 1, list: [], finished: false, isEmpty: false})
        this.loadList().then(() => {
            wx.stopPullDownRefresh()
        })
    },

    onReachBottom() {
        if (this.data.finished || this.data.loading) return
        this.loadList()
    },

    async loadList() {
        if (this.data.loading) return
        this.setData({loading: true})
        try {
            const res = await get(API.TRANSLATE_HISTORY, {
                page: this.data.page,
                size: this.data.size
            })
            const newList = res.records || []
            const combinedList = [...this.data.list, ...newList]
            this.setData({
                list: combinedList,
                total: res.total,
                page: this.data.page + 1,
                finished: combinedList.length >= res.total,
                isEmpty: combinedList.length === 0
            })
        } catch (e) {
            console.error('加载历史失败', e)
        } finally {
            this.setData({loading: false})
        }
    },

    onItemClick(e) {
        const id = e.currentTarget.dataset.id
        wx.navigateTo({url: `/pages/history-detail/history-detail?id=${id}`})
    },

    onRetry() {
        this.setData({page: 1, list: [], finished: false, isEmpty: false})
        this.loadList()
    }
})
