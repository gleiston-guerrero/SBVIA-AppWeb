package com.sbvia.backend.repository;

import com.sbvia.backend.entity.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Integer> {

    Optional<UserState> findByName(String name);
}
