package com.example.cahier.ui

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.withSave
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.input.motionprediction.MotionEventPredictor
import com.example.cahier.data.CahierUiState

@SuppressLint("ClickableViewAccessibility")
@Composable
fun DrawingSurface(
    strokes: List<Stroke>,
    inProgressStrokesView: InProgressStrokesView,
    canvasStrokeRenderer: CanvasStrokeRenderer,
    onDrawing: (
        event: MotionEvent,
        inProgressStrokesView: InProgressStrokesView,
        pointerIdToStrokeId: MutableMap<Int, InProgressStrokeId>,
        predictor: MotionEventPredictor,
    ) -> Unit,
    uiState: CahierUiState,
    modifier: Modifier = Modifier
) {
    val pointerIdToStrokeId = remember { mutableMapOf<Int, InProgressStrokeId>() }
    val predictor = remember { MotionEventPredictor.newInstance(inProgressStrokesView) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val rootView = FrameLayout(context)
                inProgressStrokesView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                rootView.addView(inProgressStrokesView)

                val touchListener = View.OnTouchListener { view, event ->
                    view.requestUnbufferedDispatch(event)
                    onDrawing(
                        event,
                        inProgressStrokesView,
                        pointerIdToStrokeId,
                        predictor,
                    )
                    true
                }
                rootView.setOnTouchListener(touchListener)
                rootView
            },
            update = {
            },
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize()

            ) {
                Canvas(
                    modifier = Modifier
                ) {
                    val canvas = drawContext.canvas.nativeCanvas
                    strokes.forEach { stroke ->
                        canvas.withSave {
                            canvasStrokeRenderer.draw(
                                stroke = stroke,
                                canvas = this,
                                strokeToScreenTransform = Matrix()
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                if (uiState.note.imageUriList?.isNotEmpty() == true) {
                    NoteImagesView(
                        images = uiState.note.imageUriList,
                        onClearImages = { /*TODO*/ },
                    )
                }
            }

        }
    }
}