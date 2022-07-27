package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class AllChatSummaryWorker(
    private val consumersRepository: ConsumersRepository, private val vkService: VkService
) : Worker {
    override fun regex(): Regex = Regex("${regexPrefix}общая потреба", RegexOption.IGNORE_CASE)

    override fun reactToMessage(vkMessage: VkMessage) {
        countAllChatSpent(vkMessage)
    }

    private fun countAllChatSpent(message: VkMessage) {
        consumersRepository.findSum()
            .subscribe { vkService.sendMessage(message.chatId, "Этот чат напотребил уже на $it. Мда, конечно") }
    }

}