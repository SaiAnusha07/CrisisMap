package com.makeskilled.CrisisMap.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ngo_resource_usage")
public class NgoResourceUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "ngo_username", nullable = false)
    private String ngoUsername;

    @Column(name = "quantity_taken", nullable = false)
    private int quantityTaken;

    @Column(name = "usage_timestamp")
    private LocalDateTime usageTimestamp;

    // Constructors
    public NgoResourceUsage() {
        this.usageTimestamp = LocalDateTime.now();
    }

    public NgoResourceUsage(Long resourceId, String ngoUsername, int quantityTaken) {
        this.resourceId = resourceId;
        this.ngoUsername = ngoUsername;
        this.quantityTaken = quantityTaken;
        this.usageTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getNgoUsername() {
        return ngoUsername;
    }

    public void setNgoUsername(String ngoUsername) {
        this.ngoUsername = ngoUsername;
    }

    public int getQuantityTaken() {
        return quantityTaken;
    }

    public void setQuantityTaken(int quantityTaken) {
        this.quantityTaken = quantityTaken;
    }

    public LocalDateTime getUsageTimestamp() {
        return usageTimestamp;
    }

    public void setUsageTimestamp(LocalDateTime usageTimestamp) {
        this.usageTimestamp = usageTimestamp;
    }


	
}