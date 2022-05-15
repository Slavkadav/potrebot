//package ru.abe.slaves.potrebot
//
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Disabled
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.DynamicPropertyRegistry
//import org.springframework.test.context.DynamicPropertySource
//import org.testcontainers.containers.MongoDBContainer
//import org.testcontainers.junit.jupiter.Container
//import org.testcontainers.junit.jupiter.Testcontainers
//import ru.abe.slaves.potrebot.domain.model.Consumer
//import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository
//
//@Disabled
//@Testcontainers
//@DataMongoTest
//@ActiveProfiles("test")
//class RepositoryTests {
//
//    @Autowired
//    lateinit var repo: ConsumersRepository
//
//    companion object {
//        @Container
//        val container: MongoDBContainer = MongoDBContainer("mongo")
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun properties(registry: DynamicPropertyRegistry) {
//            registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl)
//        }
//    }
//
//    @BeforeEach
//    fun cleanup() = repo.deleteAll()
//
//    @Test
//    fun shouldSaveConsumerRecord() {
//        val consumer = Consumer(1, 1000)
//        repo.save(consumer)
//    }
//
//    @Test
//    fun shouldLoadSavedConsumerRecord() {
//        val consumer = Consumer(1, 1000)
//        repo.save(consumer)
//        val allByUserId = repo.findAllByUserId(1)
//        assertThat(allByUserId).size().isEqualTo(1)
//        assertThat(allByUserId[0].uid).isEqualTo(consumer.uid)
//    }
//
//    @Test
//    fun shouldReturnSum() {
//        val consumer = Consumer(1, 1000)
//        repo.save(consumer)
//        val consumer2 = Consumer(2, 2000)
//        repo.save(consumer2)
//
//        val sum = repo.findSum()
//        assertThat(sum).isEqualTo(3000)
//    }
//}