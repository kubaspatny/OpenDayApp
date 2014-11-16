package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.provider.HashProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 16:40
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
public class UserObjectTest extends AbstractTest {

    @Autowired private GenericDao dao;
    @Autowired private HashProvider hashProvider;

    private String username = "kuba.spatny@gmail.com";
    private String usernameAdmin = "admin@gmail.com";

    @Before
    public void setUp() throws Exception {
        User u = new User();
        u.setFirstName("Kuba");
        u.setLastName("Spatny");
        u.setUsername(username);
        u.setEmail(username);
        u.setPassword("password");
        u.setOrganization("Czech Technical University in Prague");
        u.setUserEnabled(true);

        dao.saveOrUpdate(u);

        User.Builder builder = new User.Builder(usernameAdmin, usernameAdmin, "password");
        builder.setFirstName("FIRSTNAME").setLastName("LASTNAME").setOrganization("CTU").addUserRole(User.UserRole.ROLE_GUIDE);
        dao.saveOrUpdate(builder.build());
    }

    @Test
    public void testCheckUserCredentials() throws Exception {
        User u = dao.getByPropertyUnique("username", username, User.class);
        Assert.assertNotEquals(u.getPassword(), hashProvider.hash("passwodr", u.getSalt()));
        Assert.assertEquals(u.getPassword(), hashProvider.hash("password",u.getSalt()));
    }

    @Test
    public void testChangeUserPassword() throws Exception {
        User u = dao.getByPropertyUnique("username", username, User.class);

        String firstSalt = u.getSalt();
        String firstPassword = u.getPassword();

        System.out.println(u);
        u.setPassword("newPassword");

        Assert.assertNotEquals(firstSalt, u.getSalt());
        Assert.assertNotEquals(firstPassword, u.getPassword());
        System.out.println(u);
    }

    @Test
    public void testBuilder() throws Exception {

        User u = dao.getByPropertyUnique("username", usernameAdmin, User.class);
        u.print();
        Assert.assertTrue(u.isLoginCorrect("password"));
        Assert.assertFalse(u.isLoginCorrect("passwodr"));

        u.addUserRole(User.UserRole.ROLE_GUIDE);
        u.addUserRole(User.UserRole.ROLE_ORGANIZER);
        dao.saveOrUpdate(u);

        u.print();

    }
}
