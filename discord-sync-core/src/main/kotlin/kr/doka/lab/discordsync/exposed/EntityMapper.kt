package kr.doka.lab.discordsync.exposed

import kr.doka.lab.discordsync.api.dto.AccountLink
import kr.doka.lab.discordsync.api.dto.AuthSession
import kr.doka.lab.discordsync.api.dto.Token
import kr.doka.lab.discordsync.exposed.entities.AccountLinkEntity
import kr.doka.lab.discordsync.exposed.entities.AuthSessionEntity
import kr.doka.lab.discordsync.exposed.entities.TokenEntity

fun AuthSessionEntity.toDTO() = AuthSession(
    id.value, mcUuid, state, status, createdAt, expiresAt, completedAt
)

fun AccountLinkEntity.toDTO() = AccountLink(
    id.value, mcUuid, discordUserId, linkedAt, verifiedAt, isActive
)

fun TokenEntity.toDTO() = Token(
    id.value, accountLink.id.value, accessToken, refreshToken,
    tokenType, expiresAt, createdAt, updatedAt
)
