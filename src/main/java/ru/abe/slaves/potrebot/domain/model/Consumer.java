package ru.abe.slaves.potrebot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Consumer {
    @Id
    private UUID uid = UUID.randomUUID();

    private int userId;
    private double moneySpent;
    private LocalDateTime addTime = LocalDateTime.now();

    public Consumer(int fromId, double sum) {
        this.userId = fromId;
        this.moneySpent = sum;
    }
}
