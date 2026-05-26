package com.example.smartulcerpredictor.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String
)

data class AuthResponse(
    val status: String,
    val message: String,
    val user: User?
)

data class ForgotPasswordResponse(
    val status: String,
    val message: String,
    val reset_code: String?
)

data class ResetPasswordResponse(
    val status: String,
    val message: String
)

data class UploadData(
    val id: Int,
    val result: String,
    val confidence: Float,
    val image_url: String,
    val created_at: String
)

data class UploadResponse(
    val status: String,
    val message: String,
    val data: UploadData?
)

data class HistoryNetworkItem(
    val id: String,
    val result: String,
    val confidence: Float,
    val image_path: String,
    val date: String
)

data class HistoryListResponse(
    val status: String,
    val history: List<HistoryNetworkItem>?
)
