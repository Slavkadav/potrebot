package ru.abe.slaves.potrebot.domain.repository

import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.abe.slaves.potrebot.domain.model.Consumer
import java.time.LocalDateTime
import java.util.*

interface ConsumersRepository : ReactiveMongoRepository<Consumer, UUID> {
    fun findAllByUserId(userId: Int): Flux<Consumer>
    fun findAllByUserIdAndAddTimeBetween(userId: Int, start: LocalDateTime, end: LocalDateTime): Flux<Consumer>
    fun findFirstByUserIdOrderByAddTimeDesc(userId: Int): Mono<Consumer?>

    @Aggregation(pipeline = ["{\$group: { _id: '', total: {\$sum: \$moneySpent}}}"])
    fun findSum(): Mono<Long>
}