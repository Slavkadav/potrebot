package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class AllTagWorker(
    private val vkService: VkService
) : Worker {
    override suspend fun regex(): Regex = "[@*](все|all)".toRegex(RegexOption.IGNORE_CASE)

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        reactToAllTag(vkMessage)
    }

    private fun reactToAllTag(vkMessage: VkMessage) {
        val userInfo = vkService.loadUserInfo(vkMessage.fromId) ?: return
        vkService.sendMessage(
            vkMessage.chatId,
            "Опять тут @${userInfo.screenName}(${userInfo.firstName}) использует оллтег. Осуждаем."
        )
    }
}