package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.junit.Assert;
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

    @Test
    public void testCreateGeneratedUser() throws Exception {
        String username = "opendayapptest@gmail.com";
        Long id = userService.createGeneratedUser(username);
        Assert.assertNotNull(id);

        User u = dao.getByPropertyUnique("username", username, User.class);
        Assert.assertNotNull(u);
        u.print();

        Assert.assertNotNull(dao.getById(id, User.class));
        Assert.assertNotNull(dao.getByPropertyUnique("email", username, User.class));

        Thread.sleep(5000); // make sure the async task is complete
    }
}
