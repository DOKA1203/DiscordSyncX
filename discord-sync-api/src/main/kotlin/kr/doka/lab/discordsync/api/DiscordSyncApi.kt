package kr.doka.lab.discordsync.api

import java.lang.reflect.InvocationTargetException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

interface DiscordSyncApi {
    companion object {
        fun getApi(): DiscordSyncApi {
            val className = "kr.doka.lab.discordsync.DiscordSyncPlugin"
            try {
                val kClass = Class.forName(className).kotlin
                val companionInstance = kClass.companionObjectInstance
                val companionKClass = kClass.companionObject
                val prop = companionKClass!!.memberProperties.find { it.name == "instance" }
                val api: DiscordSyncApi = prop?.getter?.call(companionInstance) as DiscordSyncApi
                return api
            } catch (e: ClassNotFoundException) {
                throw IllegalStateException("DiscordSyncApi class not found: $className")
            } catch (e: InvocationTargetException) {
                throw IllegalStateException("DiscordSyncApi class not found: $className")
            }
        }
    }
}
