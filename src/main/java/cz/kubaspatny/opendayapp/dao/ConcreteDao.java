package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.bo.Group;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 3/3/2015
 * Time: 20:13
 * Copyright 2015 Jakub Spatny
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

@Component("concreteDao")
public class ConcreteDao {

    @Autowired
    protected EntityManagerFactory entityManagerfactory;

    @Autowired
    protected GenericDao dao;

    protected EntityManager getEntityManager() {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerfactory); //entity manager with @Transactional support
    }


    public List<Group> getGroups(String username) throws DataAccessException {

        User u = dao.getByPropertyUnique("username", username, User.class);

        return getEntityManager().createQuery("SELECT e FROM Group e JOIN e.route r WHERE e.guide = :guide order by r.date")
                .setParameter("guide", u).getResultList();
    }

    public List<Group> getGroups(String username, int page, int pageSize) throws DataAccessException {

        User u = dao.getByPropertyUnique("username", username, User.class);

        if(page < 0){
            throw new DataAccessException("Negative page number!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if(pageSize <= 0) {
            throw new DataAccessException("Non-positive pageSize!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        return getEntityManager().createQuery("SELECT e FROM Group e JOIN e.route r WHERE e.guide = :guide order by r.date")
                .setParameter("guide", u)
                .setFirstResult(page*pageSize)
                .setMaxResults(pageSize)
                .getResultList();

    }

    public List<Group> getUpcomingEventsGroups(String username) throws DataAccessException {

        User u = dao.getByPropertyUnique("username", username, User.class);

        return getEntityManager().createQuery("SELECT e FROM Group e JOIN e.route r WHERE e.guide = :guide and r.date >= :date order by r.date")
                .setParameter("guide", u).setParameter("date", new DateTime().withTime(0,0,0,0)).getResultList();
    }

    public Long countGroups(String username) throws DataAccessException {
        User u = dao.getByPropertyUnique("username", username, User.class);

        Query q = getEntityManager().createQuery("SELECT count(distinct e) FROM Group e JOIN e.route r WHERE e.guide = :guide")
                .setParameter("guide", u);

        try {
            return (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e){
            return 0l;
        }

    }

    public Long countUpcomingEventsGroups(String username) throws DataAccessException {
        User u = dao.getByPropertyUnique("username", username, User.class);

        Query q = getEntityManager().createQuery("SELECT count(distinct e) FROM Group e JOIN e.route r WHERE e.guide = :guide and r.date >= :date")
                .setParameter("guide", u).setParameter("date", new DateTime().withTime(0,0,0,0));

        try {
            return (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e){
            return 0l;
        }

    }

}
