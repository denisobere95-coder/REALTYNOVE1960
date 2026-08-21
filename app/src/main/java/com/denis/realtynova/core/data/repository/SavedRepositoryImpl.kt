package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.features.saved.SavedRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class SavedRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : SavedRepository {

    private val userId: String? get() = auth.currentUser?.uid

    override fun observeSavedProperties(): Flow<List<Property>> = callbackFlow {
        val uid = userId
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("savedProperties")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing saved properties")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val properties = snapshot.toObjects(PropertyDto::class.java).map { it.toDomain() }
                    trySend(properties)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun remove(propertyId: String) {
        val uid = userId ?: return
        try {
            firestore.collection("users")
                .document(uid)
                .collection("savedProperties")
                .document(propertyId)
                .delete()
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error removing saved property")
        }
    }
    
    override suspend fun save(property: Property) {
        val uid = userId ?: return
        try {
            firestore.collection("users")
                .document(uid)
                .collection("savedProperties")
                .document(property.id)
                .set(property)
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error saving property")
        }
    }
}
