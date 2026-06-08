package app.bytejoseph.tunnelvoice.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class AuthManager(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    fun signInAnonymously(onComplete: (Boolean) -> Unit) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Log.d("AuthManager", "Signed in: ${user?.uid}")
                onComplete(true)
            } else {
                Log.e("AuthManager", "Sign-in failed", task.exception)
                onComplete(false)
            }
        }
    }

    fun getCurrentUser() = auth.currentUser
}
