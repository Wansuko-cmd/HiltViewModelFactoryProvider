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
    ).normalViewModelFactory()
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
    fun normalViewModelFactory(): NormalViewModelFactory
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
    ).assistedViewModelAssistedFactory()
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
    fun assistedViewModelAssistedFactory(): AssistedViewModelAssistedFactory
}

@AssistedFactory
public interface AssistedViewModelAssistedFactory {
    fun create(
        @Assisted text: kotlin.String, @Assisted("foo") num: kotlin.Int,
    ): AssistedViewModel
}

```

## FAQ

### hiltViewModelとの違い

1. 可読性の向上

それぞれのViewModelごとに専用の関数を用意するため、IDEなしの環境でもどのViewModelが代入されるか分かりやすい

```kotlin
// hiltViewModel
FooScreen(viewModel = hiltViewModel())

// HiltViewwModelFactoryProvider
FooScreen(viewModel = fooViewModel())
```

2. Assistedを利用する際に書くコード量が減る

hiltViewModelの場合Factoryを用意する必要があるが、本ライブラリでは`@HVMGenerator`アノテーションを付けることで内部で自動的に生成される

3. 自由度

引数にfactoryを渡せるhiltViewModelに対して、内部でfactoryを生成する本ライブラリの方が自由度は低い
また、現状本ライブラリはViewModelStoreOwnerの引き渡しに対応していない（LocalViewModelStoreOwenrの値を見る）

### 生成されるコードでhiltViewModelを使わない理由

1. 複数のアノテーションが付く

`hiltViewModel()`では`@HiltViewModel`が付けられたViewModelをDIしてくれるため、以下のようなコードになる

```kotlin
@HVMGenerator
@HiltViewModel
FooViewModel : ViewModel()
```

※ 責務の分割を行うため等でこの書き方を採用するのも断然アリだと思う

2. Assistedの方では`@HiltViewModel`を利用することが出来ない

  - `@HiltViewModel`の引数に利用するFactoryを指定する必要がある
    - そのためにはコードの編集を行う必要がある（kspでは不可能）
    - 安定しないメタプログラミングを行う必要が想定される
  - Assistedを使う場合は`@HiltViewModel`を付けずに、Assistedを使わない場合は`@HiltViewModel`を付ける・・・という仕様にはあまりしたくない

以上の二点（特に2）からhiltViewModelの採用を見送った。
