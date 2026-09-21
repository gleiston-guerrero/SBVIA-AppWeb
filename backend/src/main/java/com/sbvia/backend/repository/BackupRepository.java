package com.sbvia.backend.repository;

import com.sbvia.backend.model.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link com.sbvia.backend.model.Backup} records.
 *
 * @author Keitho_
 */
@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {
    /**
     * <p>findAllByOrderByStartDateDesc.</p>
     *
     * @return a {@link java.util.List} object
     */
    List<Backup> findAllByOrderByStartDateDesc();
}
