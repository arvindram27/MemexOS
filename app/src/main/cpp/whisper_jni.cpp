#include <jni.h>
#include <string>
#include <android/log.h>
#include <vector>
#include <sstream>
#include "whisper/whisper.h"

#define LOG_TAG "WhisperJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// Helper function to convert jstring to std::string
std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr) return "";
    
    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));
    
    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);
    
    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);
    
    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

// Read audio file (WAV format expected)
std::vector<float> read_wav(const std::string& fname) {
    std::vector<float> pcmf32;
    
    FILE* file = fopen(fname.c_str(), "rb");
    if (!file) {
        LOGE("Failed to open audio file: %s", fname.c_str());
        return pcmf32;
    }
    
    // Skip WAV header (44 bytes for standard WAV)
    fseek(file, 44, SEEK_SET);
    
    // Read PCM data
    int16_t sample;
    while (fread(&sample, sizeof(int16_t), 1, file) == 1) {
        pcmf32.push_back(sample / 32768.0f);
    }
    
    fclose(file);
    LOGI("Read %zu samples from %s", pcmf32.size(), fname.c_str());
    return pcmf32;
}

JNIEXPORT jstring JNICALL
Java_com_example_memexos_WhisperWrapper_nativeTranscribe(
        JNIEnv *env,
        jobject /* this */,
        jstring audioPath,
        jstring modelPath) {
    
    // Convert Java strings to C++ strings
    std::string audio_path = jstring2string(env, audioPath);
    std::string model_path = jstring2string(env, modelPath);
    
    LOGI("Starting transcription - Audio: %s, Model: %s", audio_path.c_str(), model_path.c_str());
    
    // Initialize whisper context with model
    struct whisper_context_params cparams = whisper_context_default_params();
    struct whisper_context * ctx = whisper_init_from_file_with_params(model_path.c_str(), cparams);
    
    if (ctx == nullptr) {
        LOGE("Failed to load model from: %s", model_path.c_str());
        return env->NewStringUTF("Error: Failed to load model");
    }
    
    // Read audio file
    std::vector<float> pcmf32 = read_wav(audio_path);
    if (pcmf32.empty()) {
        whisper_free(ctx);
        return env->NewStringUTF("Error: Failed to read audio file");
    }
    
    // Whisper parameters
    whisper_full_params wparams = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    
    // Configure parameters for better accuracy
    wparams.print_progress   = false;
    wparams.print_special    = false;
    wparams.print_realtime   = false;
    wparams.print_timestamps = false;
    wparams.translate        = false;
    wparams.language         = "en";
    wparams.n_threads        = 4;  // Adjust based on device capabilities
    wparams.offset_ms        = 0;
    wparams.duration_ms      = 0;
    wparams.single_segment   = false;
    wparams.max_tokens       = 0;
    wparams.speed_up         = false;
    wparams.audio_ctx        = 0;
    
    // Process audio
    LOGI("Processing %zu samples...", pcmf32.size());
    if (whisper_full(ctx, wparams, pcmf32.data(), pcmf32.size()) != 0) {
        LOGE("Failed to process audio");
        whisper_free(ctx);
        return env->NewStringUTF("Error: Failed to process audio");
    }
    
    // Get results
    const int n_segments = whisper_full_n_segments(ctx);
    LOGI("Found %d segments", n_segments);
    
    std::stringstream result;
    for (int i = 0; i < n_segments; ++i) {
        const char * text = whisper_full_get_segment_text(ctx, i);
        result << text;
        if (i < n_segments - 1) {
            result << " ";
        }
    }
    
    // Clean up
    whisper_free(ctx);
    
    std::string transcription = result.str();
    LOGI("Transcription complete: %s", transcription.c_str());
    
    return env->NewStringUTF(transcription.c_str());
}

// Initialize function - can be used for one-time setup
JNIEXPORT void JNICALL
Java_com_example_memexos_WhisperWrapper_nativeInit(
        JNIEnv *env,
        jobject /* this */) {
    LOGI("WhisperJNI initialized");
}

// Cleanup function
JNIEXPORT void JNICALL
Java_com_example_memexos_WhisperWrapper_nativeCleanup(
        JNIEnv *env,
        jobject /* this */) {
    LOGI("WhisperJNI cleanup");
}

} // extern "C"
