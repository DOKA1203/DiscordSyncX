package kr.doka.lab.discordsync

data class DiscordSyncConfig(
    val databaseConfig: DatabaseConfig,
)

data class DatabaseConfig(
    val database: String,
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
)
