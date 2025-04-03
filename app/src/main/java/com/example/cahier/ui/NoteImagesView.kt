package com.example.cahier.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cahier.R

@Composable
fun NoteImagesView(
    images: List<String>,
    onClearImages: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        LazyColumn {
            item {
                Spacer(modifier = Modifier.padding(8.dp))
            }
            images.forEach { imageUri ->
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = stringResource(R.string.uploaded_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}