package ru.abe.slaves.potrebot.domain.model

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*


data class Consumer(
    var userId: Int,
    var moneySpent: Long,
    @Id var uid: UUID = UUID.randomUUID(),
    var addTime: LocalDateTime = LocalDateTime.now()
)