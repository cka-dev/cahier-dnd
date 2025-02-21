package com.example.cahier.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class SerializedStroke(
    val inputs: ByteArray,
    val brush: SerializedBrush
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializedStroke

        if (!inputs.contentEquals(other.inputs)) return false
        if (brush != other.brush) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputs.contentHashCode()
        result = 31 * result + brush.hashCode()
        return result
    }
}

@Parcelize
@Serializable
data class SerializedBrush(
    val size: Float,
    val color: Long,
    val epsilon: Float,
    val stockBrush: SerializedStockBrush
) : Parcelable

enum class SerializedStockBrush {
    MARKER_LATEST,
    PRESSURE_PEN_LATEST,
    HIGHLIGHTER_LATEST,
    DASHED_LINE_LATEST,
}

@Parcelize
@Serializable
data class SerializedStrokeInputBatch(
    val toolType: SerializedToolType,
    val strokeUnitLengthCm: Float,
    val inputs: List<SerializedStrokeInput>
) : Parcelable

@Parcelize
@Serializable
data class SerializedStrokeInput(
    val x: Float,
    val y: Float,
    val timeMillis: Float,
    val pressure: Float,
    val tiltRadians: Float,
    val orientationRadians: Float,
    val strokeUnitLengthCm: Float
) : Parcelable

enum class SerializedToolType {
    STYLUS,
    TOUCH,
    MOUSE,
    UNKNOWN
}
