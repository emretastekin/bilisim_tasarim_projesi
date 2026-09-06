package com.emretastekin.akillimutfakasistani.data.remote.api

import com.emretastekin.akillimutfakasistani.data.remote.dto.ReceiptResponseDto
import com.emretastekin.akillimutfakasistani.data.remote.dto.RecipeRequestDto
import com.emretastekin.akillimutfakasistani.data.remote.dto.RecipeResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BackendApiService {

    // Fiş fotoğrafını Python sunucusuna gönderdiğimiz POST isteği
    @Multipart
    @POST("/api/upload-receipt")
    suspend fun uploadReceipt(
        @Part file: MultipartBody.Part
    ): Response<ReceiptResponseDto>

    // Malzemeleri gönderip tarif istediğimiz POST isteği
    @POST("/api/recommend-recipe")
    suspend fun recommendRecipe(
        @Body request: RecipeRequestDto
    ): Response<RecipeResponseDto>
}