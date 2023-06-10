package com.yuba.cafe.service

import com.yuba.cafe.model.Address
import com.yuba.cafe.model.OrderLine
import com.yuba.cafe.model.Snack
import com.yuba.cafe.model.UserProfile
import com.yuba.cafe.model.order.Order
import com.yuba.cafe.repo.SearchCategoryCollection
import com.yuba.cafe.repo.SearchRepo
import com.yuba.cafe.repo.SearchSuggestionGroup
import com.yuba.cafe.request.SignInReq
import com.yuba.cafe.request.SignUpReq
import com.yuba.cafe.response.FilterResp
import com.yuba.cafe.response.SignInResp
import com.yuba.cafe.response.SignUpResp
import com.yuba.cafe.response.SnackCollectionResp
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    companion object {
        var apiService: ApiService? = null
        fun getInstance(): ApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("http://34.131.92.91:8080/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiService::class.java)
            }
            return apiService!!
        }
    }

    @POST("public/user/signin")
    suspend fun signIn(@Body signInReq: SignInReq): Response<SignInResp>

    @POST("public/user/signup")
    suspend fun signUp(@Body signInReq: SignUpReq): Response<SignUpResp>

    @GET("public/categories")
    suspend fun getCategories(): Response<List<SearchCategoryCollection>>

    @GET("public/suggestion")
    suspend fun getSuggestions(): Response<List<SearchSuggestionGroup>>

    @GET("search")
    fun search(query: String) = SearchRepo.search(query)

    @GET("public/feed")
    suspend fun getFeed(): Response<List<SnackCollectionResp>>

    @GET("public/filters")
    suspend fun getFilters(): Response<List<FilterResp>>

    @GET("secure/profile")
    suspend fun getProfile(@Header("Authorization") bearerToken: String): Response<UserProfile>

    @GET("secure/cart")
    suspend fun getCart(@Header("Authorization") bearerToken: String): Response<List<OrderLine>>

    @GET("secure/addToCart/{snackId}")
    suspend fun addToCart(
        @Header("Authorization") bearerToken: String,
        @Path("snackId") snackId: Long
    ): Response<List<OrderLine>>

    @GET("secure/removeFromCart/{snackId}")
    suspend fun removeFromCart(
        @Header("Authorization") bearerToken: String,
        @Path("snackId") snackId: Long
    ): Response<List<OrderLine>>

    @GET("secure/addQuantity/{snackId}")
    suspend fun addQuantity(
        @Header("Authorization") bearerToken: String,
        @Path("snackId") snackId: Long
    ): Response<List<OrderLine>>

    @GET("secure/subQuantity/{snackId}")
    suspend fun subQuantity(
        @Header("Authorization") bearerToken: String,
        @Path("snackId") snackId: Long
    ): Response<List<OrderLine>>

    @GET("secure/cart/inspired")
    suspend fun getCartInspired(@Header("Authorization") bearerToken: String): Response<SnackCollectionResp>

    @GET("secure/address/currentAddress")
    suspend fun getCurrentAddress(@Header("Authorization") bearerToken: String): Response<Address>

    @GET("secure/checkout")
    suspend fun checkout(@Header("Authorization") bearerToken: String): Response<List<OrderLine>>

    @GET("public/snack/{snackId}")
    suspend fun getSnackById(
        @Path("snackId") snackId: Long,
        @Header("Authorization") bearerToken: String
    ): Response<Snack>

    @GET("secure/order")
    suspend fun getAllOrders(@Header("Authorization") bearerToken: String): Response<List<Order>>

    @GET("secure/snack")
    suspend fun getAllSnacks(@Header("Authorization") bearerToken: String): Response<List<Snack>>

    @POST("secure/snack")
    suspend fun updateSnack(
        @Header("Authorization") bearerToken: String,
        @Body snack: Snack
    ): Response<Snack>

    @PUT("secure/snack")
    suspend fun addSnack(
        @Header("Authorization") bearerToken: String,
        @Body snack: Snack
    ): Response<Snack>

    @GET("secure/order/accept/{orderId}")
    suspend fun acceptOrder(
        @Header("Authorization") bearerToken: String,
        @Path("orderId") orderId: Long,
    ): Response<Order>

    @GET("secure/order/reject/{orderId}")
    suspend fun rejectOrder(
        @Header("Authorization") bearerToken: String,
        @Path("orderId") orderId: Long,
    ): Response<Order>
}