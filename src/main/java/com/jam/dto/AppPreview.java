package com.jam.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "preview")
public class AppPreview {

    @Id
    @Column(name = "ApplicationId")
    private int id;
    @Column(name = "positionTitle")
    private String title;
    private String positionId;
    private String companyName;
    private String personName;
    private String status;

    public AppPreview(){
        //to be used by hibernate
    }

    public AppPreview(int id, String title, String positionId, String companyName, String personName, String status) {
        this.id = id;
        this.title = title;
        this.positionId = positionId;
        this.companyName = companyName;
        this.personName = personName;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
