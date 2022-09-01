package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage
import java.time.LocalDateTime

@Component
class MeowWorker(
    private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}мяу")

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        reactToMeow(vkMessage)
    }

    private fun reactToMeow(vkMessage: VkMessage) {
        val now = LocalDateTime.now()
        if (now.month.value == 9 && now.dayOfMonth == 3) {
            vkService.sendMessage(vkMessage.chatId, null, "doc-204764107_648741118")
        }
        vkService.sendMessage(vkMessage.chatId, null, "doc-204764107_644937145")
    }
}