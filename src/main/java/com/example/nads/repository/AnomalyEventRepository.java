package com.example.nads.repository;

import com.example.nads.domain.AnomalyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomalyEventRepository extends JpaRepository<AnomalyEvent, Long> {}
