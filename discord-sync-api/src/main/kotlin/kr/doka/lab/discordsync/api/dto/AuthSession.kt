package kr.doka.lab.discordsync.api.dto

import kr.doka.lab.discordsync.api.AuthStatus
import java.time.Instant
import java.util.UUID

data class AuthSession(
    val id: Int,
    val mcUuid: UUID,
    val state: String,
    val status: AuthStatus,
    val createdAt: Instant,
    val expiresAt: Instant,
    val completedAt: Instant?
)
