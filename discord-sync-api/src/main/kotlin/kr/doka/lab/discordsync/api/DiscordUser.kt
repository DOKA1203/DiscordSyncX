package kr.doka.lab.discordsync.api

import java.util.UUID

interface DiscordUser {
    val uuid: UUID
    val discordId: String

    fun unlink()
}
