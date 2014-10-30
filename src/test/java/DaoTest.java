import cz.kubaspatny.opendayapp.bo.MockBusinessObject;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private GenericDao dao;

    public DaoTest() {
        super();
    }

    @Test
    public void testSave() throws Exception {
        Assert.assertNotNull(dao);

        MockBusinessObject obj = new MockBusinessObject("Test Object");
        obj = dao.saveOrUpdate(obj);
        Assert.assertNotNull(obj.getId());
        System.out.println("New Object Id: " + obj.getId());
    }

    @Test
    public void testUpdate() throws Exception {

        String old_value = "TEST TEXT 1";
        String new_value = "TEST TEXT 2 (UPDATED)";

        MockBusinessObject obj = new MockBusinessObject(old_value);
        obj = dao.saveOrUpdate(obj);

        Assert.assertNotNull(obj.getId());
        System.out.println(obj.getId() + ": " + obj.getText());

        obj.setText(new_value);
        obj = dao.saveOrUpdate(obj);
        Assert.assertEquals(new_value, obj.getText());
        System.out.println(obj.getId() + ": " + obj.getText());

    }
}
