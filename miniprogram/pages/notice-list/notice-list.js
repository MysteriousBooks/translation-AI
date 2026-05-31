const {get} = require('../../utils/request')
const {API} = require('../../utils/constants')
const {formatTime} = require('../../utils/util')

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
        this.loadList().then(() => wx.stopPullDownRefresh())
    },

    onReachBottom() {
        if (this.data.finished || this.data.loading) return
        this.loadList()
    },

    async loadList() {
        if (this.data.loading) return
        this.setData({loading: true})
        try {
            const res = await get(API.NOTICE_LIST, {
                page: this.data.page,
                size: this.data.size
            })
            const newList = (res.records || []).map(item => ({
                ...item,
                timeStr: formatTime(item.publishTime || item.createTime)
            }))
            this.setData({
                list: [...this.data.list, ...newList],
                total: res.total,
                page: this.data.page + 1,
                finished: this.data.list.length + newList.length >= res.total,
                isEmpty: this.data.list.length === 0 && newList.length === 0
            })
        } catch (e) {
            console.error('加载公告失败', e)
        } finally {
            this.setData({loading: false})
        }
    },

    onItemClick(e) {
        const id = e.currentTarget.dataset.id
        wx.navigateTo({url: `/pages/notice-detail/notice-detail?id=${id}`})
    }
})
