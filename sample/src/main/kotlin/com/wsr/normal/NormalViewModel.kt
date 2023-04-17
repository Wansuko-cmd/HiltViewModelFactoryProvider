package com.wsr.normal

import androidx.lifecycle.ViewModel
import annotation.AnnotateProvideFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@AnnotateProvideFactory
class NormalViewModel @Inject constructor() : ViewModel() {
    val text = "Normal"
}
