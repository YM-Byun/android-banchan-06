package com.woowahan.domain.usecase

import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchBestBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
){
    suspend operator fun invoke(): Flow<Result<List<BestBanchanModel>>> {
        return banchanRepository.fetchBestBanchan().map {
            kotlin.runCatching {
                val cart = fetchCartItemsKeyUseCase().getOrThrow()
                listOf(
                    BestBanchanModel.empty().copy(viewType = BestBanchanModel.ViewType.Banner)
                ) + it.getOrThrow().map {
                    it.banchans.map { banchan ->
                        if(cart.contains(banchan.hash))
                            banchan.copy(isCartItem = true)
                        else
                            banchan
                    }
                    it
                }
            }
        }
    }
}