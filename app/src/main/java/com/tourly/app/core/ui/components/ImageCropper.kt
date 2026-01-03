package com.tourly.app.core.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect as ComposeRect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Composable
fun ImageCropperDialog(
    imageUri: Uri,
    onCrop: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load bitmap with proper orientation handling
    androidx.compose.runtime.LaunchedEffect(imageUri) {
        withContext(Dispatchers.IO) {
            try {
                val originalBitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }

                if (originalBitmap != null) {
                    // Read EXIF orientation
                    val exif = context.contentResolver.openInputStream(imageUri)?.use { stream ->
                        ExifInterface(stream)
                    }

                    val orientation = exif?.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    ) ?: ExifInterface.ORIENTATION_NORMAL

                    // Apply rotation if needed
                    bitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipBitmap(originalBitmap, horizontal = true)
                        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipBitmap(originalBitmap, horizontal = false)
                        ExifInterface.ORIENTATION_TRANSPOSE -> {
                            // Flip horizontal then rotate 270
                            val flipped = flipBitmap(originalBitmap, horizontal = true)
                            rotateBitmap(flipped, 270f)
                        }
                        ExifInterface.ORIENTATION_TRANSVERSE -> {
                            // Flip horizontal then rotate 90
                            val flipped = flipBitmap(originalBitmap, horizontal = true)
                            rotateBitmap(flipped, 90f)
                        }
                        else -> originalBitmap
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                bitmap?.let { btm ->
                    ImageCropperContent(
                        bitmap = btm,
                        onCrop = { croppedBitmap ->
                            // Save cropped bitmap to file
                            try {
                                val file = File(context.cacheDir, "cropped_profile_${System.currentTimeMillis()}.jpg")
                                val out = FileOutputStream(file)
                                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                out.flush()
                                out.close()
                                onCrop(Uri.fromFile(file))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        onCancel = onDismiss
                    )
                } ?: Text(
                    text = "Loading image...",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// Helper function to rotate bitmap
private fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees)
    }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

// Helper function to flip bitmap
private fun flipBitmap(source: Bitmap, horizontal: Boolean): Bitmap {
    val matrix = Matrix().apply {
        if (horizontal) {
            preScale(-1f, 1f)
        } else {
            preScale(1f, -1f)
        }
    }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

@Composable
fun ImageCropperContent(
    bitmap: Bitmap,
    onCrop: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Circle constraints
    val density = LocalDensity.current
    val circleRadius = with(density) { 150.dp.toPx() }

    // Calculate minimum scale to ensure crop circle is always covered
    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()
    val circleDiameter = circleRadius * 2f
    val minScale = circleDiameter / min(imageWidth, imageHeight)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(bitmap, circleRadius) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Apply zoom with constraints
                    val newScale = (scale * zoom).coerceIn(minScale, 5f)

                    // Apply pan
                    val newOffset = offset + pan

                    // Calculate constraints for pan
                    val scaledWidth = imageWidth * newScale
                    val scaledHeight = imageHeight * newScale

                    // Maximum offset to keep crop circle within image bounds
                    val maxOffsetX = ((scaledWidth - circleDiameter) / 2f).coerceAtLeast(0f)
                    val maxOffsetY = ((scaledHeight - circleDiameter) / 2f).coerceAtLeast(0f)

                    // Constrain offset
                    val constrainedOffset = Offset(
                        x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                        y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                    )

                    scale = newScale
                    offset = constrainedOffset
                }
            }
    ) {
        if (containerSize != IntSize.Zero) {
            val screenWidth = containerSize.width.toFloat()
            val screenHeight = containerSize.height.toFloat()
            val center = Offset(screenWidth / 2f, screenHeight / 2f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val imageWidth = bitmap.width.toFloat()
                val imageHeight = bitmap.height.toFloat()

                // Draw Image
                val scaledWidth = imageWidth * scale
                val scaledHeight = imageHeight * scale

                val topLeftX = center.x - (scaledWidth / 2f) + offset.x
                val topLeftY = center.y - (scaledHeight / 2f) + offset.y

                // Draw the image
                drawImage(
                    image = bitmap.asImageBitmap(),
                    dstOffset = IntOffset(topLeftX.toInt(), topLeftY.toInt()),
                    dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt())
                )

                // Draw overlay with hole
                val path = Path().apply {
                    addRect(ComposeRect(0f, 0f, size.width, size.height))

                    val circlePath = Path().apply {
                        addOval(ComposeRect(
                            center.x - circleRadius,
                            center.y - circleRadius,
                            center.x + circleRadius,
                            center.y + circleRadius
                        ))
                    }

                    op(this, circlePath, PathOperation.Difference)
                }

                drawPath(path, Color.Black.copy(alpha = 0.7f))

                // Draw circle border
                drawCircle(
                    color = Color.White,
                    radius = circleRadius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Buttons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val imageWidth = bitmap.width.toFloat()
                        val imageHeight = bitmap.height.toFloat()
                        val scaledWidth = imageWidth * scale
                        val scaledHeight = imageHeight * scale

                        val topLeftX = center.x - (scaledWidth / 2f) + offset.x
                        val topLeftY = center.y - (scaledHeight / 2f) + offset.y

                        val circleX = center.x - circleRadius
                        val circleY = center.y - circleRadius
                        val circleDiam = circleRadius * 2

                        val cropX = (circleX - topLeftX) / scale
                        val cropY = (circleY - topLeftY) / scale
                        val cropWidth = circleDiam / scale
                        val cropHeight = circleDiam / scale

                        val finalX = max(0, cropX.toInt())
                        val finalY = max(0, cropY.toInt())
                        val finalW = min(bitmap.width - finalX, cropWidth.toInt())
                        val finalH = min(bitmap.height - finalY, cropHeight.toInt())

                        if (finalW > 0 && finalH > 0) {
                            val cropped = Bitmap.createBitmap(bitmap, finalX, finalY, finalW, finalH)
                            onCrop(cropped)
                        }
                    }
                ) {
                    Text("Set Profile Picture")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Color.White)
                }
            }
        }
    }
}