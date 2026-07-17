package com.example.leafly

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.example.leafly.model.UserModel

object GoogleSignInHelper {

    // Web client ID from google-services.json (type 3)
    private const val WEB_CLIENT_ID = "990732134695-n238kcigok1054ttkiomf922jq57s451.apps.googleusercontent.com"

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(context: Context): Intent {
        return getGoogleSignInClient(context).signInIntent
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.let {
                            // Save user to Realtime Database
                            val userModel = UserModel(
                                id = it.uid,
                                name = it.displayName ?: "",
                                email = it.email ?: "",
                                avatarUrl = it.photoUrl?.toString() ?: ""
                            )
                            FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(it.uid)
                                .setValue(userModel)
                        }
                        onSuccess()
                    } else {
                        onFailure(authTask.exception?.message ?: "Google sign-in failed")
                    }
                }
        } catch (e: Exception) {
            onFailure(e.message ?: "Google sign-in failed")
        }
    }

    fun signOut(context: Context) {
        getGoogleSignInClient(context).signOut()
        FirebaseAuth.getInstance().signOut()
    }
}
