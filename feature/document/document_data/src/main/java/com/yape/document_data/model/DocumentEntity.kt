package com.yape.document_data.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class DocumentEntity(
    val id: String,
    val name: String,
    val type: String,
    val relativePath: String,
    val createdAt: Long,
    val sizeBytes: Long,
    val accessLog: List<Long> = emptyList()
)
