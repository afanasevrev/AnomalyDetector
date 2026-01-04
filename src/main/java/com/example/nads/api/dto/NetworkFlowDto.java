package com.example.nads.api.dto;

import lombok.Data;

@Data
public class NetworkFlowDto {
    private String startTime; // ISO-8601
    private String endTime;

    private String srcIp;
    private Integer srcPort;
    private String dstIp;
    private Integer dstPort;

    private String protocol;

    private Long bytes;
    private Long packets;

    private Boolean inbound;

}
