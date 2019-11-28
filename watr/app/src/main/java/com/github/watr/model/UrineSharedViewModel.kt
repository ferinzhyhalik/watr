package com.github.watr.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UrineSharedViewModel : ViewModel() {
    val selected = MutableLiveData<Int>()

    fun selectedItem(item: Int) {
        selected.value = item
    }
}