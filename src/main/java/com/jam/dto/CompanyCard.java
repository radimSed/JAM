package com.jam.dto;

public class CompanyCard extends Company{
    private String card;

    public CompanyCard(int id, String name, String card){
        super(id, name);
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
