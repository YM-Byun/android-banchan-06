package com.woowahan.data.apiservice

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 본래 service 들 마다 각각 test 파일을 생성해야 하지만, 일단 하나로 통합하여 생성
 */

@OptIn(ExperimentalCoroutinesApi::class)
class BanchansRetrofitApiServiceTest {

    private lateinit var realRetrofit: Retrofit
    private val baseUrl = "https://api.codesquad.kr/onban/"

    @Before
    fun initRetrofit(){
        realRetrofit = Retrofit.Builder().apply {
            baseUrl(baseUrl)
            client(OkHttpClient.Builder().apply {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }.build())
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Test
    fun fetchBestBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(BestBanchanApiService::class.java)

        //When
        val res = service.fetchBestBanchans()

        //Then
        println(res.body()?.toString())
        Assertions.assertThat(res.body()?.body).isNotNull
    }

    @Test
    fun fetchMainDishBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(MainDishBanchanApiService::class.java)

        //WHen
        val res = service.fetchMainDishBanchans()

        //Then
        println(res.body()?.toString())
        Assertions.assertThat(res.body()?.body).isNotNull
    }

    @Test
    fun fetchSoupDishBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(SoupDishBanchanApiService::class.java)

        //When
        val res = service.fetchSoupDishBanchans()

        //Then
        println(res.body()?.toString())
        Assertions.assertThat(res.body()?.body).isNotNull
    }

    @Test
    fun fetchSideDishBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(SideDishBanchanApiService::class.java)

        //When
        val res = service.fetchSideDishBanchans()

        //Then
        println(res.body()?.toString())
        Assertions.assertThat(res.body()?.body).isNotNull
    }
}