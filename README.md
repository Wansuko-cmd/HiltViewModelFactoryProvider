# HiltViewModelFactoryProvider

Composeで使える、@Assistedに対応したViewModelFactoryを作成してくれるライブラリです。
引数を追加・削除してもFactoryクラス群を作る必要性がなくなり、コンパイル時エラーによる早期の修正漏れ発見にも期待できます。

`SampleViewModel.kt`
```kotlin
@HVMGenerator
internal class NormalViewModel @Inject constructor() : ViewModel() {
    val text = "Normal"
}


@HVMGenerator
class AssistedViewModel @AssistedInject constructor(
    @Assisted val text: String,
    @Assisted("foo") val num: Int,
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
            val assistedViewModel = assistedViewModel(text = "Assisted", num = 2) // 自動生成

            Column {
                Text(normalViewModel.text)
                Text(assistedViewModel.text + assistedViewModel.num)
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

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@Composable
internal inline fun normalViewModel(): NormalViewModel {
    val viewModelFactory = EntryPointAccessors.fromActivity(
        activity = LocalContext.current as Activity,
        entryPoint = NormalViewModelFactoryProvider::class.java,
    ).viewModelFactory()
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                viewModelFactory.create() as T
        }
    )
}

@EntryPoint
@InstallIn(ActivityComponent::class)
internal interface NormalViewModelFactoryProvider {
    fun viewModelFactory(): NormalViewModelFactory
}

internal class NormalViewModelFactory @Inject constructor() {
    fun create(): NormalViewModel = NormalViewModel()
}

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
public inline fun assistedViewModel(text: kotlin.String, num: kotlin.Int): AssistedViewModel {
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
public interface AssistedViewModelFactoryProvider {
    fun assistedViewModelFactory(): AssistedViewModelAssistedFactory
}

@AssistedFactory
public interface AssistedViewModelAssistedFactory {
    fun create(
        @Assisted text: kotlin.String, @Assisted("foo") num: kotlin.Int,
    ): AssistedViewModel
}

```
