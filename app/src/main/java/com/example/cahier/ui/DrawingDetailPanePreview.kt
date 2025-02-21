package com.example.cahier.ui

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.semantics.Role
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingPreview(
    strokes: List<Stroke>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canvasStrokeRenderer = remember { CanvasStrokeRenderer.create() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
    ) {
        val canvas = drawContext.canvas.nativeCanvas
        strokes.forEach { stroke ->
            canvasStrokeRenderer.draw(
                stroke = stroke,
                canvas = canvas,
                strokeToScreenTransform = Matrix()
            )
        }
    }
}