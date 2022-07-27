package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class CancelWorker(
    private val consumersRepository: ConsumersRepository, private val vkService: VkService
) : Worker {
    override fun regex(): Regex = Regex("${regexPrefix}отмена")

    override fun reactToMessage(vkMessage: VkMessage) {
        cancelLastOperation(vkMessage)
    }

    private fun cancelLastOperation(message: VkMessage) {
        consumersRepository.findFirstByUserIdOrderByAddTimeDesc(message.fromId)
            .mapNotNull { it?.let { consumersRepository.delete(it) } }
            .subscribe { vkService.sendMessage(message.chatId, "Галочка, у нас отмена!") }
    }

}