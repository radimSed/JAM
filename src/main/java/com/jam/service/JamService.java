package com.jam.service;

import com.jam.dto.*;
import com.jam.exceptions.AlreadyExistsException;
import com.jam.exceptions.UnableToPerformException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transaction;
import net.bytebuddy.TypeCache;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@Service
public class JamService {
    private final String UNKNOWN = "Unknown";
    private final int AUTOCLOSEDAYS = 30;

    private EntityManagerFactory entityManagerFactory;

    JamService(){
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

        setAutoclose();
    }

    /**
     * sets all application with "Open" status and "lastInteractionDate" older then 30 days
     * to "Autoclosed" state
     */
    private void setAutoclose(){
        autoclose(getAppOlderThan30Days());
    }

    private List<Application> getAppOlderThan30Days(){
        LocalDateTime date = LocalDateTime.now().minusDays(AUTOCLOSEDAYS);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Application> criteria = builder.createQuery(Application.class);
        Root<Application> root = criteria.from(Application.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<>();
        Predicate status = builder.equal(root.get(Application_.statusId), 1); //open status
        predicates.add(status);
        Predicate datePre = builder.lessThan(root.get(Application_.lastInteractionDate), date);
        predicates.add(datePre);
        criteria.where(predicates.toArray(new Predicate[]{}));

        return entityManager.createQuery(criteria).getResultList();
    }

    private void autoclose(List<Application> appToAutoclose){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        EntityTransaction transaction = entityManager.getTransaction();

        //create update
        CriteriaUpdate<Application> update = builder.createCriteriaUpdate(Application.class);
        //set the root class
        Root<Application> root = update.from(Application.class);
        try {
            transaction.begin();
            for(Application app: appToAutoclose) {
                update.set(root.get(Application_.lastInteractionDate), LocalDateTime.now());
                update.set(root.get(Application_.statusId), 3);
                update.where(builder.equal(root.get(Application_.applicationId), app.getApplicationId()));
                entityManager.createQuery(update).executeUpdate();
            }
            transaction.commit();
            System.out.println(appToAutoclose.size() + " applications autoclosed");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to perform autoclosure");
        } finally {
            entityManager.close();
        }
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

//*********************** Company related methods ************************************************
    public ResultMessage postCompany(Company company){
        Company retCompany;
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //to avoid empty fields in db
        if(company.getName().isEmpty()){
            company.setName(UNKNOWN);
        }

        if(checkCompany(entityManager, company) != null){
            throw new AlreadyExistsException("At least one company with the name \"" + company.getName() + "\" already exists in database.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(company);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Company " + company.getName() + " has NOT been saved to database");
        }
        finally {
            //check id of the newly saved company
            retCompany = checkCompany(entityManager, company);
            entityManager.close();
        }

        return new ResultMessage("Company \"" + retCompany.getName() + "\" has been saved with id " + retCompany.getId());
    }

    private Company checkCompany(EntityManager em, Company company){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        Root<Company> root = criteria.from(Company.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get(Company_.name), company.getName()));

        //check usage of the company name first to ensure there is only one company of the name
        List<Company> compList = (List<Company>) em.createQuery(criteria).getResultList();

        if(!compList.isEmpty()){
            return compList.get(0);
        } else {
            return null;
        }
    }

    public List<Company> getCompanies(int id, String name){
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> criteria = builder.createQuery(Company.class);
        Root<Company> root = criteria.from(Company.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if(id != 0) {
            Predicate idLike = builder.like(root.<Integer>get("id").as(String.class), "%" + id + "%");
            predicates.add(idLike);
        }
        if (!name.isEmpty()) {
            Predicate nameLike = builder.like(root.get("name"), "%"+name+"%");
            predicates.add(nameLike);
        }
        criteria.where(predicates.toArray(new Predicate[]{}));

        return entityManager.createQuery(criteria).getResultList();
    }

    public ResultMessage deleteCompany(int id){
        // delete company
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        //create delete
        CriteriaDelete<Company> delete = builder.createCriteriaDelete(Company.class);
        //set the root class
        Root<Company> root = delete.from(Company.class);
        //setup update where clause
        //delete.where(builder.equal(root.get("id"), id));
        delete.where(builder.equal(root.get(Company_.id), id));

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            //perform the deletion
            entityManager.createQuery(delete).executeUpdate();
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to delete company id " + id);
        }
        finally {
            entityManager.close();
        }
        return new ResultMessage("Company with id " + id + " has been deleted from database");
    }

    public ResultMessage updateCompany(Company company){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        //create update
        CriteriaUpdate<Company> update = builder.createCriteriaUpdate(Company.class);
        //set the root class
        Root<Company> root = update.from(Company.class);
        //setup update where clause
        update.set(root.get(Company_.name), company.getName());
        update.where(builder.equal(root.get(Company_.id), company.getId()));


        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            //perform the update
            entityManager.createQuery(update).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to update company id " + company.getId());
        } finally {
            entityManager.close();
        }

        return new ResultMessage("Name of company id " + company.getId() + " has been updated");
    }

//*********************** Person related methods ************************************************
public List<Person> getPersons(int id, String name, String email, String phone){
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Person> criteria = builder.createQuery(Person.class);
    Root<Person> root = criteria.from(Person.class);
    criteria.select(root);

    List<Predicate> predicates = new ArrayList<>();
    if(id != 0) {
        Predicate idLike = builder.like(root.<Integer>get("id").as(String.class), "%" + id + "%");
        predicates.add(idLike);
    }
    if (!name.isEmpty()) {
        Predicate nameLike = builder.like(root.get(Person_.name), "%"+name+"%");
        predicates.add(nameLike);
    }
    if (!email.isEmpty()) {
        Predicate emailLike = builder.like(root.get(Person_.email), "%"+email+"%");
        predicates.add(emailLike);
    }
    if (!phone.isEmpty()) {
        Predicate phoneLike = builder.like(root.get(Person_.phone), "%"+phone+"%");
        predicates.add(phoneLike);
    }
    criteria.where(predicates.toArray(new Predicate[]{}));

    return entityManager.createQuery(criteria).getResultList();
}

