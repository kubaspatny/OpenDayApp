package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 26/10/2014
 * Time: 18:37
 * Copyright 2014 Jakub Spatny
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Component("genericDao")
public class Dao implements GenericDao {


    @Autowired
    protected EntityManagerFactory entityManagerfactory;

    protected EntityManager getEntityManager() {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerfactory); //entity manager with @Transactional support
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> ENTITY saveOrUpdate(ENTITY o) throws DataAccessException {

        if(o == null){
            throw new DataAccessException("Passed object is null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if (o.getId() == null) {
            getEntityManager().persist(o);
        } else {
            getEntityManager().merge(o);
        }
        return o;

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void remove(ENTITY o) throws DataAccessException {

        if(o == null) throw new DataAccessException("Object parameter cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        try {
            getEntityManager().remove(o);
        } catch(InvalidDataAccessApiUsageException e){
            throw new DataAccessException("Couldn't remove object!", DataAccessException.ErrorCode.DETACHED_INSTANCE);
        }

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void removeById(Long id, Class<ENTITY> entity_class) throws DataAccessException {

        if(id == null){
            throw new DataAccessException("ID cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        ENTITY e = getEntityManager().find(entity_class, id);
        if (e != null) {
            getEntityManager().remove(e);
        } else {
            throw new DataAccessException("Object with id: " + id + " doesn't exist!", DataAccessException.ErrorCode.INVALID_ID);
        }
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> ENTITY getById(Long id, Class<ENTITY> entity_class) throws DataAccessException {
        if(id == null){
            throw new DataAccessException("ID cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        return getEntityManager().find(entity_class, id);
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> ENTITY getByPropertyUnique(String property, Object value, Class<ENTITY> entity_class) throws DataAccessException {

        if(property == null || value == null){
            throw new DataAccessException("Search paramaters cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e WHERE e." + property + " = :" + property;
        Query q = getEntityManager().createQuery(queryString).setParameter(property, value);

        try {
            return (ENTITY) q.getSingleResult();
        } catch (EmptyResultDataAccessException e){
            throw new DataAccessException("Couldn't find an instance with " + property +" equal to " + value, DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        }

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getAll(Class<ENTITY> entity_class) {
        return getEntityManager().createQuery("SELECT e FROM " + entity_class.getSimpleName() + " e order by e.id").getResultList();
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Class<ENTITY> entity_class) throws DataAccessException {

        if(page < 0){
            throw new DataAccessException("Negative page number!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if(pageSize <= 0) {
            throw new DataAccessException("Non-positive pageSize!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e order by e.id";
        return getEntityManager().createQuery(queryString)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, String sortBy, boolean ascending, Class<ENTITY> entity_class) throws DataAccessException {

        if(page < 0){
            throw new DataAccessException("Negative page number!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        } else if(pageSize <= 0) {
            throw new DataAccessException("Non-positive pageSize!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        } else if(sortBy == null){
            throw new DataAccessException("Parameter sortBy cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e order by e." + sortBy + (ascending ? " asc" : " desc");
        return getEntityManager().createQuery(queryString)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Map<String, Object> parameters, String sortBy, boolean ascending, Class<ENTITY> entity_class) throws DataAccessException {

        if(page < 0){
            throw new DataAccessException("Negative page number!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        } else if(pageSize <= 0) {
            throw new DataAccessException("Non-positive pageSize!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        } else if(sortBy == null){
            throw new DataAccessException("Parameter sortBy cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        } else if(parameters == null){
            throw new DataAccessException("Parameter map for filtering cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        StringBuilder s = new StringBuilder();
        Iterator<Map.Entry<String, Object>> it = parameters.entrySet().iterator();
        while(it.hasNext()){
            String key = it.next().getKey();
            s.append("e.").append(key).append(" = :").append(key);
            if(it.hasNext()) s.append(" and ");
        }

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e WHERE " + s.toString() + " order by e." + sortBy + (ascending ? " asc" : " desc");
        Query q = getEntityManager().createQuery(queryString)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize);

        it = parameters.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Object> e = it.next();
            q.setParameter(e.getKey(), e.getValue());
        }

        return q.getResultList();


    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> searchByProperty(Map<String, Object> parameters, Class<ENTITY> entity_class) throws DataAccessException {

        if(parameters == null){
            throw new DataAccessException("Parameters cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        StringBuilder s = new StringBuilder();
        Iterator<Map.Entry<String, Object>> it = parameters.entrySet().iterator();
        while(it.hasNext()){
            String key = it.next().getKey();
            s.append("e.").append(key).append(" = :").append(key);
            if(it.hasNext()) s.append(" and ");
        }

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e WHERE " + s.toString();
        Query q = getEntityManager().createQuery(queryString);


        it = parameters.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Object> e = it.next();
            q.setParameter(e.getKey(), e.getValue());
        }

        return q.getResultList();
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> Long countByProperty(String property, Object value, Class<ENTITY> entity_class) throws DataAccessException {

        if(property == null || value == null){
            throw new DataAccessException("Search paramaters cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        String queryString = "SELECT COUNT(e) FROM " + entity_class.getSimpleName() + " e WHERE e." + property + " = :" + property;
        Query q = getEntityManager().createQuery(queryString).setParameter(property, value);

        try {
            return (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e){
            throw new DataAccessException("Error counting instances with " + property +" equal to " + value, DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        }

    }
}

























