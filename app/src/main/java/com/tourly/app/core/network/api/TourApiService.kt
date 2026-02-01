package com.tourly.app.core.network.api

import android.net.Uri
import android.content.Context
import android.provider.OpenableColumns.DISPLAY_NAME
import com.tourly.app.create_tour.data.dto.CreateTourRequestDto
import com.tourly.app.core.domain.model.TourFilters
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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
                            value = Json.encodeToString(CreateTourRequestDto.serializer(), request),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "application/json")
                            }
                        )

                        // Add image if present
                        imageUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                val bytes = inputStream.readBytes()
                                val filename = getFileName(context, uri) ?: "tour_image.jpg"

                                append(
                                    key = "image",
                                    value = bytes,
                                    headers = Headers.build {
                                        append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                                        append(HttpHeaders.ContentType, "image/jpeg")
                                    }
                                )
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

    suspend fun getAllTags(): HttpResponse {
        return client.get("tags")
    }

    suspend fun getAllTours(filters: TourFilters): HttpResponse {
        return client.get("tours") {
            // Add each tag as separate parameter
            filters.tags.forEach { tag -> parameter("tags", tag) }
            
            // Add optional filters
            filters.location?.let { parameter("location", it) }
            filters.minPrice?.let { parameter("minPrice", it) }
            filters.maxPrice?.let { parameter("maxPrice", it) }
            filters.minRating?.let { parameter("minRating", it) }
            filters.scheduledAfter?.let { parameter("scheduledAfter", it) }
            filters.scheduledBefore?.let { parameter("scheduledBefore", it) }
            
            // Add sorting
            parameter("sortBy", filters.sortBy.name.lowercase())
            parameter("sortOrder", filters.sortOrder.name.lowercase())
        }
    }

    suspend fun getTour(id: Long): HttpResponse {
        return client.get("tours/$id")
    }

    suspend fun updateTour(
        context: Context,
        id: Long,
        request: CreateTourRequestDto,
        imageUri: Uri?
    ): HttpResponse {
        return client.post("tours/$id") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // Add tour data as JSON part
                        append(
                            key = "data",
                            value = Json.encodeToString(CreateTourRequestDto.serializer(), request),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "application/json")
                            }
                        )

                        // Add image if present
                        imageUri?.let { uri ->
                            if (uri.scheme == "content" || uri.scheme == "file") {
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    val bytes = inputStream.readBytes()
                                    val filename = getFileName(context, uri) ?: "tour_image.jpg"

                                    append(
                                        key = "image",
                                        value = bytes,
                                        headers = Headers.build {
                                            append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                                            append(HttpHeaders.ContentType, "image/jpeg")
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            )
        }
    }

    suspend fun deleteTour(id: Long): HttpResponse {
        return client.delete("tours/$id")
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }
}
