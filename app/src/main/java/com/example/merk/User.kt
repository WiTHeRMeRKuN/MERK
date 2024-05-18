package com.example.merk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val dob: String,
    val gender: String,
    val username: String,
    val email: String,
    val phone: String,
    var password: String,
    val role: String,
    val recoveryWords: List<String>
)


