package cz.kubaspatny.opendayapp.api;

import cz.kubaspatny.opendayapp.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 20/1/2015
 * Time: 21:14
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
@RequestMapping("/v1/time")
public class TimeRestController {

    @Autowired
    TestService testService;

    @RequestMapping(value = "/prague", method = RequestMethod.GET, produces = "text/plain")
    public String getTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
        return dateFormat.format(cal.getTime());
    }


    @RequestMapping(value = "/secured", method = RequestMethod.GET, produces = "text/plain")
    public String getSecuredTime(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
        return "SECURED TIME: " + dateFormat.format(cal.getTime());
    }

    @RequestMapping(value = "/secured2", method = RequestMethod.GET, produces = "application/json")
    public String getSecuredTime2(){

        testService.getSomeText();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
        return "SECURED TIME: " + dateFormat.format(cal.getTime());
    }

}

