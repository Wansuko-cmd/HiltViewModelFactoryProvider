package com.wsr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import com.wsr.assisted.assistedViewModel
import com.wsr.normal.normalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val normalViewModel = normalViewModel()
            val assistedViewModel = assistedViewModel(text = "Assisted")

            Column {
                Text(normalViewModel.text)
                Text(assistedViewModel.text)
            }
        }
    }
}
