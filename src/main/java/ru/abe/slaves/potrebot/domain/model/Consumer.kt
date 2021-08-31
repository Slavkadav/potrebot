package ru.abe.slaves.potrebot.domain.model

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*


data class Consumer(
    @Id
    var uid: UUID = UUID.randomUUID(),
    val userId: Int,
    val moneySpent: Long,
    val addTime: LocalDateTime = LocalDateTime.now()
)