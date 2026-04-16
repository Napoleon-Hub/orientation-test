package com.funnygaytest.managers.firebase.auth

import android.app.Activity
import com.funnygaytest.R
import com.google.android.gms.games.PlayGames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PlayGamesAuthProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun startAuthFlow(activity: Activity, onComplete: (String) -> Unit) {
        val gamesSignInClient = PlayGames.getGamesSignInClient(activity)

        gamesSignInClient.isAuthenticated.addOnCompleteListener { task ->
            val isAuthenticated = task.isSuccessful && task.result.isAuthenticated

            if (isAuthenticated) {
                Timber.d("Play Games: Пользователь уже авторизован, берем код доступа...")
                exchangePlayGamesCodeForFirebase(activity, onComplete)
            } else {
                Timber.d("Play Games: Не авторизован, пробуем войти анонимно...")
                signInAnonymously(onComplete)
            }
        }
    }

    private fun exchangePlayGamesCodeForFirebase(activity: Activity, onComplete: (String) -> Unit) {
        val serverClientId = activity.getString(R.string.web_client_id)
        val gamesSignInClient = PlayGames.getGamesSignInClient(activity)

        gamesSignInClient.requestServerSideAccess(serverClientId, false)
            .addOnSuccessListener { serverAuthCode ->
                val credential = PlayGamesAuthProvider.getCredential(serverAuthCode)

                auth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->
                        Timber.i("Firebase: Вход через Play Games успешен. UID: ${result.user?.uid}")
                        onComplete(result.user?.uid ?: "")
                    }
                    .addOnFailureListener {
                        Timber.e(it, "Firebase: Ошибка обмена токена, откат на анонимку")
                        signInAnonymously(onComplete)
                    }
            }
            .addOnFailureListener {
                Timber.e(it, "Play Games: Не удалось получить ServerAuthCode")
                signInAnonymously(onComplete)
            }
    }

    private fun signInAnonymously(onComplete: (String) -> Unit) {
        if (auth.currentUser != null) {
            onComplete(auth.currentUser?.uid ?: "")
            return
        }

        auth.signInAnonymously().addOnSuccessListener { result ->
            Timber.i("Firebase: Анонимный вход успешен. UID: ${result.user?.uid}")
            onComplete(result.user?.uid ?: "")
        }
    }
}