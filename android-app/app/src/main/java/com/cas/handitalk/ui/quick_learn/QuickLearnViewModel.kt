package com.cas.handitalk.ui.quick_learn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuickLearnViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is quick learn Fragment"
    }
    val text: LiveData<String> = _text
}