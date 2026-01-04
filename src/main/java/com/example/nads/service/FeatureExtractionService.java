package com.example.nads.service;

import lombok.RequiredArgsConstructor;
import com.example.nads.domain.FlowFeatures;
import com.example.nads.domain.NetworkFlow;
import com.example.nads.repository.FlowFeaturesRepository;
import com.example.nads.util.NetworkUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Service
@RequiredArgsConstructor
public class FeatureExtractionService {
    private final FlowFeaturesRepository featuresRepository;

    @Transactional
    public FlowFeatures extractAndSave(NetworkFlow flow) {
        FlowFeatures features = buildFeatures(flow);
        return featuresRepository.save(features);
    }

    public FlowFeatures buildFeatures(NetworkFlow flow) {
        double durationSec = Duration.between(flow.getStartTime(), flow.getEndTime()).toMillis() / 1000.0;
        if (durationSec <= 0) {
            durationSec = 0.001; // защита от деления на ноль
        }

        double bytesPerSec = flow.getBytes() / durationSec;
        double packetsPerSec = flow.getPackets() / durationSec;
        double bytesPerPacket = flow.getPackets() > 0 ? (double) flow.getBytes() / flow.getPackets() : 0.0;

        int srcInternal = NetworkUtils.isInternalIp(flow.getSrcIp()) ? 1 : 0;
        int dstInternal = NetworkUtils.isInternalIp(flow.getDstIp()) ? 1 : 0;

        String proto = flow.getProtocol();
        int protoTcp = NetworkUtils.isTcp(proto) ? 1 : 0;
        int protoUdp = NetworkUtils.isUdp(proto) ? 1 : 0;
        int protoIcmp = NetworkUtils.isIcmp(proto) ? 1 : 0;

        return FlowFeatures.builder()
                .flow(flow)
                .durationSeconds(durationSec)
                .bytesPerSecond(bytesPerSec)
                .packetsPerSecond(packetsPerSec)
                .bytesPerPacket(bytesPerPacket)
                .srcInternal(srcInternal)
                .dstInternal(dstInternal)
                .protoTcp(protoTcp)
                .protoUdp(protoUdp)
                .protoIcmp(protoIcmp)
                .build();
    }

}
