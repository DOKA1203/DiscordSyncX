package kr.doka.lab.discordsync.exposed.entities

import kr.doka.lab.discordsync.exposed.tables.AccountLinks
import kr.doka.lab.discordsync.exposed.tables.Tokens
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AccountLinkEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AccountLinkEntity>(AccountLinks)

    var mcUuid by AccountLinks.mcUuid
    var discordUserId by AccountLinks.discordUserId
    var linkedAt by AccountLinks.linkedAt
    var verifiedAt by AccountLinks.verifiedAt
    var isActive by AccountLinks.isActive

    // 역참조: 이 계정 링크에 연결된 토큰들
    val tokens by TokenEntity referrersOn Tokens.accountLink
}
