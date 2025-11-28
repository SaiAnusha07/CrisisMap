package com.makeskilled.CrisisMap.Repository;

import com.google.common.base.Optional;
import com.makeskilled.CrisisMap.Entity.NgoResourceUsage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NgoResourceUsageRepository extends JpaRepository<NgoResourceUsage, Long> {
    List<NgoResourceUsage> findAllByResourceId(Long resourceId);
}
