package com.jam.dto;

import java.time.LocalDateTime;

public class ApplicationDetail {
    private int applicationId;
    private String title;
    private String positionId;
    private LocalDateTime startDate;
    private LocalDateTime lastInteractionDate;
    private int status;

    private Person person;
    private Company company;
    private String records;

    public ApplicationDetail(int applicationId, String title, String positionId, LocalDateTime startDate, LocalDateTime lastInteractionDate,
                             int status, Person person, Company company, String records) {
        this.applicationId = applicationId;
        this.title = title;
        this.positionId = positionId;
        this.startDate = startDate;
        this.lastInteractionDate = lastInteractionDate;
        this.status = status;
        this.person = person;
        this.company = company;
        this.records = records;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
