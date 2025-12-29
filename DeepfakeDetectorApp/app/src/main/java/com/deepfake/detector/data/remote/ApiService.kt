package com.deepfake.detector.data.remote

import com.deepfake.detector.data.model.GradioResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    
    // Step 1: Upload file to Gradio
    // Returns array of file paths: ["/tmp/gradio/xxx/file.jpg"]
    @Multipart
    @POST("gradio_api/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<List<String>>
    
    // Step 2: Gradio 5 API endpoints for prediction (queue-based)
    // These return an event_id that needs to be polled
    @POST("gradio_api/call/predict")
    suspend fun predictImage(
        @Body requestBody: RequestBody
    ): Response<GradioResponse>
    
    @POST("gradio_api/call/predict_1")
    suspend fun predictVideo(
        @Body requestBody: RequestBody
    ): Response<GradioResponse>
    
    @POST("gradio_api/call/predict_2")
    suspend fun predictAudio(
        @Body requestBody: RequestBody
    ): Response<GradioResponse>
    
    // Step 3: Poll for results using event_id
    // These return Server-Sent Events (SSE), not JSON
    @GET("gradio_api/call/predict/{event_id}")
    suspend fun pollImageResult(
        @Path("event_id") eventId: String
    ): Response<okhttp3.ResponseBody>
    
    @GET("gradio_api/call/predict_1/{event_id}")
    suspend fun pollVideoResult(
        @Path("event_id") eventId: String
    ): Response<okhttp3.ResponseBody>
    
    @GET("gradio_api/call/predict_2/{event_id}")
    suspend fun pollAudioResult(
        @Path("event_id") eventId: String
    ): Response<okhttp3.ResponseBody>
    
    companion object {
        const val BASE_URL = "https://praneshjs-fakevideodetect.hf.space/"
        // Alternative local backend: "http://10.0.2.2:5000/" for emulator
        // or "http://localhost:5000/" for physical device on same network
    }
}
