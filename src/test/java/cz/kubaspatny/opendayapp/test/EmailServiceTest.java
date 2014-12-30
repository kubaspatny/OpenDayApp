package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.service.EmailService;
import cz.kubaspatny.opendayapp.utils.PasswordGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:56
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
public class EmailServiceTest extends AbstractTest {

    @Autowired private EmailService emailService;

    @Test
    public void testSendEmail() throws Exception {

        emailService.sendCredentials("kuba.spatny", "kuba.spatny@gmail.com", PasswordGenerator.generatePassword(8));
        Thread.sleep(10000);

    }

    @Test
    public void testGeneratePassword() throws Exception {

        for (int i = 0; i < 10; i++) {

            System.out.println(PasswordGenerator.generatePassword(8));

        }

    }
}
