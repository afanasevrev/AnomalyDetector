package com.example.nads.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flow_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с сырым потоком
    @OneToOne
    @JoinColumn(name = "flow_id", nullable = false, unique = true)
    private NetworkFlow flow;

    // Базовые признаки:
    private double durationSeconds;
    private double bytesPerSecond;
    private double packetsPerSecond;
    private double bytesPerPacket;

    // Признаки направленности
    private int srcInternal;   // 1 если src внутри нашей сети, иначе 0
    private int dstInternal;   // 1 если dst внутри

    // Простая one-hot кодировка протокола (для прототипа)
    private int protoTcp;
    private int protoUdp;
    private int protoIcmp;

    // Оценка аномальности, рассчитанная моделью
    private Double anomalyScore;

    // Флаг "аномальный" по текущему порогу
    private Boolean anomalous;

}
