package com.example.nads.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "behavior_profiles", indexes = {
        @Index(name = "idx_entity_key", columnList = "entityType,entityKey", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehaviorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Тип сущности: HOST, USER, SERVICE
    private String entityType;

    // Ключ сущности: IP-адрес, userId и т.п.
    private String entityKey;

    // Простейшие агрегаты (для примера):
    private double avgBytesPerFlow;
    private double stdBytesPerFlow;

    private double avgDurationSeconds;
    private double stdDurationSeconds;

    private double avgFlowsPerMinute;
    private double stdFlowsPerMinute;

    // Версия / "эпоха" профиля для адаптивности
    private long version;

}
