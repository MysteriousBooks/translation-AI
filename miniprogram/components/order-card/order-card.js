const {formatTime, formatMoney} = require('../../utils/util')
const {ORDER_STATUS} = require('../../utils/constants')

Component({
    properties: {
        item: {
            type: Object,
            value: {}
        }
    },
    data: {
        orderNo: '',
        amount: '',
        statusText: '',
        createTime: ''
    },
    observers: {
        'item': function (item) {
            if (item) {
                this.setData({
                    orderNo: item.orderNo || '',
                    amount: formatMoney(item.amount),
                    statusText: ORDER_STATUS[item.status] || '未知',
                    createTime: formatTime(item.createTime)
                })
            }
        }
    }
})
