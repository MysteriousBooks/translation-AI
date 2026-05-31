package com.translation.app.ui.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.model.Order
import com.translation.app.domain.model.Wallet
import com.translation.app.domain.model.WalletRecord
import com.translation.app.domain.repository.IWalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class WalletUiState(
        val wallet: Wallet? = null,
        val records: List<WalletRecord> = emptyList(),
        val isLoading: Boolean = false,
        val page: Int = 1,
        val total: Long = 0,
        val hasMore: Boolean = true,
        val rechargeOrder: Order? = null,
        val rechargeError: String? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val walletRepo: IWalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    fun getOrderNo(): String = savedStateHandle["orderNo"] ?: ""

    fun loadBalance() {
        viewModelScope.launch {
            walletRepo.getBalance().onSuccess { _uiState.value = _uiState.value.copy(wallet = it) }
        }
    }

    fun loadRecords(refresh: Boolean = false) {
        val page = if (refresh) 1 else _uiState.value.page
        if (!refresh && !_uiState.value.hasMore) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            walletRepo.getRecords(null, page, 20).onSuccess { (list, total) ->
                val current = _uiState.value.records
                val newList = if (refresh) list else current + list
                _uiState.value = _uiState.value.copy(
                        records = newList,
                        page = page + 1, total = total,
                        hasMore = newList.size < total,
                        isLoading = false
                )
            }.onFailure { _uiState.value = _uiState.value.copy(isLoading = false) }
        }
    }

    fun recharge(payType: Int, amount: BigDecimal) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, rechargeError = null)
            walletRepo.recharge(payType, amount).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, rechargeOrder = it)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, rechargeError = it.message)
            }
        }
    }

    fun checkRechargeStatus(orderNo: String, onPaid: () -> Unit) {
        viewModelScope.launch {
            walletRepo.getRechargeStatus(orderNo).onSuccess { order ->
                if (order.status == 1) {
                    loadBalance(); onPaid()
                }
            }
        }
    }
}
