package com.translation.app.data.dto.refund

import kotlinx.serialization.Serializable

@Serializable
data class RefundApplyRequest(val orderId: Long, val reason: String)
