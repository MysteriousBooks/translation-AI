const {get} = require('../../utils/request')
const {API, WALLET_RECORD_TYPE} = require('../../utils/constants')
const {formatMoney, formatTime} = require('../../utils/util')

Page({
    data: {
        balance: '0.00',
        totalConsume: '0.00',
        records: [],
        page: 1,
        size: 10,
        total: 0,
        loading: false,
        finished: false,
        isEmpty: false,
        WALLET_RECORD_TYPE
    },

    onLoad() {
        this.loadBalance()
        this.loadRecords()
    },

    onPullDownRefresh() {
        this.setData({page: 1, records: [], finished: false, isEmpty: false})
        Promise.all([this.loadBalance(), this.loadRecords()]).then(() => {
            wx.stopPullDownRefresh()
        })
    },

    onReachBottom() {
        if (this.data.finished || this.data.loading) return
        this.loadRecords()
    },

    async loadBalance() {
        try {
            const res = await get(API.WALLET_BALANCE)
            this.setData({
                balance: formatMoney(res.balance),
                totalConsume: formatMoney(res.totalConsume)
            })
        } catch (e) {
            console.error('加载余额失败', e)
        }
    },

    async loadRecords() {
        if (this.data.loading) return
        this.setData({loading: true})
        try {
            const res = await get(API.WALLET_RECORDS, {
                page: this.data.page,
                size: this.data.size
            })
            const newRecords = (res.records || []).map(r => ({
                ...r,
                amountStr: formatMoney(r.amount),
                timeStr: formatTime(r.createTime),
                typeText: WALLET_RECORD_TYPE[r.type] || '未知'
            }))
            const combinedRecords = [...this.data.records, ...newRecords]
            this.setData({
                records: combinedRecords,
                total: res.total,
                page: this.data.page + 1,
                finished: combinedRecords.length >= res.total,
                isEmpty: combinedRecords.length === 0
            })
        } catch (e) {
            console.error('加载流水失败', e)
        } finally {
            this.setData({loading: false})
        }
    },

    onRecharge() {
        wx.navigateTo({url: '/pages/recharge/recharge'})
    }
})
