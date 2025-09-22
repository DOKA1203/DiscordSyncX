package kr.doka.lab.discordsync.exposed.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object AccountLinks : LongIdTable(name = "account_link", columnName = "id") {
    val mcUuid = uuid("mc_uuid").index()
    val discordUserId = long("discord_user_id").index()
    val linkedAt = timestamp("linked_at")
    val verifiedAt = timestamp("verified_at").nullable()
    val isActive = bool("is_active").default(true)

    init {
        uniqueIndex(mcUuid, discordUserId)
    }
}
