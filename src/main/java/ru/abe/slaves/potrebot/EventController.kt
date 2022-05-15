package ru.abe.slaves.potrebot

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ru.abe.slaves.potrebot.web.model.VkEvent

private const val MESSAGE_TYPE = "message_new"

@RestController
@RequestMapping
class EventController(
    private val messageProcessingService: MessageProcessingService
) {
    private val log = LoggerFactory.getLogger(EventController::class.java)

    @PostMapping
    fun handleEvent(@RequestBody vkEvent: VkEvent): Mono<String> {
        log.info("received event {}", vkEvent)
        if (MESSAGE_TYPE == vkEvent.type) {
            return Mono.just("ok")
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess { messageProcessingService.processMessage(vkEvent.content.message) }
        }
        return Mono.just("ok");
    }

    @GetMapping
    fun handleGet(): String {
        return "ok"
    }
}