package kr.doka.lab.discordsync.paper

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kr.doka.lab.discordsync.AuthConfig
import kr.doka.lab.discordsync.DatabaseConfig
import kr.doka.lab.discordsync.DiscordSyncConfig
import kr.doka.lab.discordsync.exposed.tables.AccountLinks
import kr.doka.lab.discordsync.exposed.tables.AuthSessions
import kr.doka.lab.discordsync.exposed.tables.Tokens
import kr.doka.lab.discordsync.paper.listeners.LoginListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DiscordSyncPaperPlugin : JavaPlugin() {
    val pluginScope: CoroutineScope =
        CoroutineScope(
            SupervisorJob() +
                Dispatchers.Default +
                CoroutineName("DiscordSyncX-PluginScope") +
                CoroutineExceptionHandler { _, e -> logger.severe("DiscordSyncX-PluginScope - Coroutine error: $e") },
        )

    companion object {
        lateinit var instance: DiscordSyncPaperPlugin
        lateinit var pluginConfig: DiscordSyncConfig
    }

    override fun onEnable() {
        instance = this

        saveDefaultConfig()
        pluginConfig = loadConfig()

        if (!connectMariaDB()) {
            logger.severe("데이터베이스 연결에 실패하여 플러그인을 비활성화합니다.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        transaction {
            SchemaUtils.createMissingTablesAndColumns(AuthSessions, Tokens, AccountLinks)
        }

        LoginListener()
    }

    override fun onDisable() {
    }

    fun loadConfig() =
        DiscordSyncConfig(
            databaseConfig =
                DatabaseConfig(
                    database = config.getString("database.database", "Experiments")!!,
                    host = config.getString("database.host", "localhost")!!,
                    port = config.getInt("database.port", 3306),
                    username = config.getString("database.username", "root")!!,
                    password = config.getString("database.password", "root")!!,
                ),
            authConfig =
                AuthConfig(
                    url = config.getString("auth.url")!!,
                ),
        )

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
            logger.info("MariaDB 연결 성공")
            true
        } catch (ex: Exception) {
            logger.severe("MariaDB 연결 실패: ${ex.message}")
            ex.printStackTrace()
            false
        }
    }
}
