package com.google.mediapipe.examples.gesturerecognizer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to \nHandiTalk!\n🖐️\n\n\n\n\nLet AI be your hands in\nlearning sign language!"
    }
    val text: LiveData<String> = _text
}