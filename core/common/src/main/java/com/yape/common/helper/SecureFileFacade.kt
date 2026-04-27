package com.yape.common.helper

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns.DISPLAY_NAME
import android.provider.OpenableColumns.SIZE
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureFileFacade(private val context: Context) {
    private val keyStore = KeyStore.getInstance(KEY_STORE_TYPE).apply { load(null) }
    private val documentsDir = File(context.filesDir, CHILD_PATH).also { it.mkdirs() }

    init {
        if (keyStore.containsAlias(KEY_ALIAS).not()) {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_TYPE).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
                )
                generateKey()
            }
        }
    }

    fun saveFile(uri: Uri, fileName: String): String {
        val file = File(documentsDir, fileName)
        val cipher = encryptCipher()
        val plaintext = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return ""
        val ciphertext = cipher.doFinal(plaintext)
        FileOutputStream(file).use { out ->
            out.write(cipher.iv)
            out.write(ciphertext)
        }
        return "$CHILD_PATH/$fileName"
    }

    fun openDecryptedInputStream(relativePath: String): InputStream {
        val file = File(context.filesDir, relativePath)
        val iv = ByteArray(12)
        val ciphertext: ByteArray
        FileInputStream(file).use { fis ->
            fis.read(iv)
            ciphertext = fis.readBytes()
        }
        return ByteArrayInputStream(decryptCipher(iv).doFinal(ciphertext))
    }

    fun deleteFile(relativePath: String): Boolean =
        File(context.filesDir, relativePath).delete()

    private fun secretKey(): SecretKey =
        keyStore.getKey(KEY_ALIAS, null) as SecretKey

    private fun encryptCipher() = Cipher.getInstance(TRANSFORMATION_TYPE).apply {
        init(Cipher.ENCRYPT_MODE, secretKey())
    }

    private fun decryptCipher(byteArray: ByteArray) = Cipher.getInstance(TRANSFORMATION_TYPE).apply {
        init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, byteArray))
    }

    companion object {
        private const val KEY_ALIAS = "docvault_aes_key"
        private const val KEY_STORE_TYPE = "AndroidKeyStore"
        private const val TRANSFORMATION_TYPE = "AES/GCM/NoPadding"
        private const val CHILD_PATH = "documents"

        fun Uri.queryMetadata(context: Context): Pair<String, Long> {
            var name = "document_${System.currentTimeMillis()}"
            var size = 0L

            context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getColumnIndex(DISPLAY_NAME).takeIf { it >= 0 }
                        ?.let { name = cursor.getString(it) }
                    cursor.getColumnIndex(SIZE).takeIf { it >= 0 }
                        ?.let { size = cursor.getLong(it) }
                }
            }
            return name to size
        }
    }
}