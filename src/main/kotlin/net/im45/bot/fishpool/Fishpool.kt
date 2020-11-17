package net.im45.bot.fishpool

import com.google.auto.service.AutoService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregisterAllCommands
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import kotlin.math.abs

@AutoService(JvmPlugin::class)
object Fishpool : KotlinPlugin(
        JvmPluginDescription(
                "net.im45.bot.fishpool",
                "0.2.1"
        )
) {
    override fun onEnable() {
        super.onEnable()

        OhCmd.register()
        NaCmd.register()
        PaCmd.register()
        Errcode.register()
    }

    override fun onDisable() {
        super.onDisable()

        unregisterAllCommands(this)
    }
}

object OhCmd : SimpleCommand(
        Fishpool, "oh"
) {
    @Handler
    suspend fun UserCommandSender.oh(h: Short) {
        val hs = "H".repeat(abs(h.toInt()))
        sendMessage(if (h > 0) "O${hs}" else "${hs}O")
    }

    @Handler
    suspend fun UserCommandSender.oh() = oh(16)
}

object NaCmd : SimpleCommand(
        Fishpool, "na"
) {
    @Handler
    suspend fun UserCommandSender.na(n: Short) {
        val ns = "呐".repeat(abs(n.toInt()))
        if (ns.isNotEmpty()) sendMessage(ns)
    }

    @Handler
    suspend fun UserCommandSender.na() = na(2)
}

object PaCmd : SimpleCommand(
        Fishpool, "pa"
) {
    @Handler
    suspend fun UserCommandSender.pa(p: Short) {
        val ps = "爬".repeat(abs(p.toInt()))
        if (ps.isNotEmpty()) sendMessage(ps)
    }

    @Handler
    suspend fun UserCommandSender.pa() = pa(1)
}

object Errcode : SimpleCommand(
        Fishpool, "errcode",
        description = "err.code"
) {
    private val errcode = URL("https://dev.zapic.moe/err.code")
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            connectTimeoutMillis = ErrcodeConfig.connectTimeout
            requestTimeoutMillis = ErrcodeConfig.requestTimeout
            socketTimeoutMillis = ErrcodeConfig.socketTimeout
        }
    }

    private class LJYYSException(host: String) : Exception(host)

    private object ErrcodeConfig : AutoSavePluginConfig("errcode") {
        val connectTimeout by value(10000L)
        val requestTimeout by value(30000L)
        val socketTimeout by value(10000L)
    }

    private fun stackTraceFormatter(ex: Throwable, limit: Int = 3) = buildString {
        append("Exception in thread \"${Thread.currentThread().name}\": $ex")
        ex.stackTrace.let {
            it.take(limit).forEach { trace -> append("\n\tat $trace") }
            if (it.size > limit) append("\n\t... ${it.size - limit} more")
        }
    }

    @Handler
    suspend fun UserCommandSender.errcode() {
        client.runCatching {
            get<String>(errcode)
        }.onFailure {
            sendMessage(stackTraceFormatter(it))
        }.onSuccess { ec ->
            Socket().runCatching {
                use { connect(InetSocketAddress(ec, 25565), 500) }
            }.let { res ->
                val out = stackTraceFormatter(LJYYSException(if (res.isFailure) "Closed" else ec))
                sendMessage(out)
            }
        }
    }
}

