package com.example.cahier.data

import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.storage.decodeOrThrow
import androidx.ink.storage.encode
import androidx.ink.strokes.Stroke
import androidx.ink.strokes.StrokeInputBatch
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class Converters {

    private val gson: Gson = GsonBuilder().create()

    companion object {
        private val stockBrushToEnumValues = mapOf(
            StockBrushes.markerLatest to SerializedStockBrush.MARKER_LATEST,
            StockBrushes.pressurePenLatest to SerializedStockBrush.PRESSURE_PEN_LATEST,
            StockBrushes.highlighterLatest to SerializedStockBrush.HIGHLIGHTER_LATEST,
            StockBrushes.dashedLineLatest to SerializedStockBrush.DASHED_LINE_LATEST,
        )

        private val enumToStockBrush =
            stockBrushToEnumValues.entries.associate { (key, value) -> value to key }
    }

    private fun serializeBrush(brush: Brush): SerializedBrush {
        return SerializedBrush(
            size = brush.size,
            color = brush.colorLong,
            epsilon = brush.epsilon,
            stockBrush = stockBrushToEnumValues[brush.family] ?: SerializedStockBrush.MARKER_LATEST,
        )
    }

    fun serializeStroke(stroke: Stroke): String {
        val serializedBrush = serializeBrush(stroke.brush)
        val encodedSerializedInputs = ByteArrayOutputStream().use { outputStream ->
            stroke.inputs.encode(outputStream)
            outputStream.toByteArray()
        }

        val serializedStroke = SerializedStroke(
            inputs = encodedSerializedInputs,
            brush = serializedBrush
        )
        return gson.toJson(serializedStroke)
    }

    private fun deserializeStroke(serializedStroke: SerializedStroke): Stroke? {
        val inputs = ByteArrayInputStream(serializedStroke.inputs).use { inputStream ->
            StrokeInputBatch.decodeOrThrow(inputStream)
        }
        val brush = deserializeBrush(serializedStroke.brush)
        return Stroke(brush = brush, inputs = inputs)
    }

    private fun deserializeBrush(serializedBrush: SerializedBrush): Brush {
        val stockBrushFamily = enumToStockBrush[serializedBrush.stockBrush] ?: StockBrushes.markerV1

        return Brush.createWithColorLong(
            family = stockBrushFamily,
            colorLong = serializedBrush.color,
            size = serializedBrush.size,
            epsilon = serializedBrush.epsilon,
        )
    }

    fun deserializeStrokeFromString(data: String): Stroke? {
        val serializedStroke = gson.fromJson(data, SerializedStroke::class.java)
        return deserializeStroke(serializedStroke)
    }
}
