
package com.example.photogallery

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import coil.compose.rememberImagePainter
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ImageItem(uri: Uri) {
    val painter = rememberImagePainter(uri)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
    Log.d("PhotoGallery", "Displaying image: $uri")
}

