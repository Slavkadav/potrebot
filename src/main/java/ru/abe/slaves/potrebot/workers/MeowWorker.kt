package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class MeowWorker(
    private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}мяу")

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        reactToMeow(vkMessage)
    }

    private fun reactToMeow(vkMessage: VkMessage) {
        vkService.sendMessage(vkMessage.chatId, null, "doc-204764107_644937145")
    }
}