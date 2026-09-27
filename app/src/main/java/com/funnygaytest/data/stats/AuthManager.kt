package com.funnygaytest.data.stats

import android.app.Activity
import com.funnygaytest.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.PlayGames
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PlayGamesAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val auth: FirebaseAuth
) {

    private val mutex = Mutex()

    val userId: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    suspend fun signInWithPlayGames(
        activity: Activity,
        beforeAnonymousAccountRemoved: suspend (FirebaseUser) -> Unit,
        onAnonymousAccountKept: suspend (FirebaseUser) -> Unit
    ): String? = mutex.withLock {
        val credential = playGamesCredential(activity) ?: return@withLock null
        val current = auth.currentUser
        try {
            if (current != null && current.isAnonymous) {
                upgradeAnonymousAccount(current, credential, activity, beforeAnonymousAccountRemoved, onAnonymousAccountKept)
            } else {
                auth.signInWithCredential(credential).await()
            }
            auth.currentUser?.takeUnless { it.isAnonymous }?.uid
                .also { Timber.d("Signed in with Play Games, uid=$it") }
        } catch (e: FirebaseException) {
            Timber.w(e, "Firebase sign-in with Play Games failed")
            null
        }
    }

    suspend fun ensureSignedIn(): String? = mutex.withLock {
        auth.currentUser?.uid ?: try {
            auth.signInAnonymously().await().user?.uid.also { Timber.d("Signed in anonymously, uid=$it") }
        } catch (e: FirebaseException) {
            Timber.w(e, "Anonymous sign-in failed")
            null
        }
    }

    private suspend fun upgradeAnonymousAccount(
        anonymous: FirebaseUser,
        credential: AuthCredential,
        activity: Activity,
        beforeAnonymousAccountRemoved: suspend (FirebaseUser) -> Unit,
        onAnonymousAccountKept: suspend (FirebaseUser) -> Unit
    ) {
        try {
            anonymous.linkWithCredential(credential).await()
            Timber.d("Anonymous account linked to Play Games")
            return
        } catch (_: FirebaseAuthUserCollisionException) {
            Timber.d("Play Games account already exists, replacing the anonymous account")
        }
        val freshCredential = playGamesCredential(activity) ?: return
        beforeAnonymousAccountRemoved(anonymous)
        try {
            anonymous.delete().await()
            auth.signInWithCredential(freshCredential).await()
        } catch (e: FirebaseException) {
            if (auth.currentUser?.uid == anonymous.uid) onAnonymousAccountKept(anonymous)
            throw e
        }
    }

    private suspend fun playGamesCredential(activity: Activity): AuthCredential? {
        val signInClient = PlayGames.getGamesSignInClient(activity)
        return try {
            if (!signInClient.isAuthenticated.await().isAuthenticated) {
                Timber.d("Play Games is not signed in")
                return null
            }
            val serverAuthCode = signInClient
                .requestServerSideAccess(activity.getString(R.string.default_web_client_id), false)
                .await()
            PlayGamesAuthProvider.getCredential(serverAuthCode)
        } catch (e: ApiException) {
            Timber.w(e, "Play Games server auth code is unavailable")
            null
        }
    }
}
