package com.jam.dto;

import java.time.LocalDateTime;

public class ApplicationUpdate extends Application{
    private String record;

    public ApplicationUpdate(int applicationId, String positionTitle, String positionId, int personId, int companyId, LocalDateTime startDate, LocalDateTime lastInteractionDate, int statusId, String record){
        super(applicationId, positionTitle, positionId, personId, companyId, startDate,lastInteractionDate, statusId);
        this.record = record;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}
