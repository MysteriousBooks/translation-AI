/**
 * 日期格式化
 */
const formatTime = (date) => {
    if (!date) return ''
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hour = String(d.getHours()).padStart(2, '0')
    const minute = String(d.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
}

/**
 * 格式化金额
 */
const formatMoney = (amount) => {
    if (amount === null || amount === undefined) return '0.00'
    return Number(amount).toFixed(2)
}

/**
 * 文本截断
 */
const truncateText = (text, maxLen = 50) => {
    if (!text) return ''
    if (text.length <= maxLen) return text
    return text.substring(0, maxLen) + '...'
}

/**
 * 获取语言名称
 */
const getLangName = (code) => {
    const {LANGUAGES} = require('./constants')
    const lang = LANGUAGES.find(l => l.code === code)
    return lang ? lang.name : code
}

module.exports = {
    formatTime,
    formatMoney,
    truncateText,
    getLangName
}