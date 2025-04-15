package com.jam.dto;

public class PersonCard extends Person{
    private String card;

    public PersonCard(int id, String name, String email, String phone, String card) {
        super(id, name, email, phone);
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
