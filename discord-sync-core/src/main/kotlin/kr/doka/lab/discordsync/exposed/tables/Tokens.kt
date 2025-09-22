package kr.doka.lab.discordsync.exposed.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp

object Tokens : LongIdTable(name = "tokens", columnName = "id") {
    val accountLink =
        reference(
            name = "account_link_id",
            foreign = AccountLinks,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.NO_ACTION,
        ).index()

    val accessToken = text("access_token")
    val refreshToken = text("refresh_token")
    val tokenType = varchar("token_type", length = 16).default("Bearer")
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
