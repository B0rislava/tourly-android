package com.tourly.app.core.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    BULGARIAN("bg");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}
