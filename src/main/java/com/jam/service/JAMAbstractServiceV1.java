package com.jam.service;

import jakarta.persistence.EntityManagerFactory;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

public class JAMAbstractServiceV1{
    protected EntityManagerFactory entityManagerFactory;
    protected final String UNKNOWN = "Unknown";


    JAMAbstractServiceV1(){
        // an EntityManagerFactory is set up once for an application
        // IMPORTANT: notice how the name here matches the name we
        // gave the persistence-unit in persistence.xml
        try {
            entityManagerFactory = createEntityManagerFactory("com.JAM");
        } catch (Exception e) {
            System.err.println("***************************************************************************************");
            System.err.println(e.getMessage());
            System.err.println("***************************************************************************************");
        }
    }
}
