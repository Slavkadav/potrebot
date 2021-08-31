package ru.abe.slaves.potrebot.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Consumer(
    var userId: Int,
    var moneySpent: Long,
    @Id var uid: UUID = UUID.randomUUID(),
    var addTime: LocalDateTime = LocalDateTime.now()
)