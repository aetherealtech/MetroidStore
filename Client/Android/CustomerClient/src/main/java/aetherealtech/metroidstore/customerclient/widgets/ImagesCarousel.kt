package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ImagesCarousel(
    modifier: Modifier = Modifier,
    data: StateFlow<List<ImageBitmap>?>
) {
    AsyncLoadedShimmering(
        modifier = modifier,
        data = data
    ) { contentModifier, images ->
        HorizontalPagedList(
            modifier = contentModifier,
            data = images
        ) { image ->
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}