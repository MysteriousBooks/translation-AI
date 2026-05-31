package com.translation.app.util

object Constants {
    const val MAX_TRANSLATE_CHARS = 5000
}

enum class Language(val code: String, val displayName: String) {
    ZH("zh", "中文"), EN("en", "英语"), JA("ja", "日语"), KO("ko", "韩语"),
    FR("fr", "法语"), DE("de", "德语"), ES("es", "西班牙语"), RU("ru", "俄语")
}

enum class TranslateStatus(val value: Int, val label: String) {
    TRANSLATING(0, "翻译中"), SUCCESS(1, "成功"), FAILED(2, "失败");

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: FAILED
    }
}

enum class OrderStatus(val value: Int, val label: String) {
    PENDING(0, "待支付"), PAID(1, "已支付"), CANCELLED(2, "已取消"), REFUNDED(3, "已退款");

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: CANCELLED
    }
}

enum class WalletRecordType(val value: Int, val label: String) {
    RECHARGE(1, "充值"), CONSUMPTION(2, "消费"), REFUND(3, "退款");

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: CONSUMPTION
    }
}

enum class PayType(val value: Int, val label: String) { ALIPAY(1, "支付宝"), WECHAT(2, "微信支付") }
enum class LoginType(val value: Int) { EMAIL(1), WECHAT(2), ALIPAY(3) }

val Languages = listOf(
        "zh" to "中文", "en" to "英语", "ja" to "日语", "ko" to "韩语",
        "fr" to "法语", "de" to "德语", "es" to "西班牙语", "ru" to "俄语"
)

val RechargeAmounts = listOf("10", "20", "50", "100", "200")
