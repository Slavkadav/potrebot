package ru.abe.slaves.potrebot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.abe.slaves.potrebot.domain.model.Consumer
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository

@ActiveProfiles("test")
@DataMongoTest
@ExtendWith(SpringExtension::class)
class RepositoryTests {

    @Autowired
    lateinit var repo: ConsumersRepository

    @BeforeEach
    fun cleanup() = repo.deleteAll()

    @Test
    fun shouldSaveConsumerRecord() {
        val consumer = Consumer(1, 1000)
        repo.save(consumer)
    }

    @Test
    fun shouldLoadSavedConsumerRecord() {
        val consumer = Consumer(1, 1000)
        repo.save(consumer)
        val allByUserId = repo.findAllByUserId(1)
        assertThat(allByUserId).size().isEqualTo(1)
        assertThat(allByUserId[0].uid).isEqualTo(consumer.uid)
    }
}