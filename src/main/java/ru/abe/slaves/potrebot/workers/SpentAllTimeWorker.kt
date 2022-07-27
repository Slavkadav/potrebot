package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class SpentAllTimeWorker(
    private val consumersRepository: ConsumersRepository,
    private val vkService: VkService
) : Worker {

    override fun regex(): Regex = Regex("${regexPrefix}сколько", RegexOption.IGNORE_CASE)

    override fun reactToMessage(vkMessage: VkMessage) {
        countSpentForAllTime(vkMessage)
    }

    private fun countSpentForAllTime(message: VkMessage) {
        val consumers = consumersRepository.findAllByUserId(message.fromId)
        consumers.map { it.moneySpent }
            .reduce { t, u -> t + u }
            .map { vkService.sendMessage(message.chatId, "За все время ты потребил на $it. Снимаю шляпу!") }
            .subscribe()
    }
}