package kr.doka.lab.discordsync

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kr.lab.doka.discordsync.AuthServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database

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
    }

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        authServer =
            AuthServer(
                DiscordSyncConfig(
                    botToken = config.getString("bot-token") ?: "",
                    database = config.getString("database.database") ?: "",
                    host = config.getString("database.host") ?: "127.0.0.1",
                    port = config.getInt("database.port", 3306),
                    username = config.getString("database.username") ?: "root",
                    password = config.getString("database.password") ?: "",
                ),
            )

        if (!connectMariaDB()) {
            logger.severe("데이터베이스 연결에 실패하여 플러그인을 비활성화합니다.")
            // 안전하게 플러그인 종료
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
    }

    override fun onDisable() {
        authServer.app.stop()
    }

    fun connectMariaDB(): Boolean {
        return try {
            val cfg =
                HikariConfig().apply {
                    driverClassName = "org.mariadb.jdbc.Driver"

                    val host = config.getString("database.host") ?: "127.0.0.1"
                    val port = config.getInt("database.port", 3306)
                    val dbName = config.getString("database.database") ?: "Experiments"
                    val user = config.getString("database.username") ?: "root"
                    val pass = config.getString("database.password") ?: ""

                    jdbcUrl = "jdbc:mariadb://$host:$port/$dbName?useUnicode=true&characterEncoding=utf8"
                    username = user
                    password = pass

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
