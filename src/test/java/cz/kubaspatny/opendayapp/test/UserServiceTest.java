package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 17/11/2014
 * Time: 00:05
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
public class UserServiceTest extends AbstractTest {

    @Autowired private IUserService userService;
    @Autowired private GenericDao dao;

    private String username = "generic.username";
    private String email = "generic.username1@abcd.com";
    private String firstName = "FirstName";
    private String lastName = "LastName";
    private String org = "Organization";
    private String password = "genericPassword123";
    private Long userID;

    @Before
    public void setUp() throws Exception {

        User u =  new User.Builder(username, email, password).
                setFirstName(firstName).
                setLastName(lastName).
                setOrganization(org).
                addUserRole(User.UserRole.ROLE_ORGANIZER).addUserRole(User.UserRole.ROLE_STATIONMANAGER).build();

        userID = dao.saveOrUpdate(u).getId();

    }

    @Test
    public void testCreateGeneratedUser() throws Exception {
        String username = "opendayapptest@gmail.com";
        Long id = userService.createGeneratedUser(username);
        Assert.assertNotNull(id);

        User u = dao.getByPropertyUnique("username", "opendayapptest", User.class);
        Assert.assertNotNull(u);
        u.print();

        Assert.assertNotNull(dao.getById(id, User.class));
        Assert.assertNotNull(dao.getByPropertyUnique("email", username, User.class));

        Thread.sleep(5000); // make sure the async task is complete
    }

    /**
     * Service should throw an Exception if the email is already in the DB.
     * @throws Exception
     */
    @Test
    public void testCreateGeneratedUserExisting() throws Exception {

        try {
            userService.createGeneratedUser(email);
        } catch (DataAccessException e){
            Assert.assertTrue(e.getErrorCode() == DataAccessException.ErrorCode.BREAKING_UNIQUE_CONSTRAINT);
        }

        Long id = userService.createGeneratedUser("generic.username@abcd.com");
        User u = dao.getById(id, User.class);
        Assert.assertEquals("generic.username1", u.getUsername());
    }

    @Test
    public void testIsUserNameFree() throws Exception {

        Assert.assertFalse(userService.isUsernameFree(username));
        Assert.assertTrue(userService.isUsernameFree(username + System.nanoTime()));

    }

    @Test
    public void testGetUserByUsername() throws Exception {

        UserDto userDto = userService.getUser(username);
        System.out.println(userDto);

        Assert.assertNotNull(userDto);
        Assert.assertEquals(userID, userDto.getId());
        Assert.assertEquals(username, userDto.getUsername());
        Assert.assertEquals(email, userDto.getEmail());
        Assert.assertEquals(firstName, userDto.getFirstName());
        Assert.assertEquals(lastName, userDto.getLastName());
        Assert.assertEquals(org, userDto.getOrganization());

        Assert.assertTrue(userDto.getUserRoles().contains(User.UserRole.ROLE_ORGANIZER));
        Assert.assertTrue(userDto.getUserRoles().contains(User.UserRole.ROLE_STATIONMANAGER));
        Assert.assertFalse(userDto.getUserRoles().contains(User.UserRole.ROLE_GUIDE));


    }

    @Test
    public void testGetUserByID() throws Exception {

        UserDto userDto = userService.getUser(userID);
        System.out.println(userDto);

        Assert.assertNotNull(userDto);
        Assert.assertEquals(userID, userDto.getId());
        Assert.assertEquals(username, userDto.getUsername());
        Assert.assertEquals(email, userDto.getEmail());
        Assert.assertEquals(firstName, userDto.getFirstName());
        Assert.assertEquals(lastName, userDto.getLastName());
        Assert.assertEquals(org, userDto.getOrganization());

        Assert.assertTrue(userDto.getUserRoles().contains(User.UserRole.ROLE_ORGANIZER));
        Assert.assertTrue(userDto.getUserRoles().contains(User.UserRole.ROLE_STATIONMANAGER));
        Assert.assertFalse(userDto.getUserRoles().contains(User.UserRole.ROLE_GUIDE));

    }

    @Test
    public void testCreateUser() throws Exception {

        String newUsername = "user.name";
        String newPassword = "user_password";
        String newEmail = "user.mail@gmail.com";
        String newFirst = "first_name";
        String newLast = "last_name";
        String newOrganization = "organization_name";

        UserDto u = new UserDto();
        u.setUsername(newUsername);
        u.setPassword(newPassword);
        u.setEmail(newEmail);
        u.setFirstName(newFirst);
        u.setLastName(newLast);
        u.setOrganization(newOrganization);

        Long id = userService.createUser(u);
        Assert.assertNotNull(id);

        User user = dao.getById(id, User.class);
        Assert.assertNotNull(user);
        user.print();

        Assert.assertEquals(newUsername, user.getUsername());
        Assert.assertEquals(newEmail, user.getEmail());
        Assert.assertEquals(newFirst, user.getFirstName());
        Assert.assertEquals(newLast, user.getLastName());
        Assert.assertEquals(newOrganization, user.getOrganization());

    }
}




























