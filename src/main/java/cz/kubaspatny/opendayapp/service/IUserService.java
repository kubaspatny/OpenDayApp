package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
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

    @Transactional(readOnly = true)
    public boolean isUsernameFree(String username) throws DataAccessException;

    @Transactional(readOnly = true)
    public UserDto getUser(String username) throws DataAccessException;

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) throws DataAccessException;

    public Long createUser(UserDto userDto) throws DataAccessException;

    public Long createGeneratedUser(String emailAddress) throws DataAccessException;

    public void updateUser(UserDto userDto) throws DataAccessException;

    public void deactivateUser(Long userId) throws DataAccessException;



}
