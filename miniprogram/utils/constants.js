// API基础路径 - 开发环境切换
const BASE_URL = 'https://your-api-domain.com'

// API路径
const API = {
    // 认证
    AUTH_WECHAT_LOGIN: '/api/app/auth/login/wechat',
    // 翻译
    TRANSLATE_SUBMIT: '/api/app/translate',
    TRANSLATE_HISTORY: '/api/app/translate/history',
    TRANSLATE_DETAIL: '/api/app/translate/',
    // 用户
    USER_INFO: '/api/app/user/info',
    USER_UPDATE: '/api/app/user/info',
    USER_PASSWORD: '/api/app/user/password',
    // 钱包
    WALLET_BALANCE: '/api/app/wallet/balance',
    WALLET_RECORDS: '/api/app/wallet/records',
    WALLET_RECHARGE: '/api/app/wallet/recharge',
    WALLET_RECHARGE_STATUS: '/api/app/wallet/recharge/status/',
    // 反馈
    FEEDBACK_SUBMIT: '/api/app/feedback',
    // 公告
    NOTICE_LIST: '/api/app/notice/list',
    // 退款
    REFUND_APPLY: '/api/app/refund/apply'
}

// 语言选项
const LANGUAGES = [
    {code: 'zh', name: '中文'},
    {code: 'en', name: '英语'},
    {code: 'ja', name: '日语'},
    {code: 'ko', name: '韩语'},
    {code: 'fr', name: '法语'},
    {code: 'de', name: '德语'},
    {code: 'es', name: '西班牙语'},
    {code: 'ru', name: '俄语'}
]

// 订单状态
const ORDER_STATUS = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
}

// 翻译状态
const TRANSLATE_STATUS = {
    0: '翻译中',
    1: '成功',
    2: '失败'
}

// 钱包流水类型
const WALLET_RECORD_TYPE = {
    1: '充值',
    2: '消费',
    3: '退款'
}

// 支付类型
const PAY_TYPE = {
    1: '支付宝',
    2: '微信支付'
}

module.exports = {
    BASE_URL,
    API,
    LANGUAGES,
    ORDER_STATUS,
    TRANSLATE_STATUS,
    WALLET_RECORD_TYPE,
    PAY_TYPE
}