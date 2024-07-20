package com.wsr.assisted

import androidx.lifecycle.ViewModel
import annotation.HVMGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HVMGenerator
class AssistedViewModel @AssistedInject constructor(
    @Assisted val text: String,
    @Assisted("foo") val num: Int,
) : ViewModel()
