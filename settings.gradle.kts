plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "DiscordSyncX"
include("discord-sync-core")
include("discord-sync-api")
include("discord-sync-plugin")
include("discord-sync-auth")
