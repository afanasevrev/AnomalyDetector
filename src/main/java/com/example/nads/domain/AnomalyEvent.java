package com.example.nads.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "anomaly_events", indexes = {
        @Index(name = "idx_time", columnList = "eventTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnomalyEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant eventTime;

    private String srcIp;
    private String dstIp;
    private String entityType; // HOST/USER/SERVICE
    private String entityKey;

    private Double anomalyScore;

    private String severity;   // LOW/MEDIUM/HIGH/CRITICAL

    @Column(length = 2048)
    private String description;

}
