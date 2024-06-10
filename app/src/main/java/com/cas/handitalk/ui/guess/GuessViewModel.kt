package com.cas.handitalk.ui.guess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuessViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is guess Fragment"
    }
    val text: LiveData<String> = _text
}