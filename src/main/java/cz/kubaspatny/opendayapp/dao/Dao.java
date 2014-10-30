package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    public <ENTITY extends AbstractBusinessObject> ENTITY saveOrUpdate(ENTITY o) {

        if (o.getId() == null) {
            getEntityManager().persist(o);
        } else {
            getEntityManager().merge(o);
        }
        return o;

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void remove(ENTITY o) {
        getEntityManager().remove(o);
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void removeById(long id, Class<ENTITY> entity_class) {
        ENTITY e = getEntityManager().find(entity_class, id);
        if (e != null) {
            getEntityManager().remove(e);
        } else {
            // TODO: throw exception
        }
    }

    @Override
    public <ENTITY> ENTITY getById(Long id, Class<ENTITY> entity_class) {
        return getEntityManager().find(entity_class, id);
        //TODO: what does it return for non-existing ID?
    }

    @Override
    public <ENTITY> List<ENTITY> getAll(Class<ENTITY> entity_class) {
        return getEntityManager().createQuery("SELECT e FROM " + entity_class.getSimpleName() + " e order by e.id").getResultList();
    }

    @Override
    public <ENTITY> List<ENTITY> getPage(int page, int pageSize, Class<ENTITY> entity_class) {

        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e order by e.id";
        return getEntityManager().createQuery(queryString)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <ENTITY> List<ENTITY> getPage(int page, int pageSize, String sortBy, boolean ascending, Class<ENTITY> entity_class) {
        String queryString = "SELECT e FROM " + entity_class.getSimpleName() + " e order by e." + sortBy + (ascending ? " asc" : " desc");
        return getEntityManager().createQuery(queryString)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public <ENTITY> List<ENTITY> getPage(int page, int pageSize, Map<String, Object> parameters, String sortBy, boolean ascending, Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY> List<ENTITY> searchByProperty(Map<String, Object> properties, Class<ENTITY> entity_class) {
        return null;
    }
}