package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swirlfist.desastre.R

@Composable
fun NotFound() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card{
            Text(
                text = stringResource(R.string.not_found),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun BoxScope.VerticalScrollableGradient(
    modifier: Modifier = Modifier,
    color: Color,
    height: Dp = 32.dp,
    padding: Dp = 8.dp,
    isTop: Boolean = false,
) {
    val gradientColorList = with(listOf(Color.Transparent, color)) {
        if (isTop) this.asReversed() else this
    }
    val paddingValues = if (isTop) PaddingValues(top = padding) else PaddingValues(bottom = padding)
    val alignment = if (isTop) Alignment.TopCenter else Alignment.BottomCenter

    Spacer(
        modifier = modifier
            .padding(paddingValues)
            .height(height)
            .background(Brush.verticalGradient(gradientColorList))
            .align(alignment)
    )
}