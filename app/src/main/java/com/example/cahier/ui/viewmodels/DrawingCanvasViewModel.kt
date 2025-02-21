package com.example.cahier.ui.viewmodels

import android.view.MotionEvent
import androidx.annotation.UiThread
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesFinishedListener
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.brush.Brush
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.StockBrushes
import androidx.ink.geometry.AffineTransform
import androidx.ink.geometry.Intersection.intersects
import androidx.ink.geometry.MutableParallelogram
import androidx.ink.geometry.MutableSegment
import androidx.ink.geometry.MutableVec
import androidx.ink.strokes.Stroke
import androidx.input.motionprediction.MotionEventPredictor
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cahier.data.CahierUiState
import com.example.cahier.data.NotesRepository
import com.example.cahier.navigation.DrawingCanvasDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingCanvasViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CahierUiState())
    val uiState: StateFlow<CahierUiState> = _uiState.asStateFlow()

    private val noteId: Long = checkNotNull(savedStateHandle[DrawingCanvasDestination.NOTE_ID_ARG])

    val defaultBrush = Brush.createWithColorIntArgb(
        family = StockBrushes.pressurePenLatest,
        colorIntArgb = Color.Black.toArgb(),
        size = 5F,
        epsilon = 0.1F
    )

    private val _selectedBrush = MutableStateFlow<Brush>(defaultBrush)
    val selectedBrush: StateFlow<Brush> = _selectedBrush.asStateFlow()

    private val _isEraserMode = MutableStateFlow(false)
    val isEraserMode: StateFlow<Boolean> = _isEraserMode.asStateFlow()

    private var previousPoint: MutableVec? = null
    private val eraserPadding = 50f

    init {
        viewModelScope.launch {
            noteRepository.getNoteStream(noteId)
                .filterNotNull()
                .collect { note ->
                    if (note.strokesData != null) {
                        loadStrokes(note.id)
                        _uiState.update {
                            it.copy(note = note)
                        }
                    }
                }
        }
    }

    private suspend fun loadStrokes(noteId: Long) {
        val savedStrokes = noteRepository.getNoteStrokes(noteId)
        _uiState.value = _uiState.value.copy(strokes = savedStrokes)
    }

    suspend fun addStrokeToUiState(stroke: Stroke) {
        _uiState.update {
            it.copy(strokes = it.strokes + stroke)
        }
    }

    suspend fun saveStrokes() {
        noteRepository.updateNoteStrokes(noteId, _uiState.value.strokes)
    }

    suspend fun updateNoteTitle(newTitle: String) {
        val updatedNote = _uiState.value.note.copy(title = newTitle)
        noteRepository.updateNote(updatedNote)
        _uiState.value = _uiState.value.copy(note = updatedNote)
    }

    fun setInProgressStrokesFinishedListener(
        inProgressStrokesView: InProgressStrokesView,
        listener: InProgressStrokesFinishedListener
    ) {
        inProgressStrokesView.addFinishedStrokesListener(listener)
    }

    fun removeInProgressStrokesFinishedListener(
        inProgressStrokesView: InProgressStrokesView,
        listener: InProgressStrokesFinishedListener
    ) {
        inProgressStrokesView.removeFinishedStrokesListener(listener)
    }

    fun handleDrawing(
        event: MotionEvent,
        inProgressStrokesView: InProgressStrokesView,
        pointerIdToStrokeId: MutableMap<Int, InProgressStrokeId>,
        predictor: MotionEventPredictor,
    ) {

        if (_isEraserMode.value) {
            if (event.actionMasked == MotionEvent.ACTION_MOVE
                || event.actionMasked == MotionEvent.ACTION_DOWN
            ) {
                eraseIntersectingStrokes(
                    event.x,
                    event.y,
                    inProgressStrokesView
                )
            }
        } else {
            predictor.record(event)
            val predictedEvent = predictor.predict()
            try {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        val pointerIndex = event.actionIndex
                        pointerIdToStrokeId[event.getPointerId(pointerIndex)] =
                            inProgressStrokesView.startStroke(
                                event = event,
                                pointerId = event.getPointerId(pointerIndex),
                                brush = _selectedBrush.value
                            )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        for (pointerIndex in 0 until event.pointerCount) {
                            val pointerId = event.getPointerId(pointerIndex)
                            val currentStrokeId = pointerIdToStrokeId[pointerId] ?: continue
                            inProgressStrokesView.addToStroke(
                                event,
                                pointerId,
                                currentStrokeId,
                                predictedEvent
                            )
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        val pointerIndex = event.actionIndex
                        val pointerId = event.getPointerId(pointerIndex)
                        val currentStrokeId =
                            pointerIdToStrokeId.remove(pointerId) ?: return
                        inProgressStrokesView.finishStroke(event, pointerId, currentStrokeId)
                        inProgressStrokesView.postInvalidate()
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        val pointerIndex = event.actionIndex
                        val pointerId = event.getPointerId(pointerIndex)
                        val currentStrokeId = pointerIdToStrokeId.remove(pointerId)
                            ?: return
                        inProgressStrokesView.cancelStroke(currentStrokeId, event)
                    }
                }
            } finally {
                predictedEvent?.recycle()
            }
        }
    }

    @UiThread
    fun onStrokesFinished(
        strokes: Map<InProgressStrokeId, Stroke>,
        inProgressStrokesView: InProgressStrokesView
    ) {
        inProgressStrokesView.postOnAnimation {
            inProgressStrokesView.removeFinishedStrokes(strokes.keys)
        }
        strokes.values.forEach { stroke ->
            viewModelScope.launch {
                addStrokeToUiState(stroke)
                saveStrokes()
            }
        }
    }

    private fun eraseIntersectingStrokes(
        currentX: Float,
        currentY: Float,
        inProgressStrokesView: InProgressStrokesView
    ) {
        val prev = previousPoint
        if (prev == null) {
            previousPoint = MutableVec(currentX, currentY)
            return
        }

        val segment = MutableSegment(prev, MutableVec(currentX, currentY))
        val parallelogram = MutableParallelogram.fromSegmentAndPadding(segment, eraserPadding)

        previousPoint = MutableVec(currentX, currentY)

        val strokesToRemove = _uiState.value.strokes.filter { stroke ->
            stroke.shape.let { shape ->
                parallelogram.intersects(shape, AffineTransform.IDENTITY)
            }
        }

        if (strokesToRemove.isNotEmpty()) {
            _uiState.update { currentState ->
                val updatedStrokes = currentState.strokes - strokesToRemove.toSet()
                currentState.copy(strokes = updatedStrokes)
            }
            inProgressStrokesView.invalidate()
        }
        viewModelScope.launch {
            saveStrokes()
        }
    }

    fun changeBrush(brushFamily: BrushFamily) {
        _selectedBrush.update { currentBrush ->
            currentBrush.copy(
                family = brushFamily
            )
        }
    }

    fun changeBrushColor(color: Color) {
        _selectedBrush.update {
            it.copyWithColorIntArgb(
                colorIntArgb = color.toArgb()
            )
        }
    }

    fun setEraserMode(enabled: Boolean) {
        _isEraserMode.update { enabled }
        if (!enabled) {
            previousPoint = null
        } else {
            previousPoint = null
        }
    }

    suspend fun clearStrokes() {
        _uiState.update { it.copy(strokes = emptyList()) }
        saveStrokes()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            saveStrokes()
        }
    }

    companion object {
        private const val TAG = "DrawingCanvasViewModel"
    }
}
