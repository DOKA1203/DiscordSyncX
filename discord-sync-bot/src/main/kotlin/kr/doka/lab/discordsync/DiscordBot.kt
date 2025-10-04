package kr.doka.lab.discordsync

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.concurrent.Executors

class DiscordBot(private val config: DiscordSyncConfig) {
    lateinit var jda: JDA
    val coroutineScope: CoroutineScope =
        CoroutineScope(
            SupervisorJob() +
                Executors.newSingleThreadExecutor().asCoroutineDispatcher() +
                CoroutineName("DiscordSyncX-BotScope") +
                CoroutineExceptionHandler { _, e -> println("\"DiscordSyncX-BotScope\" - Coroutine error: $e") },
        )

    init {
        if (config.discordBotConfig.enable) {
            load()
        }
    }

    fun load() {
        coroutineScope.launch {
            jda =
                JDABuilder.createDefault(config.discordBotConfig.botToken)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setEventManager(AnnotatedEventManager())
                    .addEventListeners(DiscordEventListener())
                    .build()
        }
    }

    fun stop() {
        jda.shutdown()
    }

    private class DiscordEventListener {
        @SubscribeEvent
        fun ohHeyAMessage(event: MessageReceivedEvent) {
            if (event.author.isBot) return
            if (event.message.contentDisplay == "stop") {
                event.jda.shutdown()
            }
            event.message.reply("Here is ${Thread.currentThread().name}").queue()
            println(event.message.contentDisplay)
        }
    }
}
