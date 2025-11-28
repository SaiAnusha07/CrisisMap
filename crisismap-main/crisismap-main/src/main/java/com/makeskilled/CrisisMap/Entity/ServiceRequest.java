package com.makeskilled.CrisisMap.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false, length = 10)
    private String mobileNo;

    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private LocalDateTime requestDateTime;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private int volunteersNeeded;

    @Column(nullable = false)
    private int volunteersAccepted = 0;

    @Column(nullable = false)
    private int vstart = 0;

    @Column(nullable = false)
    private int vcompleted = 0;

    @ElementCollection
    @CollectionTable(name = "service_request_volunteers_status", 
                    joinColumns = @JoinColumn(name = "service_request_id"))
    @Column(name = "volunteer_status")
    private Set<String> volunteerStatuses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "service_request_volunteers", 
                    joinColumns = @JoinColumn(name = "service_request_id"))
    @Column(name = "volunteer_id")
    private Set<String> volunteers = new HashSet<>();

    public ServiceRequest() {
        this.requestDateTime = LocalDateTime.now();
        this.volunteerStatuses = new HashSet<>();
        this.volunteers = new HashSet<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVolunteersNeeded() {
        return volunteersNeeded;
    }

    public void setVolunteersNeeded(int volunteersNeeded) {
        this.volunteersNeeded = volunteersNeeded;
    }

    public int getVolunteersAccepted() {
        return volunteersAccepted;
    }

    public void setVolunteersAccepted(int volunteersAccepted) {
        this.volunteersAccepted = volunteersAccepted;
    }

    public int getVstart() {
        return vstart;
    }

    public void setVstart(int vstart) {
        this.vstart = vstart;
    }

    public int getVcompleted() {
        return vcompleted;
    }

    public void setVcompleted(int vcompleted) {
        this.vcompleted = vcompleted;
    }
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    public Set<String> getVolunteerStatuses() {
        return volunteerStatuses;
    }

    public void setVolunteerStatuses(Set<String> volunteerStatuses) {
        this.volunteerStatuses = volunteerStatuses;
    }

    public Set<String> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(Set<String> volunteers) {
        this.volunteers = volunteers;
    }
}