package com.didan.testperformance.first.repository;

import com.didan.testperformance.first.entity.LogsInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsInfoRepository extends JpaRepository<LogsInfoEntity, Integer> {
}
