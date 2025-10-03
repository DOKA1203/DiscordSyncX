package kr.doka.lab.discordsync

data class DiscordSyncConfig(
    val discordBotConfig: DiscordBotConfig,
    val discordApiConfig: DiscordApiConfig,
    val serverConfig: ServerConfig,
    val databaseConfig: DatabaseConfig,
)

data class DatabaseConfig(
    val database: String,
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
)

data class DiscordApiConfig(
    val clientId: String,
    val clientSecret: String,
    val discordRedirectUrl: String = "http://localhost:8080/callback",
    val discordTokenUrl: String = "https://discord.com/api/oauth2/token",
    val discordAuthUrl: String = "https://discord.com/oauth2/authorize",
)

data class DiscordBotConfig(
    val enable: Boolean = false,
    val botToken: String,
    val guildId: String,
)

data class ServerConfig(
    val port: Int = 8080,
    val url: String = "http://localhost:8080",
)
