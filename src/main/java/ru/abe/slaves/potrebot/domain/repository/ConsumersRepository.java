package ru.abe.slaves.potrebot.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.abe.slaves.potrebot.domain.model.Consumer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsumersRepository extends MongoRepository<Consumer, UUID> {
    Optional<Consumer> findFirstByUserId(int userId);
    List<Consumer> findAllByUserId(int userId);
}
