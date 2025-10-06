package kr.doka.lab.discordsync

data class DiscordSyncConfig(
    val databaseConfig: DatabaseConfig,
    val authConfig: AuthConfig,
)

data class DatabaseConfig(
    val database: String,
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
)

data class AuthConfig(
    val url: String,
)
