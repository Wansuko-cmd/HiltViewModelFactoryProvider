package com.wsr.assisted

import androidx.lifecycle.ViewModel
import annotation.AnnotateViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@AnnotateViewModelFactory
class AssistedViewModel @AssistedInject constructor(
    @Assisted("text") val text: String,
    @Assisted("num") val num: Int,
) : ViewModel()
