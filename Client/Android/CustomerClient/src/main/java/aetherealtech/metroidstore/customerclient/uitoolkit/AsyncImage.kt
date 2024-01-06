package aetherealtech.metroidstore.customerclient.uitoolkit

import aetherealtech.metroidstore.customerclient.model.ImageSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier,
    source: ImageSource,
    contentDescription: String
) {
    aetherealtech.androiduitoolkit.AsyncImage(
        modifier = modifier,
        source = { source.load() },
        contentDescription = contentDescription
    )
}