    public ResultMessage postPerson(Person person){
        Person retPerson;
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //to avoid empty fields in db
        if(person.getName().isEmpty()){
            person.setName(UNKNOWN);
        }
        if(person.getEmail().isEmpty()){
            person.setEmail(UNKNOWN);
        }
        if(person.getPhone().isEmpty()){
            person.setPhone(UNKNOWN);
        }

        if(checkPerson(entityManager, person) != null){
            throw new AlreadyExistsException("At least one person with the same name, e-mail and phone already exists in database.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(person);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Person " + person.getName() + " has NOT been saved to database");
        }
        finally {
            //check id of the newly saved person
            retPerson = checkPerson(entityManager, person);
            entityManager.close();
        }

        return new ResultMessage("Person \"" + retPerson.getName() + "\" has been saved with id " + retPerson.getId());
    }

    private Person checkPerson(EntityManager em, Person person){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Person> criteria = builder.createQuery(Person.class);
        Root<Person> root = criteria.from(Person.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<>();
        Predicate nameLike = builder.equal(root.get(Person_.name), person.getName());
        predicates.add(nameLike);
        Predicate emailLike = builder.equal(root.get(Person_.email), person.getEmail());
        predicates.add(emailLike);
        Predicate phoneLike = builder.equal(root.get(Person_.phone), person.getPhone());
        predicates.add(phoneLike);

        criteria.where(predicates.toArray(new Predicate[]{}));

        //check usage of the person data first to ensure there is only one person of the same data
        List<Person> personList = (List<Person>) em.createQuery(criteria).getResultList();

        if(!personList.isEmpty()){
            return personList.get(0);
        } else {
            return null;
        }
    }

    public ResultMessage deletePerson(int id){
        // delete person
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        //create delete
        CriteriaDelete<Person> delete = builder.createCriteriaDelete(Person.class);
        //set the root class
        Root<Person> root = delete.from(Person.class);
        //delete.where(builder.equal(root.get("id"), id));
        delete.where(builder.equal(root.get(Person_.id), id));

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            //perform the deletion
            entityManager.createQuery(delete).executeUpdate();
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to delete person id " + id);
        }
        finally {
            entityManager.close();
        }
        return new ResultMessage("Person with id " + id + " has been deleted from database");
    }

    public ResultMessage updatePerson(Person person){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        //create update
        CriteriaUpdate<Person> update = builder.createCriteriaUpdate(Person.class);
        //set the root class
        Root<Person> root = update.from(Person.class);
        //setup update where clause
        update.set(root.get(Person_.name), person.getName());
        update.set(root.get(Person_.email), person.getEmail());
        update.set(root.get(Person_.phone), person.getPhone());
        update.where(builder.equal(root.get(Person_.id), person.getId()));

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            //perform the update
            entityManager.createQuery(update).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new UnableToPerformException("Unable to update person id " + person.getId());
        } finally {
            entityManager.close();
        }

        return new ResultMessage("Data of person id " + person.getId() + " has been updated");
    }

    //*********************** Application related methods ************************************************
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
    //*********************** Other methods ************************************************
    public List<StatusValue> getStatusList(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatusValue> criteria = builder.createQuery(StatusValue.class);
        Root<StatusValue> root = criteria.from(StatusValue.class);
        criteria.select(root);

        return entityManager.createQuery(criteria).getResultList();
    }
}
