package com.alpha.selfemployment.Views.Explore.data.network

import com.alpha.selfemployment.Views.Explore.domain.model.ExploreMostPopularResponse
import com.alpha.selfemployment.Views.Explore.domain.model.ExploreSearchProfileResponse
import com.alpha.selfemployment.Views.Explore.domain.model.SearchHistoryResponse
import com.alpha.selfemployment.Views.Home.Videos.domain.model.HomeReelsResponseCommon
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostLikeResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ExploreAPI  {


    @POST("searchProperty")
    suspend fun getExploreProperty(@Body requestBody: RequestBody) : HomeReelsResponseCommon

    @POST("searchProfile")
    suspend fun searchProfile(@Body requestBody: RequestBody) : ExploreSearchProfileResponse

    @POST("get_search_history")
    suspend fun getSearchHistory(@Body requestBody: RequestBody) : SearchHistoryResponse



    @POST("delete_search_history")
    suspend fun deleteSearchHistory(@Body requestBody: RequestBody) : PostLikeResponse


    @POST("most_popular")
    suspend fun exploreMostPopular(@Body requestBody: RequestBody) : ExploreMostPopularResponse



}