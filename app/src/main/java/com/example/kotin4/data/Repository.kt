package com.example.kotin4.data

data class Repository(
    val id: Int,
    val fullName: String,
    val description: String?,
    val stargazersCount: Int,
    val language: String?
)

