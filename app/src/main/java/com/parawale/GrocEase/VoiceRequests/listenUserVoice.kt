package com.parawale.GrocEase.VoiceRequests

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale


fun startListening(context: Context, onResult: (String) -> Unit) {
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            onResult("error")
        }

        override fun onResults(results: Bundle?) {
            val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                ?: "error"
            onResult(result)
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    speechRecognizer.startListening(intent)
}

// Stop listening for voice input
fun stopListening(speechRecognizer: SpeechRecognizer) {
    speechRecognizer.stopListening()
    speechRecognizer.destroy()
}

// Check and request audio recording permission
