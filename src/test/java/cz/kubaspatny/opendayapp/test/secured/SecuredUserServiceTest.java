package cz.kubaspatny.opendayapp.test.secured;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.service.IUserService;
import cz.kubaspatny.opendayapp.test.AbstractSecuredTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 2/1/2015
 * Time: 18:52
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
public class SecuredUserServiceTest extends AbstractSecuredTest {

    @Autowired private GenericDao dao;
    @Autowired private IUserService userService;

    @Test
    public void testCreateUserAndGet() throws Exception {

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

        try {
            setAnonymousUser();
            userService.getUser(id);
            Assert.fail("Should HAVE thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(newUsername);
            userService.getUser(id);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown an exception!");
        }

    }

    @Test
    public void testCreateGeneratedUserAndGet() throws Exception {

        String newEmail = "guide1@ggg.com";
        userService.createGeneratedUser(newEmail);

        try {
            setAnonymousUser();
            userService.getUser("guide1");
            Assert.fail("Should HAVE thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("guide1");
            userService.getUser("guide1");
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown an exception!");
        }

    }

    @Test
    public void testUpdateUser() throws Exception {

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
        setUser(newUsername);
        u = userService.getUser(id);
        u.setOrganization("new org");

        try {
            setAnonymousUser();
            userService.updateUser(u);
            Assert.fail("Should HAVE thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(newUsername);
            userService.updateUser(u);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown an exception!");
        }

    }

    @Test
    public void testDeactivateUser() throws Exception {

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

        try {
            setAnonymousUser();
            userService.deactivateUser(id);
            Assert.fail("Should HAVE thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(newUsername);
            userService.deactivateUser(id);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown an exception!");
        }

    }

}
