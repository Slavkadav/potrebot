package ru.abe.slaves.potrebot

import lombok.RequiredArgsConstructor
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
    private val allChatSummary = Regex("\\[\\S+]\\W*общая потреба")
    private val allTag = "[@*](все|all)".toRegex(RegexOption.IGNORE_CASE)

    open fun processMessage(vkMessage: VkMessage) {
        val text = vkMessage.text
        if (text.startsWith("\\") || text.startsWith("/")) {
            return
        }
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
        } else if (allChatSummary.containsMatchIn(text)) {
            countAllSpent(vkMessage)
        } else if (allTag.containsMatchIn(text)) {
            reactToAllTag(vkMessage)
        } else {
            vkService.sendMessage(vkMessage.chatId, "Я вас таки не понял. Таки шо вы от меня хотите?")
        }
    }

    private fun reactToAllTag(vkMessage: VkMessage) {
        val userInfo = vkService.loadUserInfo(vkMessage.fromId) ?: return
        vkService.sendMessage(
            vkMessage.chatId,
            "Опять тут @${userInfo.screenName}(${userInfo.firstName}) использует оллтег. Осуждаем."
        )
    }

    private fun countAllSpent(message: VkMessage) {
        consumersRepository.findSum()
            .subscribe { vkService.sendMessage(message.chatId, "Этот чат напотребил уже на $it. Мда, конечно") }
    }

    private fun cancelLastOperation(message: VkMessage) {
        consumersRepository.findFirstByUserIdOrderByAddTimeDesc(message.fromId)
            .mapNotNull { it?.let { consumersRepository.delete(it) } }
            .subscribe { vkService.sendMessage(message.chatId, "Галочка, у нас отмена!") }
    }

    private fun countSpentForAllTime(message: VkMessage) {
        val consumers = consumersRepository.findAllByUserId(message.fromId)
        consumers.map { it.moneySpent }
            .reduce { t, u -> t + u }
            .map { vkService.sendMessage(message.chatId, "За все время ты потребил на $it. Снимаю шляпу!") }
            .subscribe()
    }

    private fun countSpentForMonth(message: VkMessage) {
        val start = LocalDateTime.now().minus(1, ChronoUnit.MONTHS)
        val end = LocalDateTime.now()
        consumersRepository.findAllByUserIdAndAddTimeBetween(message.fromId, start, end)
            .map { it.moneySpent }
            .reduce { t, u -> t + u }
            .subscribe { vkService.sendMessage(message.chatId, "За месяц ты потребил на $it. Достойно!") }
    }

    private fun countSpentForYear(message: VkMessage) {
        val start = LocalDateTime.now().minus(1, ChronoUnit.YEARS)
        val end = LocalDateTime.now()
        consumersRepository.findAllByUserIdAndAddTimeBetween(message.fromId, start, end)
            .map { it.moneySpent }
            .reduce { t, u -> t + u }
            .subscribe { vkService.sendMessage(message.chatId, "За год ты потребил на $it. Вау!") }
    }

    private fun saveMoneySpent(message: VkMessage, moneySpent: Int) {
        val fromId = message.fromId
        consumersRepository.save(Consumer(fromId, moneySpent.toLong()))
            .subscribe { vkService.sendMessage(message.chatId, "Записал") }
    }
}