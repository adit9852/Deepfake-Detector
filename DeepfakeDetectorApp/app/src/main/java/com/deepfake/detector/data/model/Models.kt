package com.deepfake.detector.data.model

import com.google.gson.annotations.SerializedName

// Response from Gradio upload endpoint
data class GradioUploadResponse(
    @SerializedName("path")
    val path: String,
    
    @SerializedName("url")
    val url: String? = null,
    
    @SerializedName("orig_name")
    val origName: String? = null,
    
    @SerializedName("size")
    val size: Long? = null
)

// Request body for Gradio prediction
data class GradioRequest(
    @SerializedName("data")
    val data: List<Any>
)

// Gradio 5 wraps responses in a data array
// Using Any because Gradio can return different types (strings, objects, etc.)
data class GradioResponse(
    @SerializedName("data")
    val data: List<Any>? = null,
    
    @SerializedName("event_id")
    val eventId: String? = null,  // For queue-based calls
    
    @SerializedName("duration")
    val duration: Double? = null,
    
    @SerializedName("average_duration")
    val averageDuration: Double? = null
)

data class PredictionResponse(
    @SerializedName("result")
    val result: String,
    
    @SerializedName("status")
    val status: String = "success",
    
    @SerializedName("source")
    val source: String = "AI Model",
    
    @SerializedName("file_info")
    val fileInfo: FileInfo? = null,
    
    @SerializedName("note")
    val note: String? = null
)

data class FileInfo(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("size")
    val size: Long,
    
    @SerializedName("size_mb")
    val sizeMb: Double,
    
    @SerializedName("size_kb")
    val sizeKb: Double
)

sealed class AnalysisResult {
    data class Success(val response: PredictionResponse) : AnalysisResult()
    data class Error(val message: String) : AnalysisResult()
    object Loading : AnalysisResult()
    object Idle : AnalysisResult()
}

enum class MediaType(val endpoint: String) {
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio")
}
