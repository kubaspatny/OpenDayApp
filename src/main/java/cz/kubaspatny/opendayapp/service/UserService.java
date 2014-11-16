package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DaoException;
import cz.kubaspatny.opendayapp.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 20:11
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

@Component("userService")
public class UserService extends DataAccessService implements IUserService {

    @Autowired private EmailService emailService;

    @Override
    public boolean isUsernameFree(String username) {
        return false;
    }

    @Override
    public UserDto getUser(String username) {
        return null;
    }

    @Override
    public UserDto getUser(Long id) {
        return null;
    }

    @Override
    public Long createUser(UserDto userDto) {
        return null;
    }

    @Override
    public Long createGeneratedUser(String emailAddress) {
        String password = PasswordGenerator.generatePassword(8);
        User.Builder builder = new User.Builder(emailAddress, emailAddress, password);

        try {
            Long id = dao.saveOrUpdate(builder.build()).getId();
            emailService.sendCredentials(emailAddress, password);
            return id;
        } catch (DaoException e){
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void updateUser(UserDto userDto) {

    }

    @Override
    public void deactivateUser(Long userId) {

    }
}
