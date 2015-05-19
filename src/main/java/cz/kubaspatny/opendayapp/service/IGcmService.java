package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.AndroidDeviceDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 13/3/2015
 * Time: 22:27
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
@Transactional
public interface IGcmService {

    /**
     * Sends a notification.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void sendNotification(Map<String, String> data, List<String> registrationIds) throws DataAccessException;


    /**
     * Registers a device with #registrationId for current logged in user.
     */
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    public void registerAndroidDevice(String registrationId) throws DataAccessException;

    /**
     * Registers a list of registered devices for given username.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<String> getRegisteredDevices(String username) throws DataAccessException;

}
