package com.example.nads.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "network_flows", indexes = {
        @Index(name = "idx_start_time", columnList = "startTime"),
        @Index(name = "idx_src_ip", columnList = "srcIp"),
        @Index(name = "idx_dst_ip", columnList = "dstIp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant startTime;
    private Instant endTime;

    private String srcIp;
    private Integer srcPort;

    private String dstIp;
    private Integer dstPort;

    private String protocol; // TCP/UDP/ICMP...

    private Long bytes;      // общий объём
    private Long packets;    // количество пакетов

    private Boolean inbound; // направление относительно наблюдаемого сегмента

}
