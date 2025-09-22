package kr.lab.doka.discordsync

import io.javalin.Javalin
import io.javalin.http.HttpStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kr.doka.lab.discordsync.DiscordSyncConfig
import kr.doka.lab.discordsync.api.dto.Token
import kr.doka.lab.discordsync.discord.DiscordUser
import kr.doka.lab.discordsync.exposed.repositories.AccountLinkRepository
import kr.doka.lab.discordsync.exposed.repositories.AuthSessionRepository
import kr.doka.lab.discordsync.exposed.repositories.TokenRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.concurrent.Executors

class AuthServer(val config: DiscordSyncConfig) {
    val app: Javalin =
        Javalin.create { cfg ->
            cfg.http.defaultContentType = "application/json"
            cfg.showJavalinBanner = false
        }.start(8080)
    val serverDispatcher =
        Executors.newSingleThreadExecutor { r ->
            Thread(r, "javalin-coroutine-thread").apply { isDaemon = false }
        }.asCoroutineDispatcher()

    val serverScope =
        CoroutineScope(
            SupervisorJob() +
                serverDispatcher +
                CoroutineName("DiscordSyncScope") +
                CoroutineExceptionHandler { _, e -> println("Coroutine error: $e") },
        )

    val sessionRepository = AuthSessionRepository()
    val accountLinkRepository = AccountLinkRepository()
    val tokenRepository = TokenRepository()

    val scopes = listOf("identify", "email", "guilds.join")

    val discordHttp = DiscordHttp(config)

    fun url(v: String): String = URLEncoder.encode(v, StandardCharsets.UTF_8)

    init {
        app.get("/") { ctx ->
            val user = ctx.sessionAttribute<DiscordUser>("user")
            ctx.json(mapOf("logged_in" to (user != null), "user" to user))
        }

        // 인증 시작
        app.get("/authentication/{state}") { ctx ->
            val state = ctx.pathParam("state")
            ctx.sessionAttribute("oauth_state", state)

            sessionRepository.findByState(state)

            val params =
                mapOf(
                    "client_id" to config.discordClientId,
                    "redirect_uri" to config.discordRedirectUrl,
                    "response_type" to "code",
                    "scope" to scopes.joinToString(" "),
                    "state" to state,
                    "prompt" to "consent",
                )
            val qs = params.entries.joinToString("&") { (k, v) -> "$k=${url(v)}" }
            ctx.redirect("${config.discordAuthUrl}?$qs")
        }

        // 콜백 처리
        app.get("/callback") { ctx ->
            val error = ctx.queryParam("error")
            if (error != null) {
                ctx.status(HttpStatus.BAD_REQUEST).json(mapOf("error" to error))
                return@get
            }
            val state = ctx.queryParam("state")
            val code = ctx.queryParam("code")
            val expected = ctx.sessionAttribute<String>("oauth_state")
            if (code.isNullOrBlank() || state.isNullOrBlank() || expected == null || expected != state) {
                ctx.status(HttpStatus.BAD_REQUEST).json(mapOf("error" to "Invalid state or code"))
                return@get
            }

            val tokens = discordHttp.exchangeCodeForToken(code = code)
            val accessToken = tokens.accessToken
            val me = discordHttp.fetchDiscordMe(accessToken!!)

            val found = sessionRepository.findByState(state)
            val link = accountLinkRepository.link(found!!.mcUuid, me.id!!.toLong())
            tokenRepository.saveForAccountLink(
                accountLinkId = link.id,
                token =
                    Token(
                        accessToken = tokens.accessToken!!,
                        refreshToken = tokens.refreshToken!!,
                        tokenType = "Bearer",
                        expiresAt = Instant.now().plusSeconds(3600),
                        createdAt = Instant.now(),
                        updatedAt = Instant.now(),
                        accountLinkId = link.id,
                    ),
            )

            // 3) 세션 완료
            sessionRepository.markCompleted(state)

            ctx.sessionAttribute("user", DiscordUser(me, tokens))
            ctx.redirect("/")
        }

        serverScope.launch {
            app.start(7000)
        }
    }
}
