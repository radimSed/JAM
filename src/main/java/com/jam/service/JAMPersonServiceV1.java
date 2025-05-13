package com.jam.service;

import com.jam.dto.Person;
import com.jam.dto.Person_;
import com.jam.dto.ResultMessage;
import com.jam.exceptions.AlreadyExistsException;
import com.jam.exceptions.UnableToPerformException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JAMPersonServiceV1{
    private final String UNKNOWN = "Unknown";

    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
        List<Person> personList = em.createQuery(criteria).getResultList();

        if(!personList.isEmpty()){
            return personList.getFirst();
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

}
