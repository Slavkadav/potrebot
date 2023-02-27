package ru.abe.slaves.potrebot

import com.google.gson.Gson
import com.vk.api.sdk.exceptions.LongPollServerKeyExpiredException
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import ru.abe.slaves.potrebot.web.model.VkEvent
import java.util.concurrent.TimeUnit

const val MESSAGE_TYPE = "message_new"

@Service
class LongPollService(
    private val vkService: VkService,
    private val messageProcessingService: MessageProcessingService
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(LongPollService::class.java)
    private val gson = Gson()

    override fun run(vararg args: String?): Unit = runBlocking{
        vkService.getLongPollServer()?.also {
            var ts = it.ts
            while (true) {
                try {
                    val longPollServerResponse = vkService.longPoll(it.server, it.key, ts)!!
                    ts = longPollServerResponse.ts
                    val events = longPollServerResponse.let { response ->
                        response.updates.map { update ->
                            gson.fromJson(update, VkEvent::class.java)
                        }
                    }
                    events.forEach { event ->
                        if (MESSAGE_TYPE == event.type) {
                            messageProcessingService.processMessage(event.content.message)
                        }
                    }
                } catch (e: LongPollServerKeyExpiredException) {
                    // по истечении ключа перезапускаем процесс
                    run(*args)
                }
                catch (e: Exception) {
                    log.error("Error on execution ", e)
                    TimeUnit.SECONDS.sleep(10)
                }
            }
        }
    }
}