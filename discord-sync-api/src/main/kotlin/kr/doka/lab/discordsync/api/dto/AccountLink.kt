package kr.doka.lab.discordsync.api.dto

import java.time.Instant
import java.util.UUID

data class AccountLink(
    val id: Long,
    val mcUuid: UUID,
    val discordUserId: Long,
    val linkedAt: Instant,
    val verifiedAt: Instant?,
    val isActive: Boolean
)
