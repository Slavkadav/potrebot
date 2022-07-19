package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class BelyaoNoWorker(private val vkService: VkService) : Worker {
    override fun regex(): Regex = Regex("${regexPrefix}нет")

    override fun reactToMessage(vkMessage: VkMessage) {
        vkService.sendMessage(vkMessage.chatId, null, "doc-204764107_645286502")
    }
}