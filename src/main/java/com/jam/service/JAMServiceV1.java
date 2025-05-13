package com.jam.service;

import com.jam.dto.*;
import com.jam.exceptions.UnableToPerformException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@Service
public class JAMServiceV1{
    private final int AUTOCLOSEDAYS = 30;

    private final EntityManagerFactory entityManagerFactory;

    public JAMServiceV1(    @Autowired
                            EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
        setAutocloseDate();
    }

    /**
     * sets all application with "Open" status and "lastInteractionDate" older than AUTOCLOSEDAYS days
     * to "Autoclosed" state
     */
    private void setAutocloseDate(){
        LocalDateTime date = LocalDateTime.now().minusDays(AUTOCLOSEDAYS);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        int updateCount = 0;
        try {
            transaction.begin();
            updateCount = entityManager.createQuery("""
                            update Application
                                set
                                    lastInteractionDate = :currentDate,
                                    statusId = :newStatus
                                where
                                    statusId = :oldStatus and
                                    lastInteractionDate <= :idleDate
                            """)
                    .setParameter("newStatus", 3)
                    .setParameter("oldStatus", 1)
                    .setParameter("idleDate", date)
                    .setParameter("currentDate", LocalDateTime.now())
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to perform autoclosure");
        } finally {
            entityManager.close();
        }
        System.out.println(updateCount + " applications autoclosed");
    }

    public String testService(){
        String retString = "Service seems to be ready as well";

        if( entityManagerFactory == null ){
            retString += "<br> Database not available for some reason";
        } else {
            Map<String, Object> infos = entityManagerFactory.getProperties();
            String info1 = infos.get("hibernate.connection.url").toString();
            String info2 = infos.get("hibernate.persistenceUnitName").toString();
            retString += "<br>Database available at " + info1 + " with UnitName " + info2;
        }
        return retString;
    }
}
