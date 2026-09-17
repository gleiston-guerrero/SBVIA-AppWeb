package com.sbvia.backend.repository;

import com.sbvia.backend.model.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {
    List<Backup> findAllByOrderByStartDateDesc();
}
