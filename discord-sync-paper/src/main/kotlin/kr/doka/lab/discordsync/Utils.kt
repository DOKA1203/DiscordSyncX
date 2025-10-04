package kr.doka.lab.discordsync

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> suspendSync(
    plugin: Plugin,
    task: () -> T,
): T =
    withTimeout(10000L) {
        // Context: The current coroutine context
        suspendCancellableCoroutine { cont ->
            // Context: The current coroutine context
            Bukkit.getScheduler().runTask(
                plugin,
                Runnable {
                    // Context: Bukkit MAIN thread
                    // runCatching is used to forward any exception that may occur here back to
                    // our coroutine, keeping the exception transparency of Kotlin coroutines
                    runCatching(task).fold({ cont.resume(it) }, cont::resumeWithException)
                },
            )
        }
    }
