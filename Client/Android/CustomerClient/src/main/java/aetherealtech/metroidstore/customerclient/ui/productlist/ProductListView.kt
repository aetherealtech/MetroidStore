package aetherealtech.metroidstore.customerclient.productlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aetherealtech.metroidstore.customerclient.fakedatasources.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.utilities.cacheLatest
import aetherealtech.metroidstore.customerclient.utilities.mapState
import aetherealtech.metroidstore.customerclient.widgets.AsyncLoadedShimmering
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: ProductListViewModel
) {
    var searchQuery by remember { mutableStateOf(viewModel.searchQuery.value ?: "") }

    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery -> searchQuery = newQuery },
                    onSearch = { viewModel.searchQuery.value = if (searchQuery.isEmpty()) null else searchQuery },
                    active = false,
                    onActiveChange = { },
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    placeholder = { Text("Search Products") },
                    shape = RoundedCornerShape(8.dp),
                    content = { }
                )
            }
        ))
    }

    AsyncLoadedShimmering(
        modifier = modifier,
        data = viewModel.items
    ) { modifier, items ->
        LazyColumn(modifier = modifier) {
            this.items(items) { rowViewModel ->
                ProductRowView(
                    viewModel = rowViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModel(
    productRepository: ProductRepository,
    selectProduct: (ProductID) -> Unit
): ViewModel() {
    val items: StateFlow<ImmutableList<ProductRowViewModel>?>

    val searchQuery = MutableStateFlow<String?>(null)

    init {
        items = searchQuery
            .flatMapLatest { searchQuery ->
                val result = MutableStateFlow<ImmutableList<ProductRowViewModel>?>(null)

                viewModelScope.launch {
                    result.value = productRepository.getProducts(searchQuery)
                        .map { product ->
                            ProductRowViewModel(
                                product = product,
                                select = { selectProduct(product.id) }
                            )
                        }
                        .toImmutableList()
                }

                return@flatMapLatest result
            }
            .cacheLatest(initialValue = null)
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    MetroidStoreTheme {
        ProductListView(
            setAppBarState = { },
            viewModel = ProductListViewModel(
                productRepository = ProductRepository(
                    dataSource = DataSourceFake()
                ),
                selectProduct = { }
            )
        )
    }
}