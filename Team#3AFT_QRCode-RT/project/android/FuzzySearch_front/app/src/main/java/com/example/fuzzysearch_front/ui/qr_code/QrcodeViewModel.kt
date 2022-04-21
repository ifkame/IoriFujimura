package com.example.fuzzysearch_front.ui.qr_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrcodeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}