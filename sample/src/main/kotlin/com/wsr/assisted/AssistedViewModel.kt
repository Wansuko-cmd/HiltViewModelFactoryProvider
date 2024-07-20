package com.wsr.assisted

import androidx.lifecycle.ViewModel
import annotation.HVMWithGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HVMWithGenerator
class AssistedViewModel @AssistedInject constructor(
    @Assisted val text: String,
    @Assisted("foo") val num: Int,
) : ViewModel()
