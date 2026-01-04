package com.example.nads.repository;

import com.example.nads.domain.BehaviorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BehaviorProfileRepository extends JpaRepository<BehaviorProfile, Long> {
    Optional<BehaviorProfile> findByEntityTypeAndEntityKey(String entityType, String entityKey);
}
