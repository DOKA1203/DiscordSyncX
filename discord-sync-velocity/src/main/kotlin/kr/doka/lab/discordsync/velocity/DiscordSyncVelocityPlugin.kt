package kr.doka.lab.discordsync.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin

@Plugin(id = "discord-sync-x", name = "DiscordSyncX", version = "1.0.0")
class DiscordSyncVelocityPlugin {
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
    }

    @Subscribe
    fun onChatEvent(event: PlayerChatEvent) {
        event.message
    }
}
