package ru.abe.slaves.potrebot.workers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.domain.model.Consumer
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class SpentWorker(
    private val consumersRepository: ConsumersRepository,
    private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix},\\s?(\\d+)$")

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        val matchResult = regex().find(vkMessage.text)
        matchResult!!.groups[1]?.let { saveMoneySpent(vkMessage, it.value.toInt()) }
    }

    private suspend fun saveMoneySpent(message: VkMessage, moneySpent: Int) {
        withContext(Dispatchers.IO) {
            launch {
                val fromId = message.fromId
                consumersRepository.save(Consumer(fromId, moneySpent.toLong())).awaitSingleOrNull()
            }
        }

        vkService.sendMessage(message.chatId, "Записал")
    }
}