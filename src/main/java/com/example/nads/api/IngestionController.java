package com.example.nads.api;

import com.example.nads.api.dto.NetworkFlowDto;
import com.example.nads.domain.FlowFeatures;
import com.example.nads.domain.NetworkFlow;
import com.example.nads.repository.NetworkFlowRepository;
import com.example.nads.service.AnomalyDetectionService;
import com.example.nads.service.FeatureExtractionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/ingest")
@RequiredArgsConstructor
public class IngestionController {
    private final NetworkFlowRepository flowRepository;
    private final FeatureExtractionService featureExtractionService;
    private final AnomalyDetectionService anomalyDetectionService;

    @PostMapping("/flow")
    public ResponseEntity<AnomalyResponse> ingestFlow(@RequestBody NetworkFlowDto dto) {
        NetworkFlow flow = toEntity(dto);
        flow = flowRepository.save(flow);

        FlowFeatures features = featureExtractionService.extractAndSave(flow);

        double score = anomalyDetectionService.scoreAndPossiblyCreateEvent(features);

        AnomalyResponse response = new AnomalyResponse();
        response.setFlowId(flow.getId());
        response.setAnomalyScore(score);
        response.setAnomalous(score >= 3.0);

        return ResponseEntity.ok(response);
    }

    private NetworkFlow toEntity(NetworkFlowDto dto) {
        return NetworkFlow.builder()
                .startTime(Instant.parse(dto.getStartTime()))
                .endTime(Instant.parse(dto.getEndTime()))
                .srcIp(dto.getSrcIp())
                .srcPort(dto.getSrcPort())
                .dstIp(dto.getDstIp())
                .dstPort(dto.getDstPort())
                .protocol(dto.getProtocol())
                .bytes(dto.getBytes())
                .packets(dto.getPackets())
                .inbound(dto.getInbound() != null ? dto.getInbound() : Boolean.FALSE)
                .build();
    }

    @Data
    static class AnomalyResponse {
        private Long flowId;
        private double anomalyScore;
        private boolean anomalous;
    }

}
