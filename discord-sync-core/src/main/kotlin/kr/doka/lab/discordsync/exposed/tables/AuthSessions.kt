package kr.doka.lab.discordsync.exposed.tables

import kr.doka.lab.discordsync.AuthStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object AuthSessions : IntIdTable(name = "auth_session", columnName = "session_id") {
    val mcUuid = uuid("mc_uuid").index()
    val state = text("state").uniqueIndex()
    val status = enumerationByName("status", 16, AuthStatus::class)
    val createdAt = timestamp("created_at")
    val expiresAt = timestamp("expires_at")
    val completedAt = timestamp("completed_at").nullable()
}
