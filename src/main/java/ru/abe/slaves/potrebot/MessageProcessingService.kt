package ru.abe.slaves.potrebot

import lombok.RequiredArgsConstructor
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import ru.abe.slaves.potrebot.domain.model.Consumer
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
@RequiredArgsConstructor
open class MessageProcessingService(
    private val vkService: VkService,
    private val consumersRepository: ConsumersRepository
) {
    private val spentPattern = Regex("\\[\\S+]\\W*(\\d+)")
    private val sumPattern = Regex("\\[\\S+]\\W*сколько?", RegexOption.IGNORE_CASE)
    private val sumMonthPattern = Regex("\\[\\S+]\\W*сколько за месяц?", RegexOption.IGNORE_CASE)
    private val sumYearPattern = Regex("\\[\\S+]\\W*сколько за год?", RegexOption.IGNORE_CASE)
    private val cancelPattern = Regex("\\[\\S+]\\W*отмена")

    @Async
    open fun processMessage(vkMessage: VkMessage) {
        val text = vkMessage.text
        if (spentPattern.containsMatchIn(text)) {
            val matchResult = spentPattern.find(text)
            matchResult!!.groups[1]?.let { saveMoneySpent(vkMessage, it.value.toInt()) }
        } else if (sumYearPattern.containsMatchIn(text)) {
            countSpentForYear(vkMessage)
        } else if (sumMonthPattern.containsMatchIn(text)) {
            countSpentForMonth(vkMessage)
        } else if (sumPattern.containsMatchIn(text)) {
            countSpentForAllTime(vkMessage)
        } else if (cancelPattern.containsMatchIn(text)) {
            cancelLastOperation(vkMessage)
        } else {
            vkService.sendMessage(vkMessage.chatId, "Я вас таки не понял. Таки шо вы от меня хотите?")
        }
    }

    private fun cancelLastOperation(message: VkMessage) {
        val lastOperation = consumersRepository.findFirstByUserIdOrderByAddTimeDesc(message.fromId)
        lastOperation?.let { consumersRepository.delete(lastOperation) }
        vkService.sendMessage(message.chatId, "Галочка, у нас отмена!")
    }

    private fun countSpentForAllTime(message: VkMessage) {
        val consumers = consumersRepository.findAllByUserId(message.fromId)
        val sum = consumers.sumOf { it.moneySpent }
        vkService.sendMessage(message.chatId, "За все время ты потребил на $sum. Снимаю шляпу!")
    }

    private fun countSpentForMonth(message: VkMessage) {
        val start = LocalDateTime.now().minus(1, ChronoUnit.MONTHS)
        val end = LocalDateTime.now()
        val consumers = consumersRepository.findAllByUserIdAndAddTimeBetween(message.fromId, start, end)
        val sum = consumers.sumOf { it.moneySpent }
        vkService.sendMessage(message.chatId, "За месяц ты потребил на $sum. Достойно!")
    }

    private fun countSpentForYear(message: VkMessage) {
        val start = LocalDateTime.now().minus(1, ChronoUnit.YEARS)
        val end = LocalDateTime.now()
        val consumers = consumersRepository.findAllByUserIdAndAddTimeBetween(message.fromId, start, end)
        val sum = consumers.sumOf { it.moneySpent }
        vkService.sendMessage(message.chatId, "За год ты потребил на $sum. Вау!")
    }

    private fun saveMoneySpent(message: VkMessage, moneySpent: Int) {
        val fromId = message.fromId
        consumersRepository.save(Consumer(fromId, moneySpent.toLong()))
        vkService.sendMessage(message.chatId, "Записал")
    }
}