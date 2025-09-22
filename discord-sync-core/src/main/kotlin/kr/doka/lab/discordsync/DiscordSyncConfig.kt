package kr.doka.lab.discordsync

data class DiscordSyncConfig(
    val botToken: String,
    val database: String,
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val discordClientId: String,
    val discordClientSecret: String,
    val discordRedirectUrl: String = "http://localhost:8080/callback",
    val discordTokenUrl: String = "https://discord.com/api/oauth2/token",
    val discordAuthUrl: String = "https://discord.com/api/oauth2/authorize",
)
