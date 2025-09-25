package kr.doka.lab.discordsync

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kr.doka.lab.discordsync.exposed.tables.AccountLinks
import kr.doka.lab.discordsync.exposed.tables.AuthSessions
import kr.doka.lab.discordsync.exposed.tables.Tokens
import kr.doka.lab.discordsync.listeners.LoginListener
import kr.lab.doka.discordsync.AuthServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DiscordSyncPlugin : JavaPlugin() {
    val pluginScope =
        CoroutineScope(
            SupervisorJob() +
                Dispatchers.Default +
                CoroutineName("DiscordSyncScope") +
                CoroutineExceptionHandler { _, e -> logger.severe("Coroutine error: $e") },
        )

    companion object {
        lateinit var instance: DiscordSyncPlugin
        lateinit var authServer: AuthServer

        lateinit var pluginConfig: DiscordSyncConfig
    }



    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        pluginConfig = DiscordSyncConfig(
            DiscordBotConfig(
                config.getString("bot.token")!!,
                config.getString("bot.guildId")!!,
            ),
            DiscordApiConfig(
                config.getString("discord-api.client-id")!!,
                config.getString("discord-api.client-secret")!!,
                config.getString("discord-api.redirect-url")!!,
            ),
            ServerConfig(
                config.getInt("server.port", 8080),
            ),
            DatabaseConfig(
                config.getString("database.database")!!,
                config.getString("database.host")!!,
                config.getInt("database.port", 3306),
                config.getString("database.username")!!,
                config.getString("database.password")!!,
            )
        )
        authServer = AuthServer(pluginConfig)

        if (!connectMariaDB()) {
            logger.severe("데이터베이스 연결에 실패하여 플러그인을 비활성화합니다.")
            // 안전하게 플러그인 종료
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        transaction {
            SchemaUtils.createMissingTablesAndColumns(AuthSessions, Tokens, AccountLinks)
        }

        LoginListener()
    }

    override fun onDisable() {
        authServer.app.stop()
    }

    fun connectMariaDB(): Boolean {
        return try {
            val dbConfig = pluginConfig.databaseConfig
            val cfg =
                HikariConfig().apply {
                    driverClassName = "org.mariadb.jdbc.Driver"

                    jdbcUrl = "jdbc:mariadb://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}?useUnicode=true&characterEncoding=utf8"
                    username = dbConfig.username
                    password = dbConfig.password

                    maximumPoolSize = 5
                    addDataSourceProperty("useServerPrepStmts", "true")
                    addDataSourceProperty("cachePrepStmts", "true")
                }

            Database.connect(HikariDataSource(cfg))
            logger.info("✅ MariaDB 연결 성공")
            true
        } catch (ex: Exception) {
            logger.severe("❌ MariaDB 연결 실패: ${ex.message}")
            ex.printStackTrace()
            false
        }
    }
}
