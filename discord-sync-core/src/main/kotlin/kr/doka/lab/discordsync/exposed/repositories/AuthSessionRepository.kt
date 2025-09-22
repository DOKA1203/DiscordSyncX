package kr.doka.lab.discordsync.exposed.repositories

import kr.doka.lab.discordsync.api.AuthStatus
import kr.doka.lab.discordsync.api.dto.AuthSession
import kr.doka.lab.discordsync.exposed.entities.AuthSessionEntity
import kr.doka.lab.discordsync.exposed.tables.AuthSessions
import kr.doka.lab.discordsync.exposed.toDTO
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class AuthSessionRepository {

    fun create(mcUuid: UUID, ttlSeconds: Long = 600): AuthSession = transaction {
        val now = Instant.now()
        val state = UUID.randomUUID().toString()
        val expiresAt = now.plusSeconds(ttlSeconds)

        AuthSessionEntity.new {
            this.mcUuid = mcUuid
            this.state = state
            this.status = AuthStatus.PENDING
            this.createdAt = now
            this.expiresAt = expiresAt
        }.toDTO()
    }

    fun findByState(state: String): AuthSession? = transaction {
        AuthSessionEntity.find { AuthSessions.state eq state }.singleOrNull()?.toDTO()
    }

    fun markCompleted(state: String) = transaction {
        AuthSessionEntity.find { AuthSessions.state eq state }.singleOrNull()?.apply {
            status = AuthStatus.COMPLETED
            completedAt = Instant.now()
        } ?: Unit
    }

    fun expireOldSessions() = transaction {
        val now = Instant.now()
        AuthSessionEntity.find {
            (AuthSessions.expiresAt less now) and (AuthSessions.status eq AuthStatus.PENDING)
        }.forEach { it.status = AuthStatus.EXPIRED }
    }
}
