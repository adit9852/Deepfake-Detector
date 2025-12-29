package com.deepfake.detector.domain.usecase

import android.net.Uri
import com.deepfake.detector.data.model.AnalysisResult
import com.deepfake.detector.data.model.MediaType
import com.deepfake.detector.data.repository.DeepfakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnalyzeMediaUseCase @Inject constructor(
    private val repository: DeepfakeRepository
) {
    
    operator fun invoke(uri: Uri, mediaType: MediaType): Flow<AnalysisResult> {
        return repository.analyzeMedia(uri, mediaType)
    }
    
    fun validateFileSize(sizeInBytes: Long, mediaType: MediaType): Boolean {
        val maxSizeInBytes = when (mediaType) {
            MediaType.IMAGE -> 10 * 1024 * 1024 // 10MB
            MediaType.VIDEO -> 100 * 1024 * 1024 // 100MB
            MediaType.AUDIO -> 50 * 1024 * 1024 // 50MB
        }
        return sizeInBytes <= maxSizeInBytes
    }
}
