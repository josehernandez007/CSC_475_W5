
package com.example.photogallery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import com.example.photogallery.ui.theme.PhotoGalleryTheme
import androidx.compose.ui.window.Dialog

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                loadImagesFromGallery()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoGalleryTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PhotoGallery()
                }
            }
        }
    }
    @Composable
    fun PhotoGallery() {
        var imageUris by remember { mutableStateOf(listOf<Uri>()) }
        var showFullScreen by remember { mutableStateOf(false) }
        var selectedUri by remember { mutableStateOf<Uri?>(null) }

        LaunchedEffect(Unit) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                imageUris = loadImagesFromGallery()
                Log.d("PhotoGallery", "Loaded ${imageUris.size} images")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(imageUris) { uri ->
                ImageItem(uri = uri) { clickedUri ->
                    selectedUri = clickedUri
                    showFullScreen = true
                }
            }
        }

        if (showFullScreen && selectedUri != null) {
            FullScreenImageViewer(selectedUri!!) {
                showFullScreen = false
            }
        }
    }
    private fun loadImagesFromGallery(): List<Uri> {
        val imageUris = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val images = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        images?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                imageUris.add(contentUri)
            }
        }
        return imageUris
    }
    @Composable
    fun ImageItem(uri: Uri, onClick: (Uri) -> Unit) {
        val painter = rememberImagePainter(uri)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onClick(uri) }
        )
    }
    @Composable
    fun FullScreenImageViewer(uri: Uri, onDismiss: () -> Unit) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun PreviewPhotoGallery() {
        PhotoGalleryTheme {
            PhotoGallery()
        }
    }
}
