package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import cz.kubaspatny.opendayapp.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    public boolean isEmailFree(String email) throws DataAccessException {
        return dao.countByProperty("email", email, User.class).equals(new Long(0));
    }

    @Override
    public UserDto getUser(String username) throws DataAccessException  {
        User u = dao.getByPropertyUnique("username", username, User.class);
        if(!u.isUserEnabled()) throw new DataAccessException("User was deactivated!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public UserDto getUser(Long id) throws DataAccessException  {
        User u = dao.getById(id, User.class);
        if(!u.isUserEnabled()) throw new DataAccessException("User was deactivated!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public Long createUser(UserDto userDto) throws DataAccessException  {
        return dao.saveOrUpdate(UserDto.map(userDto, new User(), null)).getId();
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

        if(userDto.getId() == null) throw new DataAccessException("Trying to update object with null ID!", DataAccessException.ErrorCode.INVALID_ID);
        User u = dao.getById(userDto.getId(), User.class);

        List<String> ignoredProperties = new ArrayList<String>();
        ignoredProperties.add("id");
        ignoredProperties.add("username");
        ignoredProperties.add("email");
        ignoredProperties.add("password");

        dao.saveOrUpdate(UserDto.map(userDto, u, ignoredProperties));

    }

    @Override
    public void deactivateUser(Long userId) throws DataAccessException  {

        User u = dao.getById(userId, User.class);
        u.setUserEnabled(false);
        dao.saveOrUpdate(u);

    }
}
