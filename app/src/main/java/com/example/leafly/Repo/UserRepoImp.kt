package com.example.leafly.Repo

import com.example.leafly.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepoImp : UserRepo {
    private val auth = FirebaseAuth.getInstance()
    private val realtimeDb = FirebaseDatabase.getInstance()
    private val usersRef = realtimeDb.getReference("users")
    private val firestoreDb = FirebaseFirestore.getInstance()

    override suspend fun login(email: String, password: String): Result<String> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success("Login Successful")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun forgetPassword(email: String): Result<String> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success("Reset link sent to $email")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUserById(id: String): Result<UserModel?> = try {
        val snapshot = usersRef.child(id).get().await()
        val user = snapshot.getValue(UserModel::class.java)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAllUser(): Result<List<UserModel?>> = try {
        val snapshot = usersRef.get().await()
        val users = snapshot.children.map { it.getValue(UserModel::class.java) }
        Result.success(users)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(email: String, password: String): Result<String> = try {
        auth.createUserWithEmailAndPassword(email, password).await()
        val uid = auth.currentUser?.uid ?: ""
        Result.success(uid)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addUser(id: String, model: UserModel): Result<String> = try {
        usersRef.child(id).setValue(model).await()
        Result.success("User added")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun editProfile(id: String, model: UserModel): Result<String> = try {
        usersRef.child(id).updateChildren(model.toMap()).await()
        Result.success("Profile updated")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteUser(id: String): Result<String> = try {
        usersRef.child(id).removeValue().await()
        Result.success("Account deleted")
    } catch (e: Exception) {
        Result.failure(e)
    }
}
