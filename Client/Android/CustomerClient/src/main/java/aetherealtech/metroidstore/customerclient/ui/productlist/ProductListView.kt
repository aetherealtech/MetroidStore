package aetherealtech.metroidstore.customerclient.ui.productlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.ui.productlistrow.ProductRowView
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.androiduitoolkit.AsyncLoadedShimmering
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