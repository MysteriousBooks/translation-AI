package com.translation.app.domain.model

import java.math.BigDecimal

data class Order(
        val id: Long, val orderNo: String, val payType: Int,
        val amount: BigDecimal, val status: Int,
        val payTime: String?, val createTime: String?, val payData: String?
)