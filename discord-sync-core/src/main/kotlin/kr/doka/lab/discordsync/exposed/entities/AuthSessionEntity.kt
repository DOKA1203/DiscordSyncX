package kr.doka.lab.discordsync.exposed.entities

import kr.doka.lab.discordsync.exposed.tables.AuthSessions
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AuthSessionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthSessionEntity>(AuthSessions)

    var mcUuid by AuthSessions.mcUuid
    var state by AuthSessions.state
    var status by AuthSessions.status
    var createdAt by AuthSessions.createdAt
    var expiresAt by AuthSessions.expiresAt
    var completedAt by AuthSessions.completedAt
}
