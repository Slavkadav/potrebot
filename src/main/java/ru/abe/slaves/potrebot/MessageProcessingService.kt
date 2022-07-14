package ru.abe.slaves.potrebot

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import ru.abe.slaves.potrebot.web.model.VkMessage
import ru.abe.slaves.potrebot.workers.Worker


@Service
@RequiredArgsConstructor
open class MessageProcessingService(
    private val vkService: VkService,
    private val workers: List<Worker>
) {
    open fun processMessage(vkMessage: VkMessage) {
        val text = vkMessage.text
        if (text.startsWith("\\") || text.startsWith("/")) {
            return
        }

        workers.forEach {
            if (it.regex().containsMatchIn(text)) {
                it.reactToMessage(vkMessage)
                return
            }
        }

        vkService.sendMessage(vkMessage.chatId, "Я вас таки не понял. Таки шо вы от меня хотите?")
    }
}