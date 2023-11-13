package com.example.metroidstore.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.metroidstore.modifiers.shimmeringLoader
import kotlinx.coroutines.flow.StateFlow

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