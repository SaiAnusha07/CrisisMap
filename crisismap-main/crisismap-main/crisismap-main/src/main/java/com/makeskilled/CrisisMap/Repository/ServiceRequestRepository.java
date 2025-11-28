package com.makeskilled.CrisisMap.Repository;

import com.makeskilled.CrisisMap.Entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.requestedBy = :requestedBy ORDER BY sr.requestDateTime DESC")
    List<ServiceRequest> findByRequestedBy(String requestedBy);
    
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.id IN " +
           "(SELECT sr2.id FROM ServiceRequest sr2 JOIN sr2.volunteers v WHERE v = :volunteerId) " +
           "ORDER BY sr.status ASC, sr.requestDateTime DESC")
    List<ServiceRequest> findByVolunteersContaining(String volunteerId);
    
    @Query("SELECT sr FROM ServiceRequest sr WHERE " +
           "sr.id NOT IN (SELECT sr2.id FROM ServiceRequest sr2 JOIN sr2.volunteers v WHERE v = :volunteerId) " +
           "AND sr.volunteersAccepted < sr.volunteersNeeded " +
           "AND sr.status = 'PENDING' " +
           "ORDER BY sr.requestDateTime DESC")
    List<ServiceRequest> findAvailableRequests(String volunteerId);
    
    @Query("SELECT sr FROM ServiceRequest sr WHERE " +
           "sr.status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED') " +
           "ORDER BY CASE sr.status " +
           "WHEN 'PENDING' THEN 1 " +
           "WHEN 'ACCEPTED' THEN 2 " +
           "WHEN 'IN_PROGRESS' THEN 3 " +
           "WHEN 'COMPLETED' THEN 4 " +
           "ELSE 5 END, sr.requestDateTime DESC")
    List<ServiceRequest> findAllActiveRequests();

    @Query("SELECT sr FROM ServiceRequest sr WHERE " +
           "sr.requestedBy = :ngoId AND " +
           "sr.status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED') " +
           "ORDER BY sr.requestDateTime DESC")
    List<ServiceRequest> findActiveRequestsByNgo(String ngoId);
}
