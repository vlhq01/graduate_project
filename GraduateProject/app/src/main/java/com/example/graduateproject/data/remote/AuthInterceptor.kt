package com.example.graduateproject.data.remote

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            try {
                val task = currentUser.getIdToken(false)

                val tokenResult = Tasks.await(task)
                val token = tokenResult.token
                Log.d("token", "intercept: $token")
                if (!token.isNullOrEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}