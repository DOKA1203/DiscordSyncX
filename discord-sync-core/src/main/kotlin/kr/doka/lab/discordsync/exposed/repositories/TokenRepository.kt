package kr.doka.lab.discordsync.exposed.repositories

import kr.doka.lab.discordsync.api.dto.Token
import kr.doka.lab.discordsync.exposed.entities.AccountLinkEntity
import kr.doka.lab.discordsync.exposed.entities.TokenEntity
import kr.doka.lab.discordsync.exposed.tables.AccountLinks
import kr.doka.lab.discordsync.exposed.tables.Tokens
import kr.doka.lab.discordsync.exposed.toDTO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class TokenRepository {
    fun saveForAccountLink(
        accountLinkId: Long,
        token: Token,
    ): Token =
        transaction {
            val link =
                AccountLinkEntity.findById(accountLinkId)
                    ?: error("AccountLink not found: $accountLinkId")

            // 최신 1건만 보관
            TokenEntity.find { Tokens.accountLink eq link.id }.forEach { it.delete() }

            val now = Instant.now()
            TokenEntity.new {
                this.accountLink = link
                this.accessToken = token.accessToken
                this.refreshToken = token.refreshToken
                this.tokenType = token.tokenType
                this.expiresAt = token.expiresAt
                this.createdAt = now
                this.updatedAt = now
            }.toDTO()
        }

    fun deleteByAccountLink(accountLinkId: Long) =
        transaction {
            val linkId = EntityID(accountLinkId, AccountLinks)
            TokenEntity.find { Tokens.accountLink eq linkId }.forEach { it.delete() }
        }
}
