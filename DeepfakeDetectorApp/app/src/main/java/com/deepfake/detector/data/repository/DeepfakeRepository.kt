package com.deepfake.detector.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.deepfake.detector.data.model.AnalysisResult
import com.deepfake.detector.data.model.GradioRequest
import com.deepfake.detector.data.model.GradioResponse
import com.deepfake.detector.data.model.GradioUploadResponse
import com.deepfake.detector.data.model.MediaType
import com.deepfake.detector.data.model.PredictionResponse
import com.deepfake.detector.data.remote.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepfakeRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val TAG = "DeepfakeRepository"
    }
    
    fun analyzeMedia(uri: Uri, mediaType: MediaType): Flow<AnalysisResult> = flow {
        try {
            Log.d(TAG, "Starting analysis for $mediaType")
            Log.d(TAG, "URI: $uri")
            emit(AnalysisResult.Loading)
            
            // Convert URI to File
            Log.d(TAG, "Converting URI to file...")
            val file = uriToFile(uri) ?: throw Exception("Failed to read file from URI")
            Log.d(TAG, "File created: ${file.name}, Size: ${file.length()} bytes")
            
            // Validate file size
            if (file.length() == 0L) {
                file.delete()
                throw Exception("File is empty")
            }
            
            // Create multipart body for upload
            val mimeType = when (mediaType) {
                MediaType.IMAGE -> "image/*"
                MediaType.VIDEO -> "video/*"
                MediaType.AUDIO -> "audio/*"
            }
            
            Log.d(TAG, "Creating multipart request for upload with mime type: $mimeType")
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "files",  // Gradio expects "files" as the field name
                file.name,
                requestBody
            )
            
            // Step 1: Upload file to Gradio
            Log.d(TAG, "Step 1: Uploading file to /gradio_api/upload")
            val uploadResponse = try {
                apiService.uploadFile(multipartBody)
            } catch (e: Exception) {
                file.delete()
                Log.e(TAG, "Upload request failed", e)
                emit(AnalysisResult.Error("Upload failed: ${e.message}"))
                return@flow
            }
            
            Log.d(TAG, "Upload response - Code: ${uploadResponse.code()}, Message: ${uploadResponse.message()}")
            
            if (!uploadResponse.isSuccessful) {
                file.delete()
                val errorBody = uploadResponse.errorBody()?.string()
                Log.e(TAG, "Upload error body: $errorBody")
                val errorMessage = "Upload failed (${uploadResponse.code()}): ${uploadResponse.message()}" +
                        if (errorBody != null) "\nDetails: $errorBody" else ""
                Log.e(TAG, errorMessage)
                emit(AnalysisResult.Error(errorMessage))
                return@flow
            }
            
            // Try to parse upload response (array of file paths)
            val uploadedFiles = try {
                uploadResponse.body()
            } catch (e: Exception) {
                file.delete()
                Log.e(TAG, "Failed to parse upload response", e)
                emit(AnalysisResult.Error("Upload response parsing failed: ${e.message}"))
                return@flow
            }
            
            if (uploadedFiles == null || uploadedFiles.isEmpty()) {
                file.delete()
                throw Exception("Upload succeeded but no file path returned")
            }
            
            // Gradio returns array of strings: ["/tmp/gradio/xxx/file.jpg"]
            val uploadedFilePath = uploadedFiles[0]
            Log.d(TAG, "File uploaded successfully. Server path: $uploadedFilePath")
            
            // Step 2: Create JSON request body with uploaded file path
            val gradioRequest = GradioRequest(
                data = listOf(uploadedFilePath)
            )
            
            val gson = com.google.gson.Gson()
            val jsonBody = gson.toJson(gradioRequest)
            Log.d(TAG, "Step 2: Sending prediction request with JSON body: $jsonBody")
            
            val requestBodyJson = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
            
            // Make API call based on media type
            Log.d(TAG, "Making prediction API call for $mediaType")
            
            val response = when (mediaType) {
                MediaType.IMAGE -> {
                    Log.d(TAG, "Calling /gradio_api/call/predict (image)")
                    apiService.predictImage(requestBodyJson)
                }
                MediaType.VIDEO -> {
                    Log.d(TAG, "Calling /gradio_api/call/predict_1 (video)")
                    apiService.predictVideo(requestBodyJson)
                }
                MediaType.AUDIO -> {
                    Log.d(TAG, "Calling /gradio_api/call/predict_2 (audio)")
                    apiService.predictAudio(requestBodyJson)
                }
            }
            
            Log.d(TAG, "Response received - Code: ${response.code()}, Message: ${response.message()}")
            
            // Clean up temporary file
            file.delete()
            Log.d(TAG, "Temporary file deleted")
            
            if (response.isSuccessful && response.body() != null) {
                val gradioResponse = response.body()!!
                Log.d(TAG, "Gradio response data: ${gradioResponse.data}")
                Log.d(TAG, "Gradio response event_id: ${gradioResponse.eventId}")
                
                // Check if this is a queue-based response with event_id
                if (gradioResponse.eventId != null) {
                    Log.d(TAG, "Queue-based processing detected, polling for results...")
                    
                    // Poll for results
                    val eventId = gradioResponse.eventId
                    var pollAttempts = 0
                    val maxAttempts = 20 // Poll for up to 40 seconds (20 * 2 seconds)
                    
                    while (pollAttempts < maxAttempts) {
                        pollAttempts++
                        Log.d(TAG, "Polling attempt $pollAttempts/$maxAttempts for event_id: $eventId")
                        
                        // Wait before polling
                        kotlinx.coroutines.delay(2000) // 2 seconds
                        
                        // Poll based on media type
                        val pollResponse = try {
                            when (mediaType) {
                                MediaType.IMAGE -> apiService.pollImageResult(eventId)
                                MediaType.VIDEO -> apiService.pollVideoResult(eventId)
                                MediaType.AUDIO -> apiService.pollAudioResult(eventId)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Poll request failed", e)
                            continue // Try next poll
                        }
                        
                        if (pollResponse.isSuccessful && pollResponse.body() != null) {
                            val responseText = pollResponse.body()!!.string()
                            Log.d(TAG, "Poll response (raw): $responseText")
                            
                            // Parse Server-Sent Events format
                            // Format: "event: complete\ndata: {json}\n\n"
                            try {
                                // Look for "event: complete" which indicates result is ready
                                if (responseText.contains("event: complete")) {
                                    // Extract JSON from SSE format
                                    val dataLine = responseText.lines().find { it.startsWith("data:") }
                                    if (dataLine != null) {
                                        val jsonData = dataLine.substring(5).trim() // Remove "data:" prefix
                                        Log.d(TAG, "Extracted JSON: $jsonData")
                                        
                                        // Parse the JSON
                                        val gson = com.google.gson.Gson()
                                        val pollData = gson.fromJson(jsonData, GradioResponse::class.java)
                                        
                                        // Check if we have results
                                        if (pollData.data != null && pollData.data.isNotEmpty()) {
                                            val resultData = pollData.data.firstOrNull()
                                            if (resultData != null) {
                                                val resultText = resultData.toString()
                                                Log.d(TAG, "Polling successful! Result: $resultText")
                                                
                                                val predictionResponse = PredictionResponse(
                                                    result = resultText,
                                                    source = "Gradio AI Model"
                                                )
                                                emit(AnalysisResult.Success(predictionResponse))
                                                return@flow
                                            }
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Still processing... (event not complete)")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing poll response", e)
                                // Continue polling
                            }
                        } else {
                            Log.e(TAG, "Poll response failed: ${pollResponse.code()}")
                        }
                    }
                    
                    // If we get here, polling timed out
                    Log.e(TAG, "Polling timed out after $maxAttempts attempts")
                    emit(AnalysisResult.Error(
                        "Analysis timed out after ${maxAttempts * 2} seconds. " +
                        "The server is busy. Please try the 'Open in Browser' button for faster results."
                    ))
                    return@flow
                }
                
                // Direct response (no event_id)
                val resultData = gradioResponse.data?.firstOrNull()
                
                if (resultData != null) {
                    val resultText = resultData.toString()
                    Log.d(TAG, "Extracted result: $resultText")
                    
                    val predictionResponse = PredictionResponse(
                        result = resultText,
                        source = "Gradio AI Model"
                    )
                    Log.d(TAG, "Success! Result: ${predictionResponse.result}")
                    emit(AnalysisResult.Success(predictionResponse))
                } else {
                    val errorMessage = "Invalid response format: data array is empty or null"
                    Log.e(TAG, errorMessage)
                    emit(AnalysisResult.Error(errorMessage))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "API Error (${response.code()}): ${response.message()}" +
                        if (errorBody != null) "\nDetails: $errorBody" else ""
                Log.e(TAG, errorMessage)
                emit(AnalysisResult.Error(errorMessage))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception during analysis", e)
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> 
                    "Network error: Cannot connect to server. Please check your internet connection."
                e.message?.contains("timeout") == true -> 
                    "Request timeout: Server is taking too long to respond."
                e.message?.contains("Failed to read file") == true -> 
                    "File error: Cannot read the selected file."
                else -> "Error: ${e.message ?: "Unknown error occurred"}"
            }
            emit(AnalysisResult.Error(errorMessage))
        }
    }
    
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "temp_${System.currentTimeMillis()}.${getFileExtension(uri)}"
            val file = File(context.cacheDir, fileName)
            
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun getFileExtension(uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return when {
            mimeType?.startsWith("image/") == true -> mimeType.substringAfter("image/")
            mimeType?.startsWith("video/") == true -> mimeType.substringAfter("video/")
            mimeType?.startsWith("audio/") == true -> mimeType.substringAfter("audio/")
            else -> "tmp"
        }
    }
}
