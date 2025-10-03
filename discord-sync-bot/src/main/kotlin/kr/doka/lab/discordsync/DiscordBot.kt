package kr.doka.lab.discordsync

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class DiscordBot(private val config: DiscordSyncConfig) {
    lateinit var kord: Kord

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    val coroutineScope: CoroutineScope =
        CoroutineScope(
            SupervisorJob() +
                Executors.newSingleThreadExecutor().asCoroutineDispatcher() +
                CoroutineName("DiscordSyncX-BotScope") +
                CoroutineExceptionHandler { _, e -> println("Coroutine error: $e") },
        )

    init {
        if (config.discordBotConfig.enable) {
            load()
        }
    }

    fun load() {
        coroutineScope.launch {
            kord = Kord(config.discordBotConfig.botToken)
            kord.on<MessageCreateEvent> { // runs every time a message is created that our bot can read

                // ignore other bots, even ourselves. We only serve humans here!
                if (message.author?.isBot != false) return@on

                // check if our command is being invoked
                if (message.content != "!ping") return@on

                // all clear, give them the pong!
                message.channel.createMessage("pong! ${Thread.currentThread().name}")
            }

            kord.login {
                // we need to specify this to receive the content of messages
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    fun stop() {
        coroutineScope.launch {
            kord.shutdown()
            println("Discord Bot Shutting down...")
        }
    }
}
