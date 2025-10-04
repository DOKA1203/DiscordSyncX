package kr.doka.lab.discordsync.api

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.doka.lab.discordsync.exposed.repositories.AccountLinkRepository
import kr.doka.lab.discordsync.exposed.repositories.TokenRepository
import java.util.UUID

class DiscordUserImpl(override val uuid: UUID) : DiscordUser {
    companion object {
        private val accountLinkRepository = AccountLinkRepository()
        private val tokenRepository = TokenRepository()

        private val coreScope =
            CoroutineScope(
                SupervisorJob() +
                    Dispatchers.Default +
                    CoroutineName("DiscordSyncCoreScope") +
                    CoroutineExceptionHandler { _, e -> println("DiscordSyncCoreScope - Coroutine error: $e") },
            )
    }

    private var _discordId: String = ""

    override val discordId
        get() = _discordId

    override fun unlink() {
        coreScope.launch {
            withContext(Dispatchers.IO) {
                accountLinkRepository.unlink(uuid)
            }
        }
    }

    init {
        coreScope.launch {
            val accountLink =
                withContext(Dispatchers.IO) {
                    accountLinkRepository.findByMinecraftUuid(uuid)
                }
            if (accountLink.isNotEmpty()) {
                _discordId = accountLink[0].discordUserId.toString()
            }
        }
    }
}
