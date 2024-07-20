package com.wsr.normal

import androidx.lifecycle.ViewModel
import annotation.AnnotateViewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AnnotateViewModelFactory
internal class NormalViewModel @Inject constructor() : ViewModel() {
    val text = "Normal"
}
