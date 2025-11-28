package com.makeskilled.CrisisMap.Repository;

import com.makeskilled.CrisisMap.Entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Volunteer findByUsername(String username);
}
