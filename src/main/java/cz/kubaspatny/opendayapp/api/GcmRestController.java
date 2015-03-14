package cz.kubaspatny.opendayapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.kubaspatny.opendayapp.api.json.DateTimeSerializer;
import cz.kubaspatny.opendayapp.dto.LocationUpdateDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IGcmService;
import cz.kubaspatny.opendayapp.service.IGroupService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 14/3/2015
 * Time: 00:14
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
@RestController
@RequestMapping("/v1/gcm")
public class GcmRestController extends ExceptionHandlingController {

    @Autowired
    IGcmService gcmService;

    @RequestMapping(value = "/android-device", method = RequestMethod.POST)
     @ResponseBody
     public void registerDevice(@RequestBody String registrationId) throws DataAccessException {
        System.out.println("Registration id: " + registrationId);
        gcmService.registerAndroidDevice(registrationId);
    }

}
