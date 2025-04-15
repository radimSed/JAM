package com.jam.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
public class Application {
    @Id
    private int applicationId;
    private String positionTitle;
    private String positionId;
    private int personId;
    private int companyId;
    private LocalDateTime startDate;
    private LocalDateTime lastInteractionDate;
    private int statusId;

    public Application(){
        //for use by hibernate
    }

    public Application(int applicationId, String positionTitle, String positionId, int personId, int companyId, LocalDateTime startDate, LocalDateTime lastInteractionDate, int statusId) {
        this.applicationId = applicationId;
        this.positionTitle = positionTitle;
        this.positionId = positionId;
        this.personId = personId;
        this.companyId = companyId;
        this.startDate = startDate;
        this.lastInteractionDate = lastInteractionDate;
        this.statusId = statusId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getLastInteractionDate() {
        return lastInteractionDate;
    }

    public void setLastInteractionDate(LocalDateTime lastInteractionDate) {
        this.lastInteractionDate = lastInteractionDate;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
