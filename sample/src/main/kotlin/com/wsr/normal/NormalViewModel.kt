package com.wsr.normal

import androidx.lifecycle.ViewModel
import annotation.HVMWithGenerator
import javax.inject.Inject

@HVMWithGenerator
internal class NormalViewModel @Inject constructor() : ViewModel() {
    val text = "Normal"
}
