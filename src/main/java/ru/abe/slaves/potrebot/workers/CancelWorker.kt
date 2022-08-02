package ru.abe.slaves.potrebot.workers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class CancelWorker(
    private val consumersRepository: ConsumersRepository, private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}отмена")

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        cancelLastOperation(vkMessage)
    }

    private suspend fun cancelLastOperation(message: VkMessage) {
        withContext(Dispatchers.IO) {
            launch {
                val consumer = consumersRepository.findFirstByUserIdOrderByAddTimeDesc(message.fromId).awaitSingleOrNull()
                consumer?.also { consumersRepository.delete(it).awaitSingle() }
            }
        }

        vkService.sendMessage(message.chatId, "Галочка, у нас отмена!")
    }

}