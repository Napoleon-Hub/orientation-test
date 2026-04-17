package com.funnygaytest.managers.firebase.firestore

import android.app.Activity
import com.funnygaytest.managers.firebase.auth.AuthManager
import com.funnygaytest.models.firebase.LabStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreManager @Inject constructor(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val authManager: AuthManager
) {

    private val userIdFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private val usersCollection = firestore.collection("subjects")

    fun recordTestResult(isWin: Boolean? = null, achievementId: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        val userDoc = usersCollection.document(uid)

        val updates = mutableMapOf<String, Any>()

        if (isWin == true) updates["wins"] = FieldValue.increment(1)
        if (isWin == false) updates["losses"] = FieldValue.increment(1)
        if (achievementId != null) updates["achievements"] = FieldValue.arrayUnion(achievementId)

        if (updates.isNotEmpty()) {
            userDoc.set(updates, SetOptions.merge())
                .addOnFailureListener { e -> Timber.e(e, "Error saving to Firestore") }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getStats(): Flow<LabStats?> = userIdFlow.flatMapLatest { uid ->
        if (uid == null) {
            flowOf(null)
        } else {
            callbackFlow {
                val registration = usersCollection.document(uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Timber.e(error, "Firestore error")
                            return@addSnapshotListener
                        }
                        trySend(snapshot?.toObject(LabStats::class.java))
                    }
                awaitClose { registration.remove() }
            }
        }
    }

    fun authorizeFirebase(activity: Activity) {
        authManager.startAuthFlow(activity) { uid ->
            Timber.d("Лаборатория готова для испытуемового: $uid")
        }
    }

}