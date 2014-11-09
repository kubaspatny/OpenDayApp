package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.provider.HashProvider;
import cz.kubaspatny.opendayapp.provider.SaltProvider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 13:31
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
public class PasswordSavingTest extends AbstractTest {

    @Autowired
    private SaltProvider saltProvider;

    @Autowired
    private HashProvider hashProvider;

    @Test
    public void testSavePassword() throws Exception {

        String plain_password = "secret_user_password";

        // ------- SAVED IN DATABASE ------------
        String salt = hashProvider.hash(saltProvider.generateSalt(32));
        System.out.println("salt: " + salt);

        String hashed_password = hashProvider.hash(plain_password, salt);
        System.out.println("hashed password: " + hashed_password);
        // --------------------------------------

        // -------- TEST LOGIN ------------------

        Assert.assertEquals(hashed_password, hashProvider.hash("secret_user_password", salt));
        Assert.assertNotEquals(hashed_password, hashProvider.hash("secret_usre_password", salt));

    }
}
