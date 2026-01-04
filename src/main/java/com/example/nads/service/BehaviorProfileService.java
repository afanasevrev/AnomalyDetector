package com.example.nads.service;

import com.example.nads.domain.BehaviorProfile;
import com.example.nads.domain.FlowFeatures;
import com.example.nads.repository.BehaviorProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BehaviorProfileService {
    private final BehaviorProfileRepository profileRepository;

    /**
     * Обновление профиля хоста по IP на основе нового потока.
     */
    @Transactional
    public BehaviorProfile updateHostProfile(String ip, FlowFeatures features) {
        BehaviorProfile profile = profileRepository
                .findByEntityTypeAndEntityKey("HOST", ip)
                .orElseGet(() -> BehaviorProfile.builder()
                        .entityType("HOST")
                        .entityKey(ip)
                        .version(1L)
                        .build());

        // Для простоты используем экспоненциальное сглаживание
        double alpha = 0.01; // скорость адаптации

        double bytesPerFlow = features.getFlow().getBytes();
        double duration = features.getDurationSeconds();

        if (profile.getVersion() == 1 && profile.getAvgBytesPerFlow() == 0) {
            // Инициализация профиля
            profile.setAvgBytesPerFlow(bytesPerFlow);
            profile.setStdBytesPerFlow(0.0);

            profile.setAvgDurationSeconds(duration);
            profile.setStdDurationSeconds(0.0);

            profile.setAvgFlowsPerMinute(0.0);
            profile.setStdFlowsPerMinute(0.0);
        } else {
            // Обновление среднего и простой оценки дисперсии
            double oldMeanBytes = profile.getAvgBytesPerFlow();
            double newMeanBytes = (1 - alpha) * oldMeanBytes + alpha * bytesPerFlow;
            double newStdBytes = Math.sqrt((1 - alpha) * Math.pow(profile.getStdBytesPerFlow(), 2)
                    + alpha * Math.pow(bytesPerFlow - newMeanBytes, 2));

            profile.setAvgBytesPerFlow(newMeanBytes);
            profile.setStdBytesPerFlow(newStdBytes);

            double oldMeanDur = profile.getAvgDurationSeconds();
            double newMeanDur = (1 - alpha) * oldMeanDur + alpha * duration;
            double newStdDur = Math.sqrt((1 - alpha) * Math.pow(profile.getStdDurationSeconds(), 2)
                    + alpha * Math.pow(duration - newMeanDur, 2));

            profile.setAvgDurationSeconds(newMeanDur);
            profile.setStdDurationSeconds(newStdDur);

            // Для примера: метрика flowsPerMinute должна обновляться отдельно
        }

        profile.setVersion(profile.getVersion() + 1);
        return profileRepository.save(profile);
    }

}
