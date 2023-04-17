package com.wsr.assisted

import androidx.lifecycle.ViewModel
import annotation.AnnotateProvideFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@AnnotateProvideFactory
class AssistedViewModel @AssistedInject constructor(
    @Assisted("text") val text: String,
) : ViewModel()
