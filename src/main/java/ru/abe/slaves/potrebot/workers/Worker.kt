package ru.abe.slaves.potrebot.workers

import ru.abe.slaves.potrebot.web.model.VkMessage

sealed interface Worker {
    val regexPrefix: String
        get() = "\\[\\S+]\\W*"

    fun regex(): Regex
    fun reactToMessage(vkMessage: VkMessage)
}