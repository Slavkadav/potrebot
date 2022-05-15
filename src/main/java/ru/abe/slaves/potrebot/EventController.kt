package ru.abe.slaves.potrebot

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.abe.slaves.potrebot.web.model.VkEvent
import java.util.concurrent.ConcurrentHashMap

private const val MESSAGE_TYPE = "message_new"

@RestController
@RequestMapping
class EventController(private val messageProcessingService: MessageProcessingService) {
    private val eventsCache: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap()

    private val log = LoggerFactory.getLogger(EventController::class.java)

    @PostMapping
    fun handleEvent(@RequestBody vkEvent: VkEvent): String = runBlocking{
        if (eventsCache.containsKey(vkEvent.eventId)) {
            return@runBlocking "ok"
        }
        launch {
            eventsCache[vkEvent.eventId] = true
            log.info("received event {}", vkEvent)
            if (MESSAGE_TYPE == vkEvent.type) {
                messageProcessingService.processMessage(vkEvent.content.message)
            }
            eventsCache.remove(vkEvent.eventId)
        }
        return@runBlocking "ok"
    }

    @GetMapping
    fun handleGet(): String {
        return "ok"
    }
}