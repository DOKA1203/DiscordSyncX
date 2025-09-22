package kr.doka.lab.discordsync.discord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
    @SerialName("me")
    val me: Me? = null,
    @SerialName("tokens")
    val tokens: Tokens? = null,
) {
    @Serializable
    data class Me(
        @SerialName("accent_color")
        val accentColor: Long? = null,
        @SerialName("avatar")
        val avatar: String? = null,
        @SerialName("banner_color")
        val bannerColor: String? = null,
        @SerialName("discriminator")
        val discriminator: String? = null,
        @SerialName("email")
        val email: String? = null,
        @SerialName("flags")
        val flags: Long? = null,
        @SerialName("global_name")
        val globalName: String? = null,
        @SerialName("id")
        val id: String? = null,
        @SerialName("locale")
        val locale: String? = null,
        @SerialName("mfa_enabled")
        val mfaEnabled: Boolean? = null,
        @SerialName("premium_type")
        val premiumType: Long? = null,
        @SerialName("public_flags")
        val publicFlags: Long? = null,
        @SerialName("username")
        val username: String? = null,
        @SerialName("verified")
        val verified: Boolean? = null,
    )

    @Serializable
    data class Tokens(
        @SerialName("access_token")
        val accessToken: String? = null,
        @SerialName("expires_in")
        val expiresIn: Long? = null,
        @SerialName("refresh_token")
        val refreshToken: String? = null,
        @SerialName("scope")
        val scope: String? = null,
        @SerialName("token_type")
        val tokenType: String? = null,
    )
}
