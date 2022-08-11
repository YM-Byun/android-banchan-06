package com.woowahan.domain.usecase

import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository

class FetchBestBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsUseCase: FetchCartItemsUseCase
){
    suspend operator fun invoke(): Result<List<BestBanchanModel>>{
        return kotlin.runCatching {
            val cart = fetchCartItemsUseCase().getOrThrow()
            listOf(
                BestBanchanModel.empty().copy(viewType = BestBanchanModel.ViewType.Banner)
            ) + banchanRepository.fetchBestBanchan().getOrThrow().map {
                it.banchans.map { banchan ->
                    if(cart[banchan.hash] != null)
                        banchan.copy(isCartItem = true)
                    else
                        banchan
                }
                it
            }
        }
    }
}