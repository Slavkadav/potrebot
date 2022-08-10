package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class PotrebaDiceWorker(private val vkService: VkService) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}потребкуб", RegexOption.IGNORE_CASE)

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        throwPotrebaDice(vkMessage)
    }

    private fun throwPotrebaDice(vkMessage: VkMessage) {
        val diceGif = when ((1..6).random()) {
            1 -> "doc-204764107_645402083"
            2 -> "doc-204764107_645402099"
            3 -> "doc-204764107_645402110"
            4 -> "doc-204764107_645402136"
            5 -> "doc-204764107_645402310"
            6 -> "doc-204764107_645402343"

            else -> "doc-204764107_645402083"
        }
        vkService.sendMessage(vkMessage.chatId, null, diceGif)
    }
}