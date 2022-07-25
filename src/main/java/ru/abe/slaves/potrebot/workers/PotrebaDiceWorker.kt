package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage
import kotlin.random.Random

@Component
class PotrebaDiceWorker(private val vkService: VkService) : Worker {
    override fun regex(): Regex = Regex("${regexPrefix}[Пп]отребкуб", RegexOption.IGNORE_CASE)

    override fun reactToMessage(vkMessage: VkMessage) {
        throwPotrebaDice(vkMessage)
    }

    private fun throwPotrebaDice(vkMessage: VkMessage) {
        val diceGif = when (Random.nextInt(5)) {
            0 -> "doc-204764107_645402083"
            1 -> "doc-204764107_645402099"
            2 -> "doc-204764107_645402110"
            3 -> "doc-204764107_645402136"
            4 -> "doc-204764107_645402310"
            5 -> "doc-204764107_645402343"

            else -> "doc-204764107_645402083"
        }
        vkService.sendMessage(vkMessage.chatId, null, diceGif)
    }
}