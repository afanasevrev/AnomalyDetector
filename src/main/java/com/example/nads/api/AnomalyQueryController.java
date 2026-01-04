package com.example.nads.api;

import com.example.nads.domain.AnomalyEvent;
import com.example.nads.repository.AnomalyEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/anomalies")
@RequiredArgsConstructor
public class AnomalyQueryController {
    private final AnomalyEventRepository anomalyEventRepository;

    @GetMapping
    public Page<AnomalyEvent> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return anomalyEventRepository.findAll(PageRequest.of(page, size));
    }

}
