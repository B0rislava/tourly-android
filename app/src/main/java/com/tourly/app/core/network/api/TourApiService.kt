package com.tourly.app.core.network.api

import android.content.Context
import android.net.Uri
import com.tourly.app.core.network.model.CreateTourRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TourApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun createTour(
        context: Context,
        request: CreateTourRequestDto,
        imageUri: Uri?
    ): HttpResponse {
        return client.post("tours") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // Add tour data as JSON part
                        append(
                            key = "data",
                            value = Json.encodeToString(request),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "application/json")
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"data\"")
                            }
                        )

                        // Add image if present
                        imageUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                val bytes = inputStream.readBytes()
                                val filename = getFileName(context, uri) ?: "tour_image.jpg"

                                appendInput(
                                    key = "image",
                                    headers = Headers.build {
                                        append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"$filename\"")
                                        append(HttpHeaders.ContentType, "image/jpeg")
                                    },
                                    size = bytes.size.toLong()
                                ) {
                                    buildPacket { writeFully(bytes) }
                                }
                            }
                        }
                    }
                )
            )
        }
    }

    suspend fun getMyTours(): HttpResponse {
        return client.get("tours/my")
    }

    suspend fun getAllTours(): HttpResponse {
        return client.get("tours")
    }

    suspend fun getTour(id: Long): HttpResponse {
        return client.get("tours/$id")
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }
}
