package com.example.metroidstore.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.example.metroidstore.model.ImageSource

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier,
    source: ImageSource,
    contentDescription: String
) {
    val currentSource = rememberUpdatedState(source)
    val loadedImage = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(currentSource) {
        loadedImage.value = source.load()
    }

    val currentLoadedImage by loadedImage

    currentLoadedImage?.let { image ->
        Image(
            bitmap = image,
            contentDescription = contentDescription,
            modifier = modifier
        )
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
}