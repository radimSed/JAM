package com.jam.service;

import com.jam.dto.Company;
import com.jam.dto.Company_;
import com.jam.dto.ResultMessage;
import com.jam.exceptions.AlreadyExistsException;
import com.jam.exceptions.UnableToPerformException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JAMCompanyServiceV1 extends JAMAbstractServiceV1{

    public JAMCompanyServiceV1(){
        super();
    }

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
            Predicate idLike = builder.like(root.<Integer>get(Company_.id).as(String.class), "%" + id + "%");
            predicates.add(idLike);
        }
        if (!name.isEmpty()) {
            Predicate nameLike = builder.like(root.get(Company_.name), "%"+name+"%");
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
}
