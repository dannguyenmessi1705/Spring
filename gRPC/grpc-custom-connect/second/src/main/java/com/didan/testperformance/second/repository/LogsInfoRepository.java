package com.didan.testperformance.second.repository;

import com.didan.testperformance.second.entity.LogsInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsInfoRepository extends JpaRepository<LogsInfoEntity, Integer> {
}
