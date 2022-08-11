package com.woowahan.banchan.di

import com.woowahan.data.repository.CartRepositoryImpl
import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideFetchBestDishBanchanUseCase(impl: BanchanRepository) =
        FetchBestBanchanUseCase(impl)

    @Provides
    fun provideFetchMainDishBanchanUseCase(impl: BanchanRepository) =
        FetchMainDishBanchanUseCase(impl)

    @Provides
    fun provideFetchSoupDishBanchanUseCase(impl: BanchanRepository) =
        FetchSoupDishBanchanUseCase(impl)

    @Provides
    fun provideFetchSideDishBanchanUseCase(impl: BanchanRepository) =
        FetchSideDishBanchanUseCase(impl)

    @Provides
    fun provideFetchCartItemsUseCase(impl: CartRepositoryImpl) =
        FetchCartItemsUseCase(impl)

    @Provides
    fun provideRemoveCartItemUseCase(impl: CartRepositoryImpl) =
        RemoveCartItemUseCase(impl)

    @Provides
    fun provideRemoveCartItemsUseCase(impl: CartRepositoryImpl) =
        RemoveCartItemsUseCase(impl)

    @Provides
    fun provideUpdateCartItemUseCase(impl: CartRepositoryImpl) =
        UpdateCartItemUseCase(impl)

}