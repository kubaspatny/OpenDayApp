package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import cz.kubaspatny.opendayapp.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
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
        return dao.countByProperty("email", email.toLowerCase(), User.class).equals(new Long(0));
    }

    @Override
    public UserDto getUser(String username) throws DataAccessException  {
        User u = dao.getByPropertyUnique("username", username, User.class);
        if(u == null || !u.isUserEnabled()) throw new DataAccessException("User was deactivated!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public UserDto getUserByEmail(String email) throws DataAccessException {
        User u = dao.getByPropertyUnique("email", email.toLowerCase(), User.class);
        if(u == null || !u.isUserEnabled()) throw new DataAccessException("User was deactivated!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public UserDto getUser(Long id) throws DataAccessException  {
        User u = dao.getById(id, User.class);
        if(u == null || !u.isUserEnabled()) throw new DataAccessException("User was deactivated!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return UserDto.map(u, new UserDto(), null);
    }

    @Override
    public Long createUser(UserDto userDto) throws DataAccessException  {

        if(userDto.getUsername() == null || !userDto.isPasswordSet() || userDto.getEmail() == null){
            throw new DataAccessException("Trying to save object with null values for non-nullable fields!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if(!isUsernameFree(userDto.getUsername()) || !isEmailFree(userDto.getUsername())){
            throw new DataAccessException("Trying to save object which breaks unique constraint!", DataAccessException.ErrorCode.BREAKING_UNIQUE_CONSTRAINT);
        }

        Long userId = dao.saveOrUpdate(UserDto.map(userDto, new User(), null)).getId();

        saveOrUpdateACL(new ObjectIdentityImpl(User.class, userId), null, false);
        addPermission(new ObjectIdentityImpl(User.class, userId), new PrincipalSid(userDto.getUsername()), BasePermission.READ);
        addPermission(new ObjectIdentityImpl(User.class, userId), new PrincipalSid(userDto.getUsername()), BasePermission.WRITE);

        return userId;
    }

    @Override
    public Long createGeneratedUser(String emailAddress) throws DataAccessException  {

        if(emailAddress == null || !emailAddress.contains("@")){ // TODO validate using EmailFormatValidator
            throw new DataAccessException("Invalid email address: " + emailAddress, DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if(!isEmailFree(emailAddress)){
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
            suffix++;
        }

        String password = PasswordGenerator.generatePassword(8);
        User.Builder builder = new User.Builder(usernameCandidate, emailAddress.toLowerCase(), password);
        builder.addUserRole(User.UserRole.ROLE_USER);
        builder.addUserRole(User.UserRole.ROLE_GUIDE);
        builder.addUserRole(User.UserRole.ROLE_STATIONMANAGER);

        Long id = dao.saveOrUpdate(builder.build()).getId();

        saveOrUpdateACL(new ObjectIdentityImpl(User.class, id), null, false);
        addPermission(new ObjectIdentityImpl(User.class, id), new PrincipalSid(usernameCandidate), BasePermission.READ);
        addPermission(new ObjectIdentityImpl(User.class, id), new PrincipalSid(usernameCandidate), BasePermission.WRITE);

        try{
            emailService.sendCredentials(usernameCandidate, emailAddress, password);
        } catch (Exception e){
            throw new RuntimeException(e.getLocalizedMessage());
        }

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
    public void changePassword(Long userId, String oldPassword, String newPassword) throws DataAccessException {

        User u = dao.getById(userId, User.class);
        if(u == null) throw new DataAccessException("User not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        if(u.isLoginCorrect(oldPassword)){
            u.setPassword(newPassword);
            dao.saveOrUpdate(u);
        } else {
            throw new DataAccessException("Wrong password!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

    }

    @Override
    public void deactivateUser(Long userId) throws DataAccessException  {

        User u = dao.getById(userId, User.class);
        u.setUserEnabled(false);
        dao.saveOrUpdate(u);

    }
}
