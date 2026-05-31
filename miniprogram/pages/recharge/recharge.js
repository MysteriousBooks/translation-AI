const {post, get} = require('../../utils/request')
const {API} = require('../../utils/constants')
const {formatMoney} = require('../../utils/util')

Page({
    data: {
        amountOptions: [10, 20, 50, 100, 200, 500],
        selectedAmount: 0,
        customAmount: '',
        balance: '0.00',
        submitting: false
    },

    onLoad() {
        this.loadBalance()
    },

    async loadBalance() {
        try {
            const res = await get(API.WALLET_BALANCE, null, {loading: false})
            this.setData({balance: formatMoney(res.balance)})
        } catch (e) {
            console.error('加载余额失败', e)
        }
    },

    onSelectAmount(e) {
        const amount = e.currentTarget.dataset.amount
        this.setData({selectedAmount: amount, customAmount: ''})
    },

    onCustomInput(e) {
        const value = e.detail.value
        this.setData({
            customAmount: value,
            selectedAmount: value ? parseFloat(value) : 0
        })
    },

    async onSubmit() {
        const {selectedAmount, customAmount} = this.data
        const amount = customAmount ? parseFloat(customAmount) : selectedAmount
        if (!amount || amount <= 0) {
            wx.showToast({title: '请选择或输入充值金额', icon: 'none'})
            return
        }
        if (amount < 1) {
            wx.showToast({title: '最低充值1元', icon: 'none'})
            return
        }
        this.setData({submitting: true})
        try {
            const res = await post(API.WALLET_RECHARGE, {
                payType: 2, // 微信支付
                amount: amount
            })
            if (res && res.payData) {
                // 发起微信支付
                this.doWechatPay(res.payData, res.orderNo)
            } else {
                wx.showToast({title: '创建订单成功', icon: 'success'})
                setTimeout(() => wx.navigateBack(), 1500)
            }
        } catch (e) {
            console.error('充值失败', e)
        } finally {
            this.setData({submitting: false})
        }
    },

    doWechatPay(payData, orderNo) {
        const params = JSON.parse(payData)
        wx.requestPayment({
            timeStamp: params.timeStamp,
            nonceStr: params.nonceStr,
            package: params.package,
            signType: params.signType || 'MD5',
            paySign: params.paySign,
            success: () => {
                wx.showToast({title: '充值成功', icon: 'success'})
                this.checkPayStatus(orderNo)
            },
            fail: (err) => {
                if (err.errMsg !== 'requestPayment:fail cancel') {
                    wx.showToast({title: '支付失败', icon: 'none'})
                }
            }
        })
    },

    async checkPayStatus(orderNo) {
        try {
            await get(API.WALLET_RECHARGE_STATUS + orderNo)
            setTimeout(() => wx.navigateBack(), 1000)
        } catch (e) {
            console.error('查询支付状态失败', e)
        }
    }
})
