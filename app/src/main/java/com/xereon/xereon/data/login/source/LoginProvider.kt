package com.xereon.xereon.data.login.source

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginProvider @Inject constructor(
    private val server: LoginServer,
) {

    suspend fun login(email: String, password: String, token: String) =
        server.login(email = email, password = password, firebaseToke = token )

    suspend fun createUser(name: String, email: String, password: String, token: String) =
        server.createUser(name = name, email = email, password = password, firebaseToke = token )

    suspend fun resetPassword(email: String) =
        server.resetPassword(email = email)

}