package com.sudani.bat.data.repository

import com.google.gson.Gson
import com.sudani.bat.data.local.NumberDao
import com.sudani.bat.data.model.SudaniNumber
import com.sudani.bat.data.remote.DashboardData
import com.sudani.bat.data.remote.SudaniApi
import com.sudani.bat.data.remote.SudaniResponse
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class SudaniRepository(
    private val api: SudaniApi,
    private val dao: NumberDao
) {
    private val gson = Gson()
    private val deviceId = "ginkgo_xiaomi_ginkgo_Redmi Note 8_Xiaomi_qcom_PKQ1.190616.001"
    private val tenant = "tec_sudatel"

    val allNumbers: Flow<List<SudaniNumber>> = dao.getAllNumbers()

    private fun getBaseHeaders(msisdn: String? = null): MutableMap<String, String> {
        val headers = mutableMapOf(
            "Content-Type" to "application/json",
            "is-b2b" to "false",
            "device-id" to deviceId,
            "tenant" to tenant,
            "subscriber-type" to "Prepaid",
            "channel" to "sc_app",
            "transaction-token" to "abc",
            "platform" to "android",
            "language" to "ar"
        )
        msisdn?.let { 
            headers["msisdn"] = it
            headers["primary-msisdn"] = it
        }
        return headers
    }

    private fun getAuthHeaders(msisdn: String, token: String, userData: Map<String, Any>, currentPoints: String = "0"): Map<String, String> {
        val headers = getBaseHeaders(msisdn)
        headers["x-auth-selfcare-key"] = token
        headers["user-id"] = userData["customerId"]?.toString() ?: ""
        headers["primary-offer-id"] = userData["subscriberId"]?.toString() ?: ""
        headers["current-loyalty-points"] = currentPoints
        
        val pricePlan = userData["primaryOfferName"]?.toString() 
            ?: userData["pricePlan"]?.toString() 
            ?: "Sudani_agent"
        
        headers["price-plan"] = pricePlan
        headers["primary-offer-name"] = pricePlan
        headers["sim-category"] = "B2C"
        headers["lastlogin"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        return headers
    }

    suspend fun generateOtp(msisdn: String): Result<String> {
        val payload = mapOf(
            "msisdn" to msisdn,
            "primaryMsisdn" to msisdn,
            "method" to "SMS",
            "useCase" to "ONBOARDING",
            "platform" to "android",
            "language" to "en"
        )
        return try {
            val response = api.generateOtp(getBaseHeaders(msisdn), payload)
            if (response.isSuccessful && response.body()?.responseCode == "200") {
                Result.success(response.body()?.responseMessage ?: "OTP Sent")
            } else {
                Result.failure(Exception(response.body()?.responseMessage ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(msisdn: String, otp: String): Result<Pair<String, String>> {
        val payload = mapOf(
            "msisdn" to msisdn,
            "primaryMsisdn" to msisdn,
            "otp" to otp,
            "method" to "SMS",
            "useCase" to "ONBOARDING",
            "channel" to "sc_app",
            "platform" to "android",
            "language" to "en"
        )
        return try {
            val response = api.verifyOtp(getBaseHeaders(msisdn), payload)
            val body = response.body()
            if (response.isSuccessful && body?.responseCode == "200") {
                // Here we would normally extract token and user data from the response
                // For now returning mock/placeholder success
                Result.success("token_here" to gson.toJson(body.data))
            } else {
                Result.failure(Exception(body?.responseMessage ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshDashboard(number: SudaniNumber): Result<DashboardData> {
        val userData = gson.fromJson(number.userDataJson, Map::class.java) as Map<String, Any>
        val headers = getAuthHeaders(number.msisdn, number.token, userData)
        val payload = mapOf("subscriberId" to (userData["subscriberId"]?.toString() ?: ""))
        
        return try {
            val response = api.getDashboard(headers, payload)
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                val data = body.data
                val updatedNumber = number.copy(
                    totalPoints = data.totalLoyaltyPoints?.toIntOrNull() ?: number.totalPoints,
                    balance = data.balance ?: number.balance,
                    lastUpdate = System.currentTimeMillis()
                )
                dao.updateNumber(updatedNumber)
                Result.success(data)
            } else {
                Result.failure(Exception(body?.responseMessage ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimPoints(number: SudaniNumber): Result<Int> {
        val userData = gson.fromJson(number.userDataJson, Map::class.java) as Map<String, Any>
        val currentPoints = number.totalPoints.toString()
        val headers = getAuthHeaders(number.msisdn, number.token, userData, currentPoints)
        val payload = mapOf(
            "Current-loyalty-points" to currentPoints,
            "milestone" to "NO",
            "milestoneIdentifier" to "1"
        )
        
        return try {
            val response = api.claimReward(headers, payload)
            if (response.isSuccessful && response.body()?.responseCode == "200") {
                // Refresh to get new points
                refreshDashboard(number)
                Result.success(10) // Usually 10 points
            } else {
                Result.failure(Exception(response.body()?.responseMessage ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
