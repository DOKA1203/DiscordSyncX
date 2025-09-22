package kr.doka.lab.discordsync.exposed.entities

import kr.doka.lab.discordsync.exposed.tables.Tokens
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TokenEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TokenEntity>(Tokens)

    var accountLink by AccountLinkEntity referencedOn Tokens.accountLink
    var accessToken by Tokens.accessToken
    var refreshToken by Tokens.refreshToken
    var tokenType by Tokens.tokenType
    var expiresAt by Tokens.expiresAt
    var createdAt by Tokens.createdAt
    var updatedAt by Tokens.updatedAt
}
