package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
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
    public boolean isUsernameFree(String username) throws DataAccessException {
        return dao.countByProperty("username", username, User.class).equals(new Long(0));
    }

    @Override
    public UserDto getUser(String username) throws DataAccessException  {
        User u = dao.getByPropertyUnique("username", username, User.class);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public UserDto getUser(Long id) throws DataAccessException  {
        User u = dao.getById(id, User.class);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public Long createUser(UserDto userDto) throws DataAccessException  {
        return null;
    }

    @Override
    public Long createGeneratedUser(String emailAddress) throws DataAccessException  {

        if(emailAddress == null || !emailAddress.contains("@")){
            throw new DataAccessException("Invalid email address: " + emailAddress, DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if(dao.countByProperty("email", emailAddress, User.class) != 0){
            throw new DataAccessException("Email address " + emailAddress + " is already in DB!", DataAccessException.ErrorCode.BREAKING_UNIQUE_CONSTRAINT);
        }

        String username = emailAddress.substring(0, emailAddress.indexOf("@"));
        String usernameCandidate = username;
        int suffix = 1;

        /**
         * If the username is already in the database, add a suffix to id (create unique password).
         */
        while(!isUsernameFree(usernameCandidate)){
            usernameCandidate = username + suffix;
        }

        String password = PasswordGenerator.generatePassword(8);
        User.Builder builder = new User.Builder(usernameCandidate, emailAddress, password);

        Long id = dao.saveOrUpdate(builder.build()).getId();
        emailService.sendCredentials(usernameCandidate, password);
        return id;
    }

    @Override
    public void updateUser(UserDto userDto) throws DataAccessException  {

    }

    @Override
    public void deactivateUser(Long userId) throws DataAccessException  {

    }
}
