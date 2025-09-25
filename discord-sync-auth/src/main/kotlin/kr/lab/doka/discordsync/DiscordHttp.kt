package kr.lab.doka.discordsync

import com.github.kittinunf.fuel.Fuel
import kotlinx.serialization.json.Json
import kr.doka.lab.discordsync.DiscordSyncConfig
import kr.doka.lab.discordsync.discord.DiscordUser

class DiscordHttp(val config: DiscordSyncConfig) {
    val json = Json { ignoreUnknownKeys = true }

    fun exchangeCodeForToken(code: String): DiscordUser.Tokens {
        // Fuel은 params를 넘기면 기본적으로 x-www-form-urlencoded로 인코딩합니다.
        val params =
            listOf(
                "client_id" to config.discordApiConfig.clientId,
                "client_secret" to config.discordApiConfig.clientSecret,
                "grant_type" to "authorization_code",
                "code" to code,
                "redirect_uri" to config.discordApiConfig.discordRedirectUrl,
            )

        val (_, response, result) =
            Fuel.post(config.discordApiConfig.discordTokenUrl, params)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .responseString()

        val status = response.statusCode
        val body =
            result.fold(
                success = { it },
                failure = { err ->
                    // FuelError에도 response가 있으므로 가능하면 서버 메시지를 꺼내서 던집니다.
                    val serverMsg = err.response.body().asString("application/json")
                    throw IllegalStateException("Token request failed ($status): $serverMsg")
                },
            )

        if (status !in 200..299) {
            // 성공으로 안 왔는데도 result가 success일 수 있으니 상태로 한 번 더 체크
            throw IllegalStateException("Token request failed ($status): $body")
        }

        return json.decodeFromString<DiscordUser.Tokens>(body)
    }

    fun fetchDiscordMe(accessToken: String): DiscordUser.Me {
        val url = "https://discord.com/api/users/@me"

        val (_, response, result) =
            Fuel.get(url)
                .header("Authorization" to "Bearer $accessToken")
                .responseString()

        val status = response.statusCode
        val body =
            result.fold(
                success = { it },
                failure = { err ->
                    val serverMsg = err.response.body().asString("application/json")
                    throw IllegalStateException("Discord /@me request failed ($status): $serverMsg")
                },
            )

        if (status !in 200..299) {
            throw IllegalStateException("Discord /@me request failed ($status): $body")
        }

        return json.decodeFromString(body)
    }
}
