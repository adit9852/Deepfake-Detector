# AI-Generated Media Detector

<div align="center">

**A powerful open-source deepfake detection system for images, videos, and audio**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Python 3.10+](https://img.shields.io/badge/python-3.10+-blue.svg)](https://www.python.org/downloads/)
[![PyTorch](https://img.shields.io/badge/PyTorch-Latest-red.svg)](https://pytorch.org/)

[Features](#features) • [Demo](#demo) • [Installation](#installation) • [Usage](#usage) • [API](#api-reference) • [Architecture](#how-it-works)

<br/>

### 📹 Watch Demo Video

https://github.com/adit9852/Deepfake-Detector/assets/Deepfake.mp4

*See the deepfake detector in action with our Android mobile app*

<br/>

### 📱 Try the Android App Live

<a href="https://appetize.io/app/b_c275cxdxhlujzoxna3gtemlwfe">
  <img src="https://img.shields.io/badge/Try%20Live%20Demo-Android%20App-success?style=for-the-badge&logo=android" alt="Try Live Demo">
</a>

*Experience the Android app directly in your browser - No installation required!*

</div>

---

## Overview

This advanced deepfake detection system combines state-of-the-art deep learning models to identify AI-generated or manipulated media across multiple formats. Built with EfficientNetV2 and RawNet architectures, it provides high-accuracy detection through frame-by-frame video analysis, spectral audio analysis, and sophisticated image processing.

**Perfect for:**
- Security analysts and digital forensics teams
- Media verification and fact-checking organizations
- Researchers studying deepfake detection
- Developers building content moderation systems

---

## Features

### Core Capabilities
- **Multi-Modal Detection**: Analyze images, videos, and audio files through a unified platform
- **High Accuracy**: Leverages EfficientNetV2 for visual media and RawNet for audio analysis
- **Real-Time Processing**: Frame-by-frame video analysis with automatic face detection using MTCNN
- **Confidence Scoring**: Detailed probability scores showing detection confidence
- **Multiple Interfaces**:
  - Web interface (Gradio)
  - REST API backend (Flask)
  - Android mobile app (Jetpack Compose)

### Technical Features
- Frame-by-frame video analysis with face tracking
- Spectral analysis for audio deepfake detection
- Batch processing support
- ONNX model optimization for faster inference
- Cross-platform compatibility

---

## Demo

### 🌐 Web Demo
Try the detector online: [https://praneshjs-fakevideodetect.hf.space/](https://praneshjs-fakevideodetect.hf.space/)

### 📱 Android App Demo
Experience the mobile app in your browser: [https://appetize.io/app/b_c275cxdxhlujzoxna3gtemlwfe](https://appetize.io/app/b_c275cxdxhlujzoxna3gtemlwfe)

### Test Data
The repository includes sample files for testing:
- **Images**: `images/lady.jpg`, `images/fake_image.jpg`
- **Videos**: `videos/aaa.mp4`, `videos/bbb.mp4`
- **Audio**: `audios/DF_E_2000027.flac`, `audios/DF_E_2000031.flac`

---

## How It Works

### Detection Pipeline

#### Image & Video Analysis
```
Input → Face Detection (MTCNN) → Preprocessing → EfficientNetV2 Model → Confidence Score → Result
```

1. **Face Detection**: [MTCNN](https://mtcnn.readthedocs.io/en/latest/) detects and extracts faces from each frame
2. **Preprocessing**: Images are normalized and resized to 224×224 pixels
3. **Classification**: [EfficientNetV2](https://arxiv.org/abs/2104.00298) analyzes facial features and patterns
4. **Scoring**: Outputs confidence percentages for real vs. fake classification

#### Audio Analysis
```
Input → Spectral Analysis → RawNet Model → Confidence Score → Result
```

1. **Audio Processing**: Extracts raw waveform features
2. **Spectral Analysis**: Analyzes frequency patterns and voice characteristics
3. **Classification**: RawNet model detects synthesis artifacts
4. **Scoring**: Provides authenticity confidence score

### Model Architecture

- **Visual Detection**: EfficientNetV2 (converted from ONNX)
- **Audio Detection**: RawNet with GRU layers
- **Multimodal Fusion**: ETMC (Enhanced Temporal Multi-modal Correlation) for combined analysis

---

## Installation

### Prerequisites

- **Python 3.10+**
- **Git**
- **pip** (Python package manager)

### Quick Start

```bash
# Clone the repository
git clone https://github.com/adit9852/Deepfake-Detector.git
cd Deepfake-Detector

# Install dependencies
pip install -r requirements.txt

# Download model weights (place in checkpoints/ folder)
# - model.pth
# - efficientnet.onnx
```

### Dependencies

```bash
# Core ML frameworks
torch torchvision torchaudio
onnx onnx2pytorch

# Computer vision
opencv-python
facenet-pytorch

# API and web interface
flask flask-cors
gradio
gradio-client

# Utilities
numpy
werkzeug
```

---

## Usage

### 1. Web Interface (Gradio)

Launch the interactive web interface:

```bash
python app.py
```

Then open your browser to `http://localhost:7860`

### 2. REST API Backend

Start the Flask API server:

```bash
python gradioapibackend.py
```

The API will be available at `http://localhost:5000`

#### API Endpoints

**Analyze Image**
```bash
curl -X POST http://localhost:5000/api/predict/image \
  -F "file=@path/to/image.jpg"
```

**Analyze Video**
```bash
curl -X POST http://localhost:5000/api/predict/video \
  -F "file=@path/to/video.mp4"
```

**Analyze Audio**
```bash
curl -X POST http://localhost:5000/api/predict/audio \
  -F "file=@path/to/audio.wav"
```

**Response Format**
```json
{
  "result": "The image is FAKE. Confidence score is: 87.3%",
  "status": "success",
  "source": "huggingface_space",
  "file_info": {
    "name": "test.jpg",
    "size_mb": 2.4
  }
}
```

### 3. Android Mobile App

The Android app is located in `DeepfakeDetectorApp/`:

```bash
cd DeepfakeDetectorApp
./gradlew assembleDebug
```

Features:
- Material Design 3 UI with Jetpack Compose
- Image, video, and audio analysis
- Direct integration with API backend
- Browser fallback option

### 4. Python Integration

```python
from inference_2 import deepfakes_image_predict, deepfakes_video_predict, deepfakes_spec_predict
import cv2

# Analyze an image
image = cv2.imread("path/to/image.jpg")
result = deepfakes_image_predict(image)
print(result)

# Analyze a video
result = deepfakes_video_predict("path/to/video.mp4")
print(result)
```

---

## Project Structure

```
AI-Generated-Video-Detector/
├── app.py                      # Gradio web interface
├── gradioapibackend.py         # Flask REST API backend
├── inference_2.py              # Core inference models
├── models/
│   ├── TMC.py                  # ETMC multimodal model
│   └── image.py                # RawNet audio model
├── checkpoints/
│   ├── model.pth               # Trained model weights
│   └── efficientnet.onnx       # ONNX model
├── DeepfakeDetectorApp/        # Android mobile app
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/deepfake/detector/
│   │       │   ├── ui/screen/  # Compose UI screens
│   │       │   ├── ui/viewmodel/
│   │       │   ├── data/       # Repository & API
│   │       │   └── di/         # Dependency injection
│   │       └── res/            # Android resources
│   └── build.gradle.kts
├── images/                     # Sample images
├── videos/                     # Sample videos
├── audios/                     # Sample audio files
└── requirements.txt
```

---

## Technologies Used

### Machine Learning & AI
- **PyTorch**: Deep learning framework
- **ONNX**: Model optimization and conversion
- **EfficientNetV2**: State-of-the-art image classification
- **RawNet**: Audio deepfake detection
- **MTCNN**: Multi-task Cascaded Convolutional Networks for face detection

### Backend & API
- **Flask**: RESTful API server
- **Flask-CORS**: Cross-origin resource sharing
- **Gradio**: Interactive web interface
- **Werkzeug**: WSGI utilities

### Computer Vision & Processing
- **OpenCV**: Video and image processing
- **NumPy**: Numerical computations
- **facenet-pytorch**: Face detection and recognition

### Mobile Development
- **Kotlin**: Android app development
- **Jetpack Compose**: Modern Android UI toolkit
- **Hilt**: Dependency injection
- **Retrofit**: HTTP client
- **Coroutines**: Asynchronous programming
- **Material Design 3**: Modern UI components

---

## API Reference

### Supported File Formats

| Media Type | Supported Formats |
|-----------|------------------|
| **Images** | JPG, JPEG, PNG, GIF, BMP, WEBP |
| **Videos** | MP4, AVI, MOV, MKV, WEBM, FLV |
| **Audio** | WAV, MP3, FLAC, OGG, AAC, M4A |

### Configuration

**Maximum File Size**: 100 MB
**Default Port (API)**: 5000
**Default Port (Gradio)**: 7860

---

## Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Areas for Contribution
- Improving detection accuracy
- Adding new model architectures
- Optimizing inference speed
- Expanding supported file formats
- Enhancing UI/UX
- Writing documentation and tutorials

---

## Roadmap

- [ ] Real-time webcam detection
- [ ] Batch processing for multiple files
- [ ] Explainable AI visualizations
- [ ] Model fine-tuning interface
- [ ] Docker containerization
- [ ] iOS mobile app
- [ ] Browser extension

---

## Citation

If you use this project in your research or work, please cite:

```bibtex
@software{deepfake_detector_2024,
  author = {Deepfake Detector Contributors},
  title = {AI-Generated Media Detector},
  year = {2024},
  url = {https://github.com/adit9852/Deepfake-Detector}
}
```

---

## License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### Third-Party Licenses
- EfficientNetV2: Apache 2.0
- PyTorch: BSD License
- Gradio: Apache 2.0

---

## Acknowledgments

- [EfficientNetV2](https://arxiv.org/abs/2104.00298) by Google Research
- [MTCNN](https://arxiv.org/abs/1604.02878) for face detection
- [RawNet](https://arxiv.org/abs/1904.08104) for audio deepfake detection
- Hugging Face for hosting the demo space

---

## Disclaimer

This project is developed for **research and educational purposes only**.

**Important Notes:**
- Detection results should not be used as the sole basis for critical decisions
- The system may produce false positives or false negatives
- Always verify results with additional methods
- Deepfake technology evolves rapidly; models require regular updates
- Use responsibly and ethically

**For researchers**: Please provide proper attribution when using this work.

---

## Contact & Support

- **Issues**: [GitHub Issues](https://github.com/adit9852/Deepfake-Detector/issues)
- **Discussions**: [GitHub Discussions](https://github.com/adit9852/Deepfake-Detector/discussions)
- **Live Demo**: [Hugging Face Space](https://praneshjs-fakevideodetect.hf.space/)

---

<div align="center">

**Made with ❤️ for a safer digital world**

[⬆ Back to Top](#ai-generated-media-detector)

</div>
