package kr.doka.lab.discordsync.exposed.repositories

import kr.doka.lab.discordsync.api.dto.AccountLink
import kr.doka.lab.discordsync.exposed.entities.AccountLinkEntity
import kr.doka.lab.discordsync.exposed.tables.AccountLinks
import kr.doka.lab.discordsync.exposed.toDTO
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class AccountLinkRepository {
    fun link(
        mcUuid: UUID,
        discordUserId: Long,
    ): AccountLink =
        transaction {
            val now = Instant.now()
            val existing =
                AccountLinkEntity.find {
                    (AccountLinks.mcUuid eq mcUuid) and (AccountLinks.discordUserId eq discordUserId)
                }.singleOrNull()

            val entity =
                existing?.apply {
                    isActive = true
                    verifiedAt = now
                } ?: AccountLinkEntity.new {
                    this.mcUuid = mcUuid
                    this.discordUserId = discordUserId
                    this.linkedAt = now
                    this.verifiedAt = now
                    this.isActive = true
                }
            entity.toDTO()
        }

    fun findByMinecraftUuid(mcUuid: UUID): List<AccountLink> =
        transaction {
            AccountLinkEntity.find { AccountLinks.mcUuid eq mcUuid }
                .map { it.toDTO() }
        }

    fun findByDiscordId(discordUserId: Long): List<AccountLink> =
        transaction {
            AccountLinkEntity.find { AccountLinks.discordUserId eq discordUserId }
                .map { it.toDTO() }
        }

    fun unlink(
        mcUuid: UUID,
        discordUserId: Long,
    ) = transaction {
        AccountLinkEntity.find {
            (AccountLinks.mcUuid eq mcUuid) and (AccountLinks.discordUserId eq discordUserId)
        }.forEach { it.isActive = false }
    }
}
