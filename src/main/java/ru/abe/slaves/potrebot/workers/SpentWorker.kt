package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.model.Consumer
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class SpentWorker(
    private val consumersRepository: ConsumersRepository,
    private val vkService: VkService
) : Worker {
    override fun regex(): Regex = Regex("$regexPrefix(\\d+)")

    override fun reactToMessage(vkMessage: VkMessage) {
        val matchResult = regex().find(vkMessage.text)
        matchResult!!.groups[1]?.let { saveMoneySpent(vkMessage, it.value.toInt()) }
    }

    private fun saveMoneySpent(message: VkMessage, moneySpent: Int) {
        val fromId = message.fromId
        consumersRepository.save(Consumer(fromId, moneySpent.toLong()))
            .subscribe { vkService.sendMessage(message.chatId, "Записал") }
    }
}