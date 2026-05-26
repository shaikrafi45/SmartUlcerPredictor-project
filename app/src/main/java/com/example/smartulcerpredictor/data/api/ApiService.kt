package com.example.smartulcerpredictor.data.api

import com.example.smartulcerpredictor.data.model.AuthResponse
import com.example.smartulcerpredictor.data.model.ForgotPasswordResponse
import com.example.smartulcerpredictor.data.model.HistoryListResponse
import com.example.smartulcerpredictor.data.model.ResetPasswordResponse
import com.example.smartulcerpredictor.data.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register.php")
    suspend fun register(
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @POST("login.php")
    suspend fun login(
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @POST("forgot_password.php")
    suspend fun forgotPassword(
        @Body body: Map<String, String>
    ): Response<ForgotPasswordResponse>

    @POST("reset_password.php")
    suspend fun resetPassword(
        @Body body: Map<String, String>
    ): Response<ResetPasswordResponse>

    @Multipart
    @POST("upload_image.php")
    suspend fun uploadImage(
        @Part("user_id") userId: RequestBody,
        @Part("result") result: RequestBody,
        @Part("confidence") confidence: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("get_history.php")
    suspend fun getHistory(
        @Query("user_id") userId: Int
    ): Response<HistoryListResponse>
}
