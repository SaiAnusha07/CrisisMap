package com.makeskilled.CrisisMap.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name="reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private String mobileNo;
    private String description;
    private LocalDate date;

    private String submittedBy; // Track the user who submitted the report

    private String remarks; // Additional remarks about the report
   

    // Getters and setters
    private String status;  // Status of the report: Resolved or Pending

    private String accepted; // User who accepted/changed the report status

    // Validate status transition
    public boolean canTransitionTo(String newStatus) {
        if (status == null) return false;
        
        switch (status) {
            case "PENDING":
                return newStatus.equals("IN_PROGRESS") || newStatus.equals("COMPLETED");
            case "IN_PROGRESS":
                return newStatus.equals("COMPLETED") || newStatus.equals("PENDING");
            default:
                return false;
        }
       
    }

    // Getters and setters for accepted
    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
        // Method to handle status transition and reset accepted field if needed
        public void updateStatus(String newStatus) {
            if (newStatus.equals("PENDING") && this.status.equals("IN_PROGRESS")) {
               setAccepted(null);
            }
            this.status = newStatus;
        }
    
}