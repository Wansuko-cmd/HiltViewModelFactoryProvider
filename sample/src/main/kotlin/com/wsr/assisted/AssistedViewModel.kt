package com.wsr.assisted

import androidx.lifecycle.ViewModel
import annotation.AssistedHiltViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@AssistedHiltViewModel
class AssistedViewModel @AssistedInject constructor(
    @Assisted("text") val text: String,
) : ViewModel()
