package com.yape.document_data.mapper

import com.yape.document_data.model.DocumentEntity
import com.yape.document_domain.model.DocumentDomain
import com.yape.document_domain.model.DocumentType
import kotlinx.collections.immutable.toImmutableList

fun DocumentEntity.documentEntityToDomain() = DocumentDomain(
    id = id,
    name = name,
    type = DocumentType.valueOf(type),
    relativePath = relativePath,
    createdAt = createdAt,
    sizeBytes = sizeBytes,
    accessLog = accessLog.toImmutableList()
)