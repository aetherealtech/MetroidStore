package com.example.metroidstore.productdetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.fakedatasources.ProductDataSourceFake
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.modifiers.shimmeringLoader
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.utilities.mapState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailView(
    viewModel: ProductDetailViewModel
) {
    val currentName by viewModel.name.collectAsState()

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp
        )
    ) {
        Text(
            text = currentName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium
        )

        AsyncLoadedShimmering(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f),
            data = viewModel.images
        ) { modifier, images ->
            val pagerState = rememberPagerState(
                pageCount = { images.size }
            )

            Box(
                modifier = modifier,
                contentAlignment = Alignment.BottomCenter
            ) {
                HorizontalPager(state = pagerState) { page ->
                    Image(
                        bitmap = images[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Row(
                    Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(pagerState.pageCount) { index ->
                        val color = if (pagerState.currentPage == index)
                            Color.DarkGray else
                            Color.LightGray

                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(color, CircleShape)
                                .size(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize(0.5f)
        )
    }
}

@Composable
fun <T> AsyncLoaded(
    modifier: Modifier = Modifier,
    data: StateFlow<T?>,
    loader: @Composable (Modifier) -> Unit = { CircularLoader(modifier) },
    content: @Composable (Modifier, T) -> Unit,
) {
    val currentData by data.collectAsState()

    currentData?.let { loadedData ->
        content(modifier, loadedData)
    } ?: loader(modifier)
}

@Composable
fun <T> AsyncLoadedShimmering(
    modifier: Modifier = Modifier,
    data: StateFlow<T?>,
    content: @Composable (Modifier, T) -> Unit
) {
    AsyncLoaded(
        modifier = modifier,
        data = data,
        loader = { Box(modifier = modifier.shimmeringLoader()) },
        content = content,
    )
}

suspend fun <E, R> Collection<E>.parallelMap(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    transform: suspend (E) -> R
) = coroutineScope {
    this@parallelMap
        .map { element ->
            async(dispatcher) {
                transform(element)
            }
        }
        .map { element -> element.await() }
}

class ProductDetailViewModel(
    productId: ProductID,
    repository: ProductRepository
): ViewModel() {
    private val _name = MutableStateFlow<String?>(null)
    private val _images = MutableStateFlow<ImmutableList<ImageBitmap>?>(null)

    val name = _name
        .mapState { name -> name ?: "Loading..." }

    val images = _images
        .asStateFlow()

    init {
        viewModelScope.launch {
            val product = repository.getProductDetails(productId)

            _name.value = product.name

            _images.value = product.images
                .parallelMap { imageSource -> imageSource.load() }
                .toImmutableList()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    MetroidStoreTheme {
        ProductDetailView(
            viewModel = ProductDetailViewModel(
                productId = ProductID(0),
                repository = ProductRepository(
                    dataSource = ProductDataSourceFake()
                )
            )
        )
    }
}