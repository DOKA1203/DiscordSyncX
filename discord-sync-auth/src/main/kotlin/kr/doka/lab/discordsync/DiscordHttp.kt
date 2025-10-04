package kr.doka.lab.discordsync

import com.github.kittinunf.fuel.Fuel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kr.doka.lab.discordsync.discord.DiscordUser

class DiscordHttp(val config: DiscordSyncConfig) {
    val json = Json { ignoreUnknownKeys = true }

    @Serializable
    data class AddGuildMemberBody(
        val access_token: String,
        val nick: String? = null,
        val roles: List<String>? = null,
        val mute: Boolean? = null,
        val deaf: Boolean? = null,
    )

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

    fun addUserToGuild(
        botToken: String,
        guildId: String,
        userId: String,
        userAccessToken: String,
        nick: String? = null,
        roles: List<String>? = null,
        mute: Boolean? = null,
        deaf: Boolean? = null,
    ) {
        val url = "https://discord.com/api/v10/guilds/$guildId/members/$userId"

        val body =
            AddGuildMemberBody(
                access_token = userAccessToken,
                nick = nick,
                roles = roles,
                mute = mute,
                deaf = deaf,
            )

        val jsonBody = json.encodeToString(body)

        // (디버깅) curl 로 확인하고 싶다면:
        // println(Fuel.put(url).header("Authorization" to "Bot $botToken").body(jsonBody).cUrlString())

        val (request, response, result) =
            Fuel.put(url)
                .header(
                    "Authorization" to "Bot $botToken",
                    "Content-Type" to "application/json",
                )
                .body(jsonBody)
                .responseString() // 간단히 문자열로 응답 받음

        when (response.statusCode) {
            201 -> {
                // 새로 생성된 길드 멤버 오브젝트가 body로 반환됨
                println("멤버 추가 완료 (201). 응답 바디:")
                println(result.get())
            }
            204 -> {
                // 이미 멤버로 존재
                println("이미 길드 멤버(204).")
            }
            400 -> {
                println("잘못된 요청 (400). 요청 바디/파라미터를 확인하세요.")
            }
            401 -> {
                println("인증 실패 (401). 봇 토큰 또는 유저 access token 이 유효한지 확인하세요.")
            }
            403 -> {
                println("권한 없음 (403). 봇이 길드에 존재하는지, 필요한 권한(create instant invite 등)을 가지고 있는지 확인하세요.")
            }
            429 -> {
                println("Rate limited (429). 응답 헤더의 재시도-시간을 확인하고 재시도하세요.")
            }
            else -> {
                println("응답 코드: ${response.statusCode}")
            }
        }
    }
}
