package com.deepfake.detector.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepfake.detector.data.model.AnalysisResult
import com.deepfake.detector.data.model.MediaType
import com.deepfake.detector.domain.usecase.AnalyzeMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val analyzeMediaUseCase: AnalyzeMediaUseCase
) : ViewModel() {
    
    private val _imageResult = MutableStateFlow<AnalysisResult>(AnalysisResult.Idle)
    val imageResult: StateFlow<AnalysisResult> = _imageResult.asStateFlow()
    
    private val _videoResult = MutableStateFlow<AnalysisResult>(AnalysisResult.Idle)
    val videoResult: StateFlow<AnalysisResult> = _videoResult.asStateFlow()
    
    private val _audioResult = MutableStateFlow<AnalysisResult>(AnalysisResult.Idle)
    val audioResult: StateFlow<AnalysisResult> = _audioResult.asStateFlow()
    
    fun analyzeImage(uri: Uri) {
        analyzeMedia(uri, MediaType.IMAGE, _imageResult)
    }
    
    fun analyzeVideo(uri: Uri) {
        analyzeMedia(uri, MediaType.VIDEO, _videoResult)
    }
    
    fun analyzeAudio(uri: Uri) {
        analyzeMedia(uri, MediaType.AUDIO, _audioResult)
    }
    
    private fun analyzeMedia(
        uri: Uri,
        mediaType: MediaType,
        resultFlow: MutableStateFlow<AnalysisResult>
    ) {
        viewModelScope.launch {
            analyzeMediaUseCase(uri, mediaType).collect { result ->
                resultFlow.value = result
            }
        }
    }
    
    fun resetImageResult() {
        _imageResult.value = AnalysisResult.Idle
    }
    
    fun resetVideoResult() {
        _videoResult.value = AnalysisResult.Idle
    }
    
    fun resetAudioResult() {
        _audioResult.value = AnalysisResult.Idle
    }
}
