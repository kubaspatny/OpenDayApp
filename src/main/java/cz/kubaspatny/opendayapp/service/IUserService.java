package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 20:14
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
@Transactional
public interface IUserService {

    /**
     * Checks if user with @username already exists in the database. Method should be called before calling @createUser.
     * @return true if no user with @username exists in the database
     */
    @Transactional(readOnly = true)
    public boolean isUsernameFree(String username) throws DataAccessException;

    /**
     * Checks if user with @email already exists in the database. Method should be called before calling @createGeneratedUser.
     * @return true if no user with @email exists in the database
     */
    @Transactional(readOnly = true)
    public boolean isEmailFree(String email) throws DataAccessException;

    /**
     * Returns a user with @username.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public UserDto getUser(String username) throws DataAccessException;

    /**
     * Returns a user with @email.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public UserDto getUserByEmail(String email) throws DataAccessException;

    /**
     * Returns a user with @id.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public UserDto getUser(Long id) throws DataAccessException;

    /**
     * Creates a new user profile.
     */
    public Long createUser(UserDto userDto) throws DataAccessException;

    /**
     * Creates a user profile for specified email address. A unique username is generated from the email address, which
     * is then send via email together with a generated alphanumeric password to the  @emailAdress.
     * @throws DataAccessException If @emailAddress in not in valid format, or a user with this address already exists
     */
    public Long createGeneratedUser(String emailAddress) throws DataAccessException;

    /**
     * Updates an existing user. @userDto.id cannot be null!
     */
    @PreAuthorize("hasPermission(#userDto.id, #userDto.ACLObjectIdentityClass, 'WRITE')")
    public void updateUser(UserDto userDto) throws DataAccessException;

    /**
     * Changes user password. @id cannot be null!
     */
    @PreAuthorize("hasPermission(#userId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('User'), 'WRITE')")
    public void changePassword(Long userId, String oldPassword, String newPassword) throws DataAccessException;

    /**
     * Deactivates user with @userId.
     */
    @PreAuthorize("hasPermission(#userId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('User'), 'WRITE')")
    public void deactivateUser(Long userId) throws DataAccessException;

}
