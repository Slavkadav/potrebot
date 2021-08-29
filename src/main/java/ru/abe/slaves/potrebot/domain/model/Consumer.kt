package ru.abe.slaves.potrebot.domain.model

import java.time.LocalDateTime


data class Consumer(
    val userId: Int,
    val moneySpent: Long,
    val addTime: LocalDateTime = LocalDateTime.now()
)