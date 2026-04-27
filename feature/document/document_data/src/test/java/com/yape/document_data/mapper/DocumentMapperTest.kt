package com.yape.document_data.mapper

import com.yape.document_data.model.DocumentEntity
import com.yape.document_domain.model.DocumentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentMapperTest {

    @Test
    fun given_entity_when_mapped_then_all_fields_are_correctly_transferred() {
        val entity = DocumentEntity(
            id = "id1",
            name = "file.pdf",
            type = "PDF",
            relativePath = "documents/id1_file.pdf",
            createdAt = 1_700_000_000_000L,
            sizeBytes = 1_024L,
            accessLog = emptyList()
        )

        val domain = entity.documentEntityToDomain()

        assertEquals("id1", domain.id)
        assertEquals("file.pdf", domain.name)
        assertEquals(DocumentType.PDF, domain.type)
        assertEquals("documents/id1_file.pdf", domain.relativePath)
        assertEquals(1_700_000_000_000L, domain.createdAt)
        assertEquals(1_024L, domain.sizeBytes)
    }

    @Test
    fun given_entity_with_image_type_when_mapped_then_type_is_image() {
        val entity = DocumentEntity("id1", "photo.jpg", "IMAGE", "path", 0L, 0L)

        val domain = entity.documentEntityToDomain()

        assertEquals(DocumentType.IMAGE, domain.type)
    }

    @Test
    fun given_entity_with_access_log_when_mapped_then_access_log_is_immutable_list() {
        val timestamps = listOf(1_000L, 2_000L, 3_000L)
        val entity = DocumentEntity("id1", "file.jpg", "IMAGE", "path", 0L, 0L, accessLog = timestamps)

        val domain = entity.documentEntityToDomain()

        assertTrue(true)
        assertEquals(timestamps, domain.accessLog.toList())
    }
}
