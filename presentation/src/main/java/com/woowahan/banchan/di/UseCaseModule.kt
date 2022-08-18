package com.woowahan.banchan.di

import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.repository.CartRepository
import com.woowahan.domain.repository.RecentViewedRepository
import com.woowahan.domain.usecase.banchan.FetchBestBanchanUseCase
import com.woowahan.domain.usecase.banchan.FetchMainDishBanchanUseCase
import com.woowahan.domain.usecase.banchan.FetchSideDishBanchanUseCase
import com.woowahan.domain.usecase.banchan.FetchSoupDishBanchanUseCase
import com.woowahan.domain.usecase.cart.*
import com.woowahan.domain.usecase.recentviewed.FetchRecentViewedItemUseCase
import com.woowahan.domain.usecase.recentviewed.InsertRecentViewedItemUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideFetchBestDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchBestBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideFetchMainDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchMainDishBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideFetchSoupDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchSoupDishBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideFetchSideDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchSideDishBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideGetCartSizeUseCase(repo: CartRepository) =
        GetCartItemsSizeFlowUseCase(repo)

    @Provides
    fun provideFetchCartItemsUseCase(repo: CartRepository) =
        FetchCartItemsUseCase(repo)

    @Provides
    fun provideFetchCartItemsKeyUseCase(repo: CartRepository) =
        FetchCartItemsKeyUseCase(repo)

    @Provides
    fun provideInsertCartItemsUseCase(repo: CartRepository) =
        InsertCartItemUseCase(repo)

    @Provides
    fun provideRemoveCartItemUseCase(repo: CartRepository) =
        RemoveCartItemUseCase(repo)

    @Provides
    fun provideUpdateCartItemCountUseCase(repo: CartRepository) =
        UpdateCartItemCountUseCase(repo)

    @Provides
    fun provideUpdateCartItemSelectUseCase(repo: CartRepository) =
        UpdateCartItemSelectUseCase(repo)

    @Provides
    fun provideFetchAllRecentViewedItemUseCase(repo: RecentViewedRepository) =
        FetchRecentViewedItemUseCase(repo)

    @Provides
    fun provideInsertRecentViewedItemUseCase(repo: RecentViewedRepository) =
        InsertRecentViewedItemUseCase(repo)
}