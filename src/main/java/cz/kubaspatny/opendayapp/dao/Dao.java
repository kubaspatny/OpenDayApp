package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.exception.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    public <ENTITY extends AbstractBusinessObject> ENTITY saveOrUpdate(ENTITY o) throws DaoException {

        if(o == null){
            throw new DaoException("Passed object is null!", DaoException.DaoErrorCode.ILLEGAL_ARGUMENT);
        }

        if (o.getId() == null) {
            getEntityManager().persist(o);
        } else {
            getEntityManager().merge(o);
        }
        return o;

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void remove(ENTITY o) throws DaoException {

        try {
            getEntityManager().remove(o);
        } catch(InvalidDataAccessApiUsageException e){
            throw new DaoException("Couldn't remove object!", DaoException.DaoErrorCode.ILLEGAL_ARGUMENT);
        }

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void removeById(Long id, Class<ENTITY> entity_class) throws DaoException {

        if(id == null){
            throw new DaoException("ID cannot be null!", DaoException.DaoErrorCode.ILLEGAL_ARGUMENT);
        }

        ENTITY e = getEntityManager().find(entity_class, id);
        if (e != null) {
            getEntityManager().remove(e);
        } else {
            throw new DaoException("Object with id: " + id + " doesn't exist!", DaoException.DaoErrorCode.INVALID_ID);
        }
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> ENTITY getById(Long id, Class<ENTITY> entity_class) throws DaoException {
        if(id == null){
            throw new DaoException("ID cannot be null!", DaoException.DaoErrorCode.ILLEGAL_ARGUMENT);
        }

        return getEntityManager().find(entity_class, id);
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getAll(Class<ENTITY> entity_class) {
        return getEntityManager().createQuery("SELECT e FROM " + entity_class.getSimpleName() + " e order by e.id").getResultList();
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Class<ENTITY> entity_class) {

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e order by e.id";
        return getEntityManager().createQuery(queryString)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, String sortBy, boolean ascending, Class<ENTITY> entity_class) {
        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e order by e." + sortBy + (ascending ? " asc" : " desc");
        return getEntityManager().createQuery(queryString)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Map<String, Object> parameters, String sortBy, boolean ascending, Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> searchByProperty(Map<String, Object> properties, Class<ENTITY> entity_class) {
        return null;
    }
}