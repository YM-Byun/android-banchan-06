package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.usecase.banchan.FetchBestBanchanUseCase
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.cart.RemoveCartItemUseCase
import com.woowahan.domain.usecase.recentviewed.InsertRecentViewedItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BestBanchanViewModel @Inject constructor(
    private val fetchBestBanchanUseCase: FetchBestBanchanUseCase,
    override val insertCartItemUseCase: InsertCartItemUseCase,
    override val removeCartItemUseCase: RemoveCartItemUseCase,
    private val insertRecentViewedItemUseCase: InsertRecentViewedItemUseCase
) : BaseCartUpdateViewModel() {
    override val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _banchans: MutableStateFlow<List<BestBanchanModel>> = MutableStateFlow(emptyList())
    val banchans = _banchans.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchBestBanchans()
    }

    private fun fetchBestBanchans() {
        viewModelScope.launch {
            _dataLoading.value = true
            fetchBestBanchanUseCase.invoke()
                .flowOn(Dispatchers.Default)
                .collect { res ->
                    res.onSuccess {
                        _banchans.value = it
                    }.onFailure {
                        it.printStackTrace()
                        it.message?.let { message ->
                            _eventFlow.emit(UiEvent.ShowToast(message))
                        }
                    }.also {
                        _dataLoading.value = false
                    }
                }
        }
    }

    val clickInsertCartButton: (BanchanModel, Boolean) -> (Unit) = { banchan, isCartItem ->
        viewModelScope.launch {
            when (isCartItem) {
                true -> removeItemFromCart(banchan)
                else -> {
                    val dialog = CartItemInsertBottomSheet(banchan) { item, count ->
                        insertItemsToCart(item, count)
                    }
                    _eventFlow.emit(UiEvent.ShowCartBottomSheet(dialog))
                }
            }
        }
    }

    val itemClickListener: (BanchanModel) -> Unit = { banchan ->
        viewModelScope.launch {
            insertRecentViewedItemUseCase(banchan, Calendar.getInstance().time)
                .flowOn(Dispatchers.Default)
                .collect()
            _eventFlow.emit(UiEvent.ShowDetailView(banchan))
        }
    }

    override val insertCartResultEvent: (BaseBanchan, Boolean) -> Unit
        get() = { _, _ ->
            viewModelScope.launch {
                _eventFlow.emit(
                    UiEvent.ShowDialog(
                        getCartItemUpdateDialog("선택한 상품이 장바구니에 담겼습니다"){
                            viewModelScope.launch {
                                _eventFlow.emit(UiEvent.ShowCartView)
                            }
                        }
                ))
            }
        }
    override val insertCartThrowableEvent: (Throwable) -> Unit
        get() = {
            viewModelScope.launch {
                it.printStackTrace()
                it.message?.let { message ->
                    _eventFlow.emit(UiEvent.ShowSnackBar(message))
                }
            }
        }

    override val removeCartResultEvent: (BaseBanchan, Boolean) -> Unit
        get() = { _, _ ->
            viewModelScope.launch {
                _eventFlow.emit(
                    UiEvent.ShowDialog(
                        getCartItemUpdateDialog("선택한 상품이 장바구니에서 제거되었습니다"){
                            viewModelScope.launch {
                                _eventFlow.emit(UiEvent.ShowCartView)
                            }
                        }
                ))
            }
        }
    override val removeCartThrowableEvent: (Throwable) -> Unit
        get() = {
            viewModelScope.launch{
                it.printStackTrace()
                it.message?.let { message ->
                    _eventFlow.emit(UiEvent.ShowToast(message))
                }
            }
        }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
        object ShowCartView : UiEvent()
        data class ShowDetailView(val banchanModel: BanchanModel) : UiEvent()
    }
}