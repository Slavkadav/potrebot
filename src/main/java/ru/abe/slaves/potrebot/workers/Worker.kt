package ru.abe.slaves.potrebot.workers

import ru.abe.slaves.potrebot.web.model.VkMessage

sealed interface Worker {
    val regexPrefix: String
        get() = "^\\S*\\s?"

    suspend fun regex(): Regex
    suspend fun reactToMessage(vkMessage: VkMessage)
}