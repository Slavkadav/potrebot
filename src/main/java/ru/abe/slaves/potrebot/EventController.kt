package ru.abe.slaves.potrebot

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.abe.slaves.potrebot.web.model.VkEvent

private const val MESSAGE_TYPE = "message_new"

@RestController
@RequestMapping
class EventController(private val messageProcessingService: MessageProcessingService) {
    private val log = LoggerFactory.getLogger(EventController::class.java)

    @PostMapping
    fun handleEvent(@RequestBody vkEvent: VkEvent): String {
        log.info("received event {}", vkEvent)
        if (MESSAGE_TYPE == vkEvent.type) {
            messageProcessingService.processMessage(vkEvent.content.message)
        }
        return "ok"
    }
}