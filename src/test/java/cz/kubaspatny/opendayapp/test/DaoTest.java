package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.MockBusinessObject;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.exception.DaoException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 30/10/2014
 * Time: 22:05
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
public class DaoTest extends AbstractTest {

    private List<Long> ids;

    @Autowired
    private GenericDao dao;

    @Autowired
    protected EntityManagerFactory entityManagerfactory;

    protected EntityManager getEntityManager() {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerfactory);
    }

    public DaoTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {

        ids = new ArrayList<Long>();

        for (int i = 1; i <= 100; i++) {
            ids.add(getEntityManager().merge(new MockBusinessObject("TEXT " + i, (i % 4) + 1)).getId());
        }

    }

    @Test
    public void testSave() throws Exception {

        // ------- SAVE CORRECT OBJECT --------------
        Assert.assertNotNull(dao);

        MockBusinessObject obj = new MockBusinessObject("Test Object",1);
        System.out.println(obj);
        Assert.assertNull(obj.getId());
        dao.saveOrUpdate(obj);
        Assert.assertNotNull(obj.getId());

        System.out.println(obj);

        // ------- SAVE NULL OBJECT --------------
        try {
            dao.saveOrUpdate(null);
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

    }

    @Test
    public void testUpdate() throws Exception {

        // ------- UPDATING EXISTING OBJECT --------------

        String new_value = "TEST TEXT 2 (UPDATED)";
        MockBusinessObject obj = getEntityManager().find(MockBusinessObject.class, ids.get(0));

        Assert.assertNotNull(obj);
        Assert.assertNotEquals(new_value, obj.getText());

        System.out.println(obj);
        obj.setText(new_value);
        dao.saveOrUpdate(obj);
        Assert.assertEquals(new_value, obj.getText());
        System.out.println(obj);

    }

    @Test
    public void testGetAll() throws Exception {

        List<MockBusinessObject> objectList = dao.getAll(MockBusinessObject.class);
        Assert.assertEquals(ids.size(), objectList.size());
        for(MockBusinessObject o : objectList){
            System.out.println(o);
        }

    }

    @Test
    public void testRemove() throws Exception {

        // ---- REMOVING EXISTING OBJECT ----

        MockBusinessObject obj = getEntityManager().find(MockBusinessObject.class, ids.get(0));
        Assert.assertNotNull(obj);

        dao.remove(obj);

        obj = getEntityManager().find(MockBusinessObject.class, ids.get(0));
        Assert.assertNull(obj);

        // ---- REMOVING NULL OBJECT ----

        try {
            dao.remove(null);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertTrue(e.getErrorCode() == DaoException.DaoErrorCode.ILLEGAL_ARGUMENT);
        }

        // ---- REMOVING NONEXISTENT(NOT PERSISTED) OBJECT ----

        try {
            obj = new MockBusinessObject(System.nanoTime(), "Remove nonexistent object.");
            dao.remove(obj);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertTrue(e.getErrorCode() == DaoException.DaoErrorCode.ILLEGAL_ARGUMENT);
        }



    }

    @Test
    public void testRemoveById() throws Exception {

        // ---- REMOVE OBJECT WITH NULL ID ----
        try {
            dao.removeById(null, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        // ---- REMOVE OBJECT WITH EXISTING ID ----

        dao.removeById(ids.get(0), MockBusinessObject.class);
        Assert.assertNull(getEntityManager().find(MockBusinessObject.class, ids.get(0)));

        // ---- REMOVE OBJECT WITH NONEXISTENT ID ----


        try {
            dao.removeById(System.nanoTime(), MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.INVALID_ID, e.getErrorCode());
        }





    }

    @Test
    public void testGetById() throws Exception {

        // ---- GETTING OBJECT WITH NULL ID ----

        try {
            dao.getById(null, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        // ----  GETTING OBJECT WITH EXISTING ID ----

        MockBusinessObject obj = dao.getById(ids.get(0), MockBusinessObject.class);
        Assert.assertNotNull(obj);
        Assert.assertEquals(ids.get(0), obj.getId());

        // ----  GETTING OBJECT WITH NONEXISTENT ID ----

        Assert.assertNull(dao.getById(System.nanoTime(), MockBusinessObject.class));

    }
}
