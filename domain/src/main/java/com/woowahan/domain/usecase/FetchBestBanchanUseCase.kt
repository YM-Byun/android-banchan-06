package com.woowahan.domain.usecase

import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository

class FetchBestBanchanUseCase(
    private val banchanRepository: BanchanRepository
){
    suspend operator fun invoke(): Result<List<BestBanchanModel>>{
        return kotlin.runCatching {
            listOf(
                BestBanchanModel.empty().copy(viewType = BestBanchanModel.ViewType.Banner)
            ) + banchanRepository.fetchBestBanchan().getOrThrow()
        }
    }
}