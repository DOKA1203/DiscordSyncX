package kr.doka.lab.discordsync.listeners

import io.papermc.paper.dialog.Dialog
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.type.DialogType
import kr.doka.lab.discordsync.DiscordSyncPlugin.Companion.instance
import kr.doka.lab.discordsync.api.AuthStatus
import kr.doka.lab.discordsync.exposed.repositories.AccountLinkRepository
import kr.doka.lab.discordsync.exposed.repositories.AuthSessionRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.jetbrains.exposed.sql.transactions.transaction

class LoginListener : Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, instance)
    }

    private val sessionRepository = AuthSessionRepository()
    private val linkRepository = AccountLinkRepository()

    @EventHandler
    fun onPlayerConfigure(event: AsyncPlayerConnectionConfigureEvent) {
        if (event.connection.profile.id == null) return
        val link = linkRepository.findByMinecraftUuid(event.connection.profile.id!!).getOrNull(0)
        if (link != null) return

        val state = sessionRepository.create(event.connection.profile.id!!)

        val dialog =
            Dialog.create { builder ->
                builder.empty()
                    .base(
                        DialogBase.builder(Component.text("DiscordSyncX by DOKA1203", NamedTextColor.LIGHT_PURPLE))
                            .canCloseWithEscape(false)
                            .body(
                                listOf(
                                    DialogBody.plainMessage(Component.text(" ")),
                                    DialogBody.plainMessage(Component.text(" ")),
                                    DialogBody.plainMessage(Component.text("우리 서버에 들어오기 위해선 디스코드 인증을 완료해야 합니다.")),
                                ),
                            )
                            .build(),
                    ).type(
                        DialogType.multiAction(
                            listOf(
                                ActionButton.builder(Component.text("인증하기", TextColor.color(0xFF8B8E)))
                                    .tooltip(Component.text("클릭하여 인증하세요"))
                                    .action(
                                        DialogAction.staticAction(
                                            ClickEvent.openUrl("http://127.0.0.1:8080/authentication/${state.state}"),
                                        ),
                                    )
                                    .build(),
                            ),
                        ).build(),
                    )
            }

        var isCompleted = false
        event.connection.audience.showDialog(dialog)
        loop@ for (i in 0..10) {
            transaction {
                val auth = sessionRepository.findByState(state.state)!!
                if (auth.status == AuthStatus.COMPLETED) isCompleted = true
            }
            if (isCompleted) break@loop
            Thread.sleep(6000)
        }

        if (isCompleted) return
        event.connection.disconnect(Component.text("인증 시간이 만료되었습니다.", NamedTextColor.RED))
    }
}
