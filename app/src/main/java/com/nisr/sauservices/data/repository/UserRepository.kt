package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.model.User

class UserRepository {
    private val users = mutableListOf<User>()

    fun registerUser(user: User) {
        users.add(user)
    }
}
