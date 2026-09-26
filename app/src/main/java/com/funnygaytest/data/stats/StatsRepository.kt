package com.funnygaytest.data.stats

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val authManager: AuthManager
) {

    private val usersCollection = firestore.collection("subjects")

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getStats(): Flow<LabStats?> = authManager.userId.flatMapLatest { uid ->
        if (uid == null) {
            flowOf(null)
        } else {
            callbackFlow {
                val registration = usersCollection.document(uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Timber.e(error, "Failed to observe lab stats")
                            return@addSnapshotListener
                        }
                        trySend(snapshot?.toObject(LabStats::class.java))
                    }
                awaitClose { registration.remove() }
            }
        }
    }

    suspend fun recordTestResult(isWin: Boolean? = null, achievementIds: List<String> = emptyList()) {
        val updates = buildMap<String, Any> {
            if (isWin == true) put(FIELD_WINS, FieldValue.increment(1))
            if (isWin == false) put(FIELD_LOSSES, FieldValue.increment(1))
            if (achievementIds.isNotEmpty()) put(FIELD_ACHIEVEMENTS, FieldValue.arrayUnion(*achievementIds.toTypedArray()))
        }
        if (updates.isEmpty()) return
        val uid = authManager.ensureSignedIn() ?: return
        writeToUser(uid, updates)
    }

    suspend fun signInWithPlayGames(activity: Activity) {
        var carriedStats: LabStats? = null
        val uid = authManager.signInWithPlayGames(activity) { anonymous ->
            val document = usersCollection.document(anonymous.uid)
            try {
                carriedStats = document.get().await().toObject(LabStats::class.java)
                document.delete().await()
            } catch (e: FirebaseException) {
                Timber.w(e, "Failed to move stats of the anonymous account")
            }
        }
        val stats = carriedStats ?: return
        if (uid == null) {
            Timber.w("Play Games sign-in failed after the anonymous account was removed, its stats are lost")
            return
        }
        mergeIntoUser(uid, stats)
    }

    private fun mergeIntoUser(uid: String, stats: LabStats) {
        val updates = buildMap<String, Any> {
            if (stats.wins > 0) put(FIELD_WINS, FieldValue.increment(stats.wins.toLong()))
            if (stats.losses > 0) put(FIELD_LOSSES, FieldValue.increment(stats.losses.toLong()))
            if (stats.achievements.isNotEmpty()) put(FIELD_ACHIEVEMENTS, FieldValue.arrayUnion(*stats.achievements.toTypedArray()))
        }
        if (updates.isNotEmpty()) writeToUser(uid, updates)
    }

    private fun writeToUser(uid: String, updates: Map<String, Any>) {
        usersCollection.document(uid)
            .set(updates, SetOptions.merge())
            .addOnFailureListener { Timber.e(it, "Failed to save lab stats") }
    }

    private companion object {
        const val FIELD_WINS = "wins"
        const val FIELD_LOSSES = "losses"
        const val FIELD_ACHIEVEMENTS = "achievements"
    }
}
