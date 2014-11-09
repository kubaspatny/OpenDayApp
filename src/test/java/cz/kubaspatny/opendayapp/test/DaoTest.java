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
import javax.persistence.Query;
import java.util.*;

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
    private List<String> colors;
    private List<String> types;

    @Autowired private GenericDao dao;
    @Autowired protected EntityManagerFactory entityManagerfactory;

    protected EntityManager getEntityManager() {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerfactory);
    }

    public DaoTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {

        ids = new ArrayList<Long>();

        colors = new ArrayList<String>();
        colors.add("red");
        colors.add("green");
        colors.add("blue");

        types = new ArrayList<String>();
        types.add("FULL");
        types.add("TRIAL");

        for (int i = 1; i <= 100; i++) {
            ids.add(getEntityManager().merge(new MockBusinessObject("TEXT " + i,
                            (i % 4) + 1,
                            colors.get(i % 3),
                            types.get(i % 2))
            ).getId());
        }

    }

    @Test
    public void testSave() throws Exception {

        // ------- SAVE CORRECT OBJECT --------------
        Assert.assertNotNull(dao);

        MockBusinessObject obj = new MockBusinessObject("Test Object",1, "red", "full");
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
            Assert.assertTrue(e.getErrorCode() == DaoException.DaoErrorCode.DETACHED_INSTANCE);
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

    @Test
    public void testGetByPropertyUnique() throws Exception {

        // ----  GETTING OBJECT WITH EXISTING ID ----

        MockBusinessObject obj = dao.getByPropertyUnique("id", ids.get(0), MockBusinessObject.class);
        Assert.assertNotNull(obj);
        Assert.assertEquals(ids.get(0), obj.getId());

        // ----  GETTING OBJECT WITH NONEXISTENT ID ----

        try {
            dao.getByPropertyUnique("id", System.nanoTime(), MockBusinessObject.class);
            Assert.fail("Should have thrown expection by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.INSTANCE_NOT_FOUND, e.getErrorCode());
        }

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
    public void testGetPage() throws Exception {

        // ---- GET FULL PAGE ----

        List<MockBusinessObject> list = dao.getPage(0, 25, MockBusinessObject.class);
        Assert.assertEquals(25, list.size());
        System.out.println("Size: " + list.size());
        for(MockBusinessObject o : list){
            System.out.println(o);
        }

        // ---- GET FULL PAGE ----

        list = dao.getPage(1, 40, MockBusinessObject.class);
        Assert.assertEquals(40, list.size());
        System.out.println("Size: " + list.size());
        for(MockBusinessObject o : list){
            System.out.println(o);
        }

        // ---- GET PARTIALLY FULL PAGE ----

        list = dao.getPage(1, 80, MockBusinessObject.class);
        Assert.assertEquals(20, list.size());
        System.out.println("Size: " + list.size());
        for(MockBusinessObject o : list){
            System.out.println(o);
        }

        // ---- GET NEGATIVE PAGE ----

        try {
            dao.getPage(-1, 80, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        // ---- GET NEGATIVE SIZE ----

        try {
            dao.getPage(1, -80, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

    }

    @Test
    public void testGetPageWithParameters() throws Exception {

        int category = 1;
        String color = "blue";


        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("category", category);
        map.put("color", color);

        List<MockBusinessObject> list = dao.getPage(0, 25, map, "id", true, MockBusinessObject.class);

        Assert.assertTrue(list.size() <= 25);

        for(MockBusinessObject o : list){
            Assert.assertEquals(category, o.getCategory());
            Assert.assertEquals(color, o.getColor());
        }

        // ---- GET NEGATIVE PAGE ----

        try {
            dao.getPage(-1, 80, map, "id", true, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        // ---- GET NEGATIVE SIZE ----

        try {
            dao.getPage(1, -80, map, "id", true, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        // ---- GET NULL MAP ----

        try {
            dao.getPage(1, 80, null, "id", true, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        // ---- GET NULL PARAMETER ----

        try {
            dao.getPage(1, 80, map, null, true, MockBusinessObject.class);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DaoException e){
            Assert.assertEquals(DaoException.DaoErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

    }

    @Test
    public void testSearchByProperty() throws Exception {

        String color = "green";
        String type = "TRIAL";

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("color", color);

        List<MockBusinessObject> list = dao.searchByProperty(map, MockBusinessObject.class);

        for(MockBusinessObject o : list){
            Assert.assertEquals(color, o.getColor());
            Assert.assertEquals(type, o.getType());
        }



    }
}

































