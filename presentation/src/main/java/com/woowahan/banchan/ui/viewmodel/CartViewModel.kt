package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.CartListModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.usecase.FetchCartItemsUseCase
import com.woowahan.domain.usecase.RemoveCartItemUseCase
import com.woowahan.domain.usecase.RemoveCartItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fetchCartItemsUseCase: FetchCartItemsUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val removeCartItemsUseCase: RemoveCartItemsUseCase,
) : ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _cartItems: MutableStateFlow<List<CartListModel>> = MutableStateFlow(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> =
        MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val selectedItems = HashMap<String, CartModel>()

    fun fetchCartItems() {
        if (_dataLoading.value) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchCartItemsUseCase().collect {
                it.onSuccess {
                    _cartItems.value = it
                }.onFailure {
                    it.printStackTrace()
                    it.message?.let { message ->
                        _eventFlow.emit(UiEvent.ShowToast(message))
                    }
                }.also {
                    _dataLoading.value = false
                    if (_refreshDataLoading.value)
                        _refreshDataLoading.value = false
                }
            }
        }
    }

    fun removeCartItem(items: List<CartModel>) {

        viewModelScope.launch {
            _dataLoading.value = true
            removeCartItemsUseCase(items.map { it.hash })
                .onSuccess { isSuccess ->
                    if (isSuccess) {
                        selectedItems.clear()
                    } else {
                        _eventFlow.emit(UiEvent.ShowToast("Can't delete items"))
                    }
                }
                .onFailure {
                    it.printStackTrace()
                    it.message?.let { message ->
                        _eventFlow.emit(UiEvent.ShowToast(message))
                    }
                }.also {
                    _dataLoading.value = false
                    if (_refreshDataLoading.value)
                        _refreshDataLoading.value = false
                }
        }
    }

    val selectAllItems: (Boolean) -> Unit = { isSelected ->
        if (isSelected) {
            _cartItems.value.filterIsInstance<CartListModel.Content>().forEach { cartContent ->
                selectedItems[cartContent.cart.hash] = cartContent.cart
            }
        } else {
            selectedItems.clear()
        }
    }

    val deleteAllSelectedItems: () -> Unit = {
        removeCartItem(selectedItems.values.toList())
    }

    val selectItem: (CartModel, Boolean) -> Unit = { cartModel, isSelected ->

    }

    val deleteItem: (CartModel) -> Unit = { deleteItem ->

    }

    val minusClicked: (CartModel) -> Unit = { minusItem ->

    }

    val plusClicked: (CartModel) -> Unit = { plusItem ->

    }

    val orderItems: (List<CartModel>) -> Unit = { orderItems ->

    }

    fun onRefresh() {
        _refreshDataLoading.value = true
        fetchCartItems()
        viewModelScope
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
    }
}