package kr.doka.lab.discordsync.api.dto

import java.time.Instant

data class Token(
    val accountLinkId: Long,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
)
