package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import com.aetherealtech.metroidstore.customerclient.R
import aetherealtech.metroidstore.customerclient.model.ImageSource
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier,
    source: ImageSource,
    contentDescription: String
) {
    val loadedImage = remember { mutableStateOf<Result<ImageBitmap?>>(Result.success(null)) }

    LaunchedEffect(source) {
        loadedImage.value = Result.success(null)
        loadedImage.value = Result.runCatching { source.load().asImageBitmap() }
    }

    val currentLoadedImage by loadedImage

    currentLoadedImage.fold(
        onSuccess = { successfulImage ->
            successfulImage?.let { image ->
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = image,
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .fillMaxSize(0.75f)
                    )
                }
            } ?: run {
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
        },
        onFailure = {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.broken_image),
                    contentDescription = "Image Failed"
                )
            }
        }
    )
}