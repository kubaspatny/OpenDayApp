package cz.kubaspatny.opendayapp.service;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import cz.kubaspatny.opendayapp.bo.AndroidDevice;
import cz.kubaspatny.opendayapp.dto.AndroidDeviceDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 13/3/2015
 * Time: 22:34
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
@Service
public class GcmService extends DataAccessService implements IGcmService {

    public final static int TYPE_SYNC_ALL = 1;
    public final static int TYPE_SYNC_ROUTE = 2;
    public final static int TYPE_MESSAGE = 3;
    public final static int TYPE_LOCATION_UPDATE = 4;

    public final static String EXTRA_ROUTE_ID = "routeId";
    public final static String EXTRA_GROUP_BEFORE = "groupBefore";
    public final static String EXTRA_GROUP_AFTER = "groupAfter";
    public final static String EXTRA_STATION_ID = "stationId";
    public final static String EXTRA_UPDATE_TYPE = "updateType";
    public final static String EXTRA_MESSAGE = "message";
    public final static String EXTRA_NOTIFICATION_TYPE = "notificationType";



    @Value("${gcm.API_KEY}") private String API_KEY;

    @Async
    @Override
    public void sendNotification(Map<String, String> data, List<String> registrationIds) throws DataAccessException {

        if(registrationIds == null || registrationIds.isEmpty()) return;

        Sender sender = new Sender(API_KEY);
        Message message = new Message.Builder().setData(data).build();

        try {
            MulticastResult result = sender.send(message, registrationIds, 3);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void registerAndroidDevice(String registrationId) throws DataAccessException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if(registrationId == null || registrationId.isEmpty() || username == null || username.isEmpty()) throw new DataAccessException("Illegal argument.", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        AndroidDevice device = null;
        if(dao.countByProperty("registrationId", registrationId, AndroidDevice.class) != 0){
            device = dao.getByPropertyUnique("registrationId", registrationId, AndroidDevice.class);
            device.setUsername(username);
        }

        if(device == null) device = new AndroidDevice(registrationId, username);
        dao.saveOrUpdate(device);

    }

    @Override
    public List<String> getRegisteredDevices(String username) throws DataAccessException {

        Map<String, Object> whereParams = new HashMap<String, Object>();
        whereParams.put("username", username);

        List<AndroidDevice> devices = dao.searchByProperty(whereParams, AndroidDevice.class);

        if(devices.isEmpty()) return Collections.emptyList();
        List<String> resultDevices = new ArrayList<String>();
        for(AndroidDevice device : devices){
            resultDevices.add(device.getRegistrationId());
        }

        return resultDevices;

    }
}
