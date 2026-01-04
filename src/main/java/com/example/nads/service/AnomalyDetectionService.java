package com.example.nads.service;

import com.example.nads.domain.AnomalyEvent;
import com.example.nads.domain.BehaviorProfile;
import com.example.nads.domain.FlowFeatures;
import com.example.nads.repository.AnomalyEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {
    private final BehaviorProfileService behaviorProfileService;
    private final AnomalyEventRepository anomalyEventRepository;

    // Порог аномальности (можно вынести в конфиг, управлять через API)
    @Value("${nad.anomaly.threshold:3.0}")
    private double anomalyThreshold;

    /**
     * Оценка потока относительно профиля хоста-источника.
     *
     */
    @Transactional
    public double scoreAndPossiblyCreateEvent(FlowFeatures features) {
        String srcIp = features.getFlow().getSrcIp();

        // Обновляем профиль (адаптивное обучение)
        BehaviorProfile profile = behaviorProfileService.updateHostProfile(srcIp, features);

        // Вычисляем Z-score по двум простым признакам
        double zBytes = computeZ(features.getFlow().getBytes(),
                profile.getAvgBytesPerFlow(),
                profile.getStdBytesPerFlow());

        double zDuration = computeZ(features.getDurationSeconds(),
                profile.getAvgDurationSeconds(),
                profile.getStdDurationSeconds());

        // Интегральная оценка аномальности как сумма модулей Z
        double score = Math.abs(zBytes) + Math.abs(zDuration);

        features.setAnomalyScore(score);
        features.setAnomalous(score >= anomalyThreshold);

        if (score >= anomalyThreshold) {
            AnomalyEvent event = AnomalyEvent.builder()
                    .eventTime(Instant.now())
                    .srcIp(features.getFlow().getSrcIp())
                    .dstIp(features.getFlow().getDstIp())
                    .entityType("HOST")
                    .entityKey(srcIp)
                    .anomalyScore(score)
                    .severity(calculateSeverity(score))
                    .description("Аномальное поведение хоста " + srcIp +
                            " по объёму/длительности потока")
                    .build();
            anomalyEventRepository.save(event);
        }

        return score;
    }

    private double computeZ(double value, double mean, double std) {
        if (std <= 0.0001) {
            return 0.0; // недостаточно статистики или нет разброса
        }
        return (value - mean) / std;
    }

    private String calculateSeverity(double score) {
        if (score >= 10) return "CRITICAL";
        if (score >= 7) return "HIGH";
        if (score >= 4) return "MEDIUM";
        return "LOW";
    }

}
