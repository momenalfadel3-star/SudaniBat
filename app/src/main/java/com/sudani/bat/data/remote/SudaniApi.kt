package com.sudani.bat.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface SudaniApi {

    @POST("/sc-onboarding/api/customer/generate-otp")
    suspend fun generateOtp(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<Any>>

    @POST("/sc-onboarding/api/customer/verify-otp")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<Any>>

    @POST("/sc-onboarding/api/customer/onboarding")
    suspend fun completeOnboarding(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<Any>>

    @POST("/sc-dashboard/api/get-dashboard")
    suspend fun getDashboard(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<DashboardData>>

    @POST("/gamification-service/api/reward/claim")
    suspend fun claimReward(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<Any>>

    @POST("/offer-service/api/loyalty/redeem-offer-v2")
    suspend fun redeemOffer(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, Any>
    ): Response<SudaniResponse<Any>>

    @POST("/offer-service/api/catalogue/subscribe")
    suspend fun subscribeService(
        @HeaderMap headers: Map<String, String>,
        @Body payload: Map<String, String>
    ): Response<SudaniResponse<Any>>
}

data class SudaniResponse<T>(
    @SerializedName("responseCode") val responseCode: String,
    @SerializedName("responseMessage") val responseMessage: String,
    @SerializedName("data") val data: T?
)

data class DashboardData(
    @SerializedName("totalLoyaltyPoints") val totalLoyaltyPoints: String?,
    @SerializedName("customerName") val customerName: String?,
    @SerializedName("balance") val balance: String?,
    @SerializedName("accountDetails") val accountDetails: List<AccountDetail>?,
    @SerializedName("subscriberId") val subscriberId: String?
)

data class AccountDetail(
    @SerializedName("label") val label: String?,
    @SerializedName("value") val value: String?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("percentage") val percentage: String?
)
