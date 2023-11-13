package com.example.metroidstore.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> HorizontalPagedList(
    modifier: Modifier = Modifier,
    data: List<T>,
    pageContent: @Composable (T) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { data.size }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        HorizontalPager(state = pagerState) { page ->
            pageContent(data[page])
        }

        PageIndicators(state = pagerState)
    }
}