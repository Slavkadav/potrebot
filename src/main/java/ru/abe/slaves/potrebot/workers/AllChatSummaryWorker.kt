package ru.abe.slaves.potrebot.workers

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class AllChatSummaryWorker(
    private val consumersRepository: ConsumersRepository, private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}общая потреба", RegexOption.IGNORE_CASE)

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        countAllChatSpent(vkMessage)
    }

    private suspend fun countAllChatSpent(message: VkMessage) {
        vkService.sendMessage(
            message.chatId,
            "Этот чат напотребил уже на ${consumersRepository.findSum().awaitSingle()}. Мда, конечно"
        )
    }

}