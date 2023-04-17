package com.wsr.normal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NormalViewModel @Inject constructor() : ViewModel() {
    val text = "Normal"
}
