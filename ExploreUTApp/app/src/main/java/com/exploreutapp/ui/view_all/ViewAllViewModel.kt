package com.exploreutapp.ui.view_all

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewAllViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is view all Fragment"
    }
    val text: LiveData<String> = _text
}