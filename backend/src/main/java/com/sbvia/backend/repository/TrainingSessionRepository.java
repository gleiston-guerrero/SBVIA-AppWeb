package com.sbvia.backend.repository;

import com.sbvia.backend.entity.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>TrainingSessionRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
}
