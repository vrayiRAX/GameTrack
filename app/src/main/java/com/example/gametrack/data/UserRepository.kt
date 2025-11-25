package com.example.gametrack.data

import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun getUserByUsername(username: String): User? =
        userDao.getUserByUsername(username)
    fun getPasswordHash(password: String): String = hashPassword(password)
    suspend fun registerUser(username: String, pass: String, email: String) {
        val passHash = hashPassword(pass)
        val newUser = User(
            username = username,
            email = email,
            passHash = passHash,
            fotoPerfilUri = null
        )
        userDao.insertUser(newUser)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}