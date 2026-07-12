package com.example.graduateproject.data.repository

import android.util.Log
import com.example.graduateproject.data.mapper.toDomainUser
import com.example.graduateproject.data.remote.AuthApiService
import com.example.graduateproject.data.remote.dto.UserSyncDTO
import com.example.graduateproject.domain.model.User
import com.example.graduateproject.domain.repository.AuthRepository
import com.example.graduateproject.notification.FcmTokenRegistrar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val api: AuthApiService,
    private val fcmTokenRegistrar: FcmTokenRegistrar
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser =
                authResult.user ?: throw Exception("Lỗi: Tài khoản không tồn tại hoặc sai mật khẩu")


            val syncData = UserSyncDTO(
                email = firebaseUser.email ?: email,
                name = firebaseUser.displayName ?: "No Name",
                avatarUrl = firebaseUser.photoUrl?.toString(),
                phoneNumber = firebaseUser.phoneNumber
            )

            val userDto = api.syncFirebaseUser(syncData)
            runCatching { fcmTokenRegistrar.registerCurrentToken() }

            Result.success(userDto.toDomainUser())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser =
                authResult.user ?: throw Exception("Lỗi: Không tạo được user trên Firebase")

            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            firebaseUser.updateProfile(profileUpdates).await()


            val syncData = UserSyncDTO(
                email = email,
                name = name,
                phoneNumber = phone,
                avatarUrl = null
            )

            val userDto = api.syncFirebaseUser(syncData)
            runCatching { fcmTokenRegistrar.registerCurrentToken() }

            Result.success(userDto.toDomainUser())

        } catch (e: Exception) {
            Log.e("Auth", "Lỗi Đăng Ký: ", e)
            Result.failure(e)
        }
    }


    override suspend fun signInWithGoogle(googleIdToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser =
                authResult.user ?: throw Exception("Firebase user is null after Google sign-in")

            val syncData = UserSyncDTO(
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: "No Name",
                avatarUrl = firebaseUser.photoUrl?.toString(),
                phoneNumber = firebaseUser.phoneNumber
            )

            val userDto = api.syncFirebaseUser(syncData)
            runCatching { fcmTokenRegistrar.registerCurrentToken() }

            val domainUser = User(
                id = userDto.id,
                email = userDto.email,
                name = userDto.name,
            )


            Result.success(domainUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()

    }


    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return Result.success(null)

            val syncData = UserSyncDTO(
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: "No Name",
                avatarUrl = firebaseUser.photoUrl?.toString(),
                phoneNumber = firebaseUser.phoneNumber
            )

            val userDto = api.syncFirebaseUser(syncData)

            Result.success(userDto.toDomainUser())
        } catch (e: Exception) {
            Log.e("Auth", "Restore session failed", e)
            Result.failure(e)
        }
    }
}