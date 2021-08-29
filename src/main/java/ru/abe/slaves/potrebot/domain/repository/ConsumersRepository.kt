package ru.abe.slaves.potrebot.domain.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.abe.slaves.potrebot.domain.model.Consumer
import java.time.LocalDateTime
import java.util.*

interface ConsumersRepository : MongoRepository<Consumer, UUID> {
    fun findAllByUserId(userId: Int): List<Consumer>
    fun findAllByUserIdAndAddTimeBetween(userId: Int, start: LocalDateTime, end: LocalDateTime): List<Consumer>
    fun findFirstByUserIdOrderByAddTimeDesc(userId: Int): Consumer?
}