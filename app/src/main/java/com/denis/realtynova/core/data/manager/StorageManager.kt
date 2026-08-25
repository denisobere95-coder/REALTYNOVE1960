package com.denis.realtynova.core.data.manager

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    private val storage: FirebaseStorage
) {
    /**
     * Uploads an image to Firebase Storage and returns the download URL.
     * 
     * @param uri The local Uri of the image to upload.
     * @param path The directory path in Storage (e.g., "profile_pictures" or "property_images").
     * @return Result containing the download URL string.
     */
    suspend fun uploadImage(uri: Uri, path: String): Result<String> {
        return try {
            val fileName = UUID.randomUUID().toString()
            val imageRef = storage.reference.child("$path/$fileName")
            
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes an image from Firebase Storage.
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
