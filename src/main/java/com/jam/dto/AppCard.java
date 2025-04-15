package com.jam.dto;

public class AppCard extends AppPreview{
    private String card;

    public AppCard(int id, String title, String positionId, String companyName, String personName, String status, String card) {
        super(id, title, positionId, companyName, personName, status);
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
