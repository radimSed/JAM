package com.jam.service;

import com.jam.dto.*;
import com.jam.exceptions.UnableToPerformException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JAMApplicationServiceV1{
    private final String UNKNOWN = "Unknown";

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    /*
    public JAMApplicationServiceV1(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
    }
*/
    public List<AppPreview> getPreview(int id, String title, String posId, String compName, String persName, String status){
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppPreview> criteria = builder.createQuery(AppPreview.class);
        Root<AppPreview> root = criteria.from(AppPreview.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if(id != 0) {
            Predicate idLike = builder.like(root.<Integer>get(AppPreview_.id).as(String.class), "%" + id + "%");
            predicates.add(idLike);
        }
        if (!title.isEmpty()) {
            Predicate titleLike = builder.like(root.get(AppPreview_.title), "%"+title+"%");
            predicates.add(titleLike);
        }
        if (!posId.isEmpty()) {
            Predicate posIdLike = builder.like(root.get(AppPreview_.positionId), "%"+posId+"%");
            predicates.add(posIdLike);
        }
        if (!compName.isEmpty()) {
            Predicate compNameLike = builder.like(root.get(AppPreview_.companyName), "%"+compName+"%");
            predicates.add(compNameLike);
        }
        if (!persName.isEmpty()) {
            Predicate persNameLike = builder.like(root.get(AppPreview_.personName), "%"+persName+"%");
            predicates.add(persNameLike);
        }
        if (!status.isEmpty()) {
            Predicate statusEq = builder.equal(root.get(AppPreview_.status), status);
            predicates.add(statusEq);
        }
        criteria.where(predicates.toArray(new Predicate[]{}));
        criteria.orderBy(builder.asc(root.get(AppPreview_.id)));

        return entityManager.createQuery(criteria).getResultList();
    }

    public ApplicationDetail getDetail(int id){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Application app = entityManager.find(Application.class, id);
        Company comp = entityManager.find(Company.class, app.getCompanyId());
        Person pers = entityManager.find(Person.class, app.getPersonId());



        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Records> criteria = builder.createQuery(Records.class);
        Root<Records> root = criteria.from(Records.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get(Records_.applicationId), id));
        criteria.orderBy(builder.desc(root.get(Records_.date)));

        List<Records> records = entityManager.createQuery(criteria).getResultList();

        ApplicationDetail appDetail = new ApplicationDetail(app.getApplicationId(), app.getPositionTitle(), app.getPositionId(),
                app.getStartDate(), app.getLastInteractionDate(), app.getStatusId(), pers, comp, processRecords(records));

        return appDetail;
    }

    private String processRecords(List<Records> records){
        StringBuilder sb = new StringBuilder();
        for(Records singleRecord: records){
            sb.append("==========").append(singleRecord.getDate()).append("==========\n");
            sb.append(singleRecord.getDescription()).append("\n\n");
        }

        return sb.toString();
    }

    public ResultMessage postApplication(Application app){
        Application retApp;
        if(app.getPositionTitle().isEmpty()){
            app.setPositionTitle(UNKNOWN);
        }
        if(app.getPositionId().isEmpty()){
            app.setPositionId(UNKNOWN);
        }
        app.setStartDate(LocalDateTime.now());
        app.setLastInteractionDate(app.getStartDate());
        app.setStatusId(1);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(app);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Application for  " + app.getPositionTitle() + " has NOT been saved to database");
        }
        finally {
            //check id of the newly saved application
            retApp = checkApplication(entityManager, app);
            entityManager.close();
        }

        return new ResultMessage("Application for \"" + app.getPositionTitle() + "\" has been saved with id " + retApp.getApplicationId());
    }

    private Application checkApplication(EntityManager em, Application app){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Application> criteria = builder.createQuery(Application.class);
        Root<Application> root = criteria.from(Application.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<>();
        Predicate titleLike = builder.equal(root.get(Application_.positionTitle), app.getPositionTitle());
        predicates.add(titleLike);
        Predicate posIdLike = builder.equal(root.get(Application_.positionId), app.getPositionId());
        predicates.add(posIdLike);
        Predicate personLike = builder.equal(root.get(Application_.personId), app.getPersonId());
        predicates.add(personLike);
        Predicate companyLike = builder.equal(root.get(Application_.companyId), app.getCompanyId());
        predicates.add(companyLike);
        Predicate statusLike = builder.equal(root.get(Application_.statusId), app.getStatusId());
        predicates.add(statusLike);

        criteria.where(predicates.toArray(new Predicate[]{}));

        List<Application> appList = (List<Application>) em.createQuery(criteria).getResultList();

        if(!appList.isEmpty()){
            return appList.get(appList.size()-1);
        } else {
            return null;
        }
    }

    public ResultMessage updateApplication(ApplicationUpdate appUpdate){
        //create Application object to update
        Application app = new Application(appUpdate.getApplicationId(), appUpdate.getPositionTitle(), appUpdate.getPositionId(),
                appUpdate.getPersonId(), appUpdate.getCompanyId(), appUpdate.getStartDate(), LocalDateTime.now(),appUpdate.getStatusId());

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        //create update
        CriteriaUpdate<Application> update = builder.createCriteriaUpdate(Application.class);
        //set the root class
        Root<Application> root = update.from(Application.class);
        update.set(root.get(Application_.positionTitle), app.getPositionTitle());
        update.set(root.get(Application_.positionId), app.getPositionId());
        update.set(root.get(Application_.personId), app.getPersonId());
        update.set(root.get(Application_.companyId), app.getCompanyId());
        update.set(root.get(Application_.lastInteractionDate), app.getLastInteractionDate());
        update.set(root.get(Application_.statusId), app.getStatusId());
        //setup update where clause
        update.where(builder.equal(root.get(Application_.applicationId), app.getApplicationId()));

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            //perform the update
            entityManager.createQuery(update).executeUpdate();

            if(!appUpdate.getRecord().isEmpty()){
                //save record too
                Records record = new Records(0, app.getApplicationId(), app.getLastInteractionDate(), appUpdate.getRecord());
                entityManager.persist(record);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to update application id " + app.getApplicationId());
        } finally {
            entityManager.close();
        }

        return new ResultMessage("Data of application id " + app.getApplicationId() + " has been updated");
    }

    public ResultMessage deleteApplication(int id){
        // delete app and Records
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        //create delete
        CriteriaDelete<Application> deleteApp = builder.createCriteriaDelete(Application.class);
        Root<Application> rootApp = deleteApp.from(Application.class);
        deleteApp.where(builder.equal(rootApp.get(Application_.applicationId), id));

        CriteriaDelete<Records> deleteRec = builder.createCriteriaDelete(Records.class);
        Root<Records> rootRec = deleteRec.from(Records.class);
        deleteRec.where(builder.equal(rootRec.get(Records_.applicationId), id));

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            //delete records first so app deletion is not blocked by foreign keys in use
            transaction.begin();
            entityManager.createQuery(deleteRec).executeUpdate();
            //delete app too
            entityManager.createQuery(deleteApp).executeUpdate();
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to delete application id " + id);
        }
        finally {
            entityManager.close();
        }
        return new ResultMessage("Application id " + id + "has been deleted from db");
    }

}
