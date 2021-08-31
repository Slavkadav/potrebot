package ru.abe.slaves.potrebot.domain.model

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*


data class Consumer(val userId: Int, val moneySpent: Long) {
    @Id
    val uid: UUID = UUID.randomUUID()
    val addTime: LocalDateTime = LocalDateTime.now()
}