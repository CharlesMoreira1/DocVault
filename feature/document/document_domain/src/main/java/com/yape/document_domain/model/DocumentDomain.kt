package com.yape.document_domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DocumentDomain(
    val id: String,
    val name: String,
    val type: DocumentType,
    val relativePath: String,
    val createdAt: Long,
    val sizeBytes: Long,
    val accessLog: ImmutableList<Long> = persistentListOf()
)
