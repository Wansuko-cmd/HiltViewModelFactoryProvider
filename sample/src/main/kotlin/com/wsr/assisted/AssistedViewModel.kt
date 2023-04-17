package com.wsr.assisted

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AssistedViewModel @AssistedInject constructor(
    @Assisted("text") val text: String,
) : ViewModel()
