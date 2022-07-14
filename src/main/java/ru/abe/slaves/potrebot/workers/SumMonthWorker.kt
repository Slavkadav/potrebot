package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class SumMonthWorker(
    private val consumersRepository: ConsumersRepository,
    private val vkService: VkService
) : Worker {
    override fun regex(): Regex = Regex("${regexPrefix}сколько за месяц?", RegexOption.IGNORE_CASE)

    override fun reactToMessage(vkMessage: VkMessage) {
        countSpentForMonth(vkMessage)
    }

    private fun countSpentForMonth(message: VkMessage) {
        val start = LocalDateTime.now().minus(1, ChronoUnit.MONTHS)
        val end = LocalDateTime.now()
        consumersRepository.findAllByUserIdAndAddTimeBetween(message.fromId, start, end)
            .map { it.moneySpent }
            .reduce { t, u -> t + u }
            .subscribe { vkService.sendMessage(message.chatId, "За месяц ты потребил на $it. Достойно!") }
    }
}