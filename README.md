# HiltViewModelFactoryProvider

Composeで使える、@Assistedに対応したViewModelFactoryを作成してくれるライブラリです。
引数を追加・削除してもFactoryクラス群を作る必要性がなくなり、コンパイル時エラーによる早期の修正漏れ発見にも期待できます。

`SampleViewModel.kt`
```kotlin
@HiltViewModel
@AnnotateViewModelFactory
class NormalViewModel @Inject constructor() : ViewModel() {
    val text = "Normal"
}


@AnnotateViewModelFactory
class AssistedViewModel @AssistedInject constructor(
    @Assisted("text") val text: String,
    @Assisted("foo") val boo: Int,
) : ViewModel()
```

`SampleActivity.kt`
```kotlin
@AndroidEntryPoint
class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val normalViewModel = normalViewModel() // 自動生成
            val assistedViewModel = assistedViewModel(text = "Assisted", boo = 2) // 自動生成

            Column {
                Text(normalViewModel.text)
                Text(assistedViewModel.text + assistedViewModel.boo)
            }
        }
    }
}
```

## 生成されるコード

`NormalViewModelFactoryProvider.kt`
```kotlin
@file:Suppress("NOTHING_TO_INLINE")

package view.model.package.place

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun normalViewModel(): NormalViewModel = viewModel()
```

`AssistedViewModelFactoryProvider.kt`
```kotlin
@file:Suppress("NOTHING_TO_INLINE")

package view.model.package.place

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@Composable
inline fun assistedViewModel(text: String, num: Int): AssistedViewModel {
    val assistedViewModelFactory = EntryPointAccessors.fromActivity(
        activity = LocalContext.current as Activity,
        entryPoint = AssistedViewModelFactoryProvider::class.java,
    ).assistedViewModelFactory()
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                assistedViewModelFactory.create(text, num) as T
        }
    )
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface AssistedViewModelFactoryProvider {
    fun assistedViewModelFactory(): AssistedViewModelAssistedFactory
}

@AssistedFactory
interface AssistedViewModelAssistedFactory {
    fun create(
        @Assisted("text") text: String, @Assisted("num") num: Int,
    ): AssistedViewModel
}

```
