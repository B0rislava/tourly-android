package com.tourly.app.core.presentation.ui.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

enum class CropShape {
    Circle, Rectangle
}

@Composable
fun ImageCropperDialog(
    imageUri: Uri,
    onCrop: (Uri) -> Unit,
    onDismiss: () -> Unit,
    cropShape: CropShape = CropShape.Circle,
    aspectRatio: Float = 1f,
    title: String = if (cropShape == CropShape.Circle) "Set Profile Picture" else "Crop Image"
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load bitmap with proper orientation handling
    LaunchedEffect(imageUri) {
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
                        cropShape = cropShape,
                        aspectRatio = aspectRatio,
                        buttonTitle = title,
                        onCrop = { croppedBitmap ->
                            // Save cropped bitmap to file
                            try {
                                val prefix = if (cropShape == CropShape.Circle) "cropped_profile_" else "cropped_image_"
                                val file = File(context.cacheDir, "${prefix}${System.currentTimeMillis()}.jpg")
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
    cropShape: CropShape,
    aspectRatio: Float, // width / height
    buttonTitle: String,
    onCrop: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Crop Area Calculation
    val density = LocalDensity.current
    val baseSize = with(density) { 300.dp.toPx() } // Base width for the crop area
    
    val cropWidth = baseSize
    val cropHeight = if (cropShape == CropShape.Circle) baseSize else baseSize / aspectRatio
    
    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()

    val minScaleX = cropWidth / imageWidth
    val minScaleY = cropHeight / imageHeight
    val minScale = max(minScaleX, minScaleY)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(bitmap, cropWidth, cropHeight) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Apply zoom with constraints
                    val newScale = (scale * zoom).coerceIn(minScale, 5f)

                    // Apply pan
                    val newOffset = offset + pan

                    // Calculate constraints for pan based on current scale
                    val scaledWidth = imageWidth * newScale
                    val scaledHeight = imageHeight * newScale


                    
                    val maxOffsetX = ((scaledWidth - cropWidth) / 2f).coerceAtLeast(0f)
                    val maxOffsetY = ((scaledHeight - cropHeight) / 2f).coerceAtLeast(0f)

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

                    val holePath = Path().apply {
                        val left = center.x - cropWidth / 2f
                        val top = center.y - cropHeight / 2f
                        val right = center.x + cropWidth / 2f
                        val bottom = center.y + cropHeight / 2f
                        
                        val rect = ComposeRect(left, top, right, bottom)
                        
                        if (cropShape == CropShape.Circle) {
                            addOval(rect)
                        } else {
                            addRect(rect)
                        }
                    }

                    op(this, holePath, PathOperation.Difference)
                }

                drawPath(path, Color.Black.copy(alpha = 0.7f))

                // Draw border
                val borderRect = ComposeRect(
                    center.x - cropWidth / 2f,
                    center.y - cropHeight / 2f,
                    center.x + cropWidth / 2f,
                    center.y + cropHeight / 2f
                )
                
                if (cropShape == CropShape.Circle) {
                    drawOval(
                        color = Color.White,
                        topLeft = Offset(borderRect.left, borderRect.top),
                        size = Size(borderRect.width, borderRect.height),
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(borderRect.left, borderRect.top),
                        size = Size(borderRect.width, borderRect.height),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
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
                        val imgW = bitmap.width.toFloat()
                        val imgH = bitmap.height.toFloat()
                        val scaledWidth = imgW * scale
                        // val scaledHeight = imgH * scale

                        val imgTopLeftX = center.x - (scaledWidth / 2f) + offset.x
                        val imgTopLeftY = center.y - (imgH * scale / 2f) + offset.y

                        val cropLeft = center.x - cropWidth / 2f
                        val cropTop = center.y - cropHeight / 2f
                        
                        val relativeCropX = cropLeft - imgTopLeftX
                        val relativeCropY = cropTop - imgTopLeftY
                        
                        val originalCropX = relativeCropX / scale
                        val originalCropY = relativeCropY / scale
                        val originalCropWidth = cropWidth / scale
                        val originalCropHeight = cropHeight / scale

                        val finalX = max(0, originalCropX.toInt())
                        val finalY = max(0, originalCropY.toInt())
                        val finalW = min(bitmap.width - finalX, originalCropWidth.toInt())
                        val finalH = min(bitmap.height - finalY, originalCropHeight.toInt())

                        if (finalW > 0 && finalH > 0) {
                            val cropped = Bitmap.createBitmap(bitmap, finalX, finalY, finalW, finalH)
                            onCrop(cropped)
                        }
                    }
                ) {
                    Text(buttonTitle)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Color.White)
                }
            }
        }
    }
}