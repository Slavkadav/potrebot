package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class FaqWorker(private val vkService: VkService) : Worker {
    override fun regex(): Regex = Regex("${regexPrefix}фак", RegexOption.IGNORE_CASE)

    override fun reactToMessage(vkMessage: VkMessage) {
        faqMessage(vkMessage)
    }

    private fun faqMessage(vkMessage: VkMessage) {
        val faq="""
            (П\п)отреба (число) - записывает личную потребу
            (О\о)тмена - отменяет последнюю отмену
            мяу - скидывает гифку кота
            (П\п)отребкуб - кидает д6 на потребу
            (К\к)убас X(Д\д\К\к\D\d)Y - кидает Х кубов У граней
            (С\с)колько - выдает сколько твоя личная потреба
            (О\о)бщая потреба - выдает оющую потребу
            нет - присылает нет
            (С\с)колько за месяц - выдает сколько потребил за месяц
            (С\с)колько за год - выдает сколько потребил за год
        """
        vkService.sendMessage(vkMessage.chatId, faq)
    }
}