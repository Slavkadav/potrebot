package ru.abe.slaves.potrebot.workers

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class SumYearWorker(
    private val consumersRepository: ConsumersRepository,
    private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}сколько за год", RegexOption.IGNORE_CASE)

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        countSpentForYear(vkMessage)
    }

    private suspend fun countSpentForYear(message: VkMessage) {
        val start = LocalDateTime.now().minus(1, ChronoUnit.YEARS)
        val end = LocalDateTime.now()
        val moneySpent = consumersRepository.findAllByUserIdAndAddTimeBetween(message.fromId, start, end).asFlow()
            .map { it.moneySpent }
            .reduce { t, u -> t + u }
        vkService.sendMessage(message.chatId, "За год ты потребил на $moneySpent. Вау!")
    }
}