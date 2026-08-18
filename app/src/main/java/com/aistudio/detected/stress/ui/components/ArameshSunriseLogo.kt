package com.aistudio.detected.stress.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aistudio.detected.stress.R

@Composable
fun ArameshSunriseLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_aramesh_logo),
        contentDescription = "Aramesh Yar Logo",
        modifier = modifier.size(64.dp).clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
