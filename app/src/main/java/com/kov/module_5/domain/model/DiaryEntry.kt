package com.kov.module_5.domain.model

data class DiaryEntry(
    val fileName: String,
    val timestamp: Long,
    val title: String,
    val content: String
)

