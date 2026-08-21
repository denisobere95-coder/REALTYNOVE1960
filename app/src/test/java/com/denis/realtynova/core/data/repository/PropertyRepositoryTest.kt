package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.domain.model.Property
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PropertyRepositoryTest {

    @Mock
    lateinit var firestore: FirebaseFirestore

    @Mock
    lateinit var collectionReference: CollectionReference

    @Mock
    lateinit var query: Query

    @Mock
    lateinit var snapshot: QuerySnapshot

    private lateinit var repository: PropertyRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(firestore.collection("properties")).thenReturn(collectionReference)
        repository = PropertyRepositoryImpl(firestore)
    }

    @Test
    fun `getProperties should return list from firestore`() = runBlocking {
        // Arrange
        `when`(collectionReference.orderBy("createdAt", Query.Direction.DESCENDING)).thenReturn(query)
        `when`(query.get()).thenReturn(Tasks.forResult(snapshot))
        `when`(snapshot.toObjects(PropertyDto::class.java)).thenReturn(listOf(
            PropertyDto(id = "1", title = "Test Villa")
        ))

        // Act
        val result = repository.getProperties()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Test Villa", result[0].title)
    }
}
