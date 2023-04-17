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
