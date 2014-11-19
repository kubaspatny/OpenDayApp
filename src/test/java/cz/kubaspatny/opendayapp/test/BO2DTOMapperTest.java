package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 19/11/2014
 * Time: 21:58
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
public class BO2DTOMapperTest extends AbstractTest {

    @Autowired
    private GenericDao dao;

    private String username = "kuba.spatny@gmail.com";

    @Before
    public void setUp() throws Exception {
        User u = new User();
        u.setFirstName("Kuba");
        u.setLastName("Spatny");
        u.setUsername(username);
        u.setEmail(username);
        u.setPassword("password");
        u.setOrganization("Czech Technical University in Prague");
        u.setUserEnabled(true);
        u.addUserRole(User.UserRole.ROLE_ORGANIZER);

        dao.saveOrUpdate(u);

        Event event = new Event();
        event.setName("CTU DAY 1");
        event.setDate(DateTime.now(DateTimeZone.UTC));
        event.setInformation("CTU DAY is an annual conference for all people.");
        u.addEvent(event);

        Event event2 = new Event();
        event2.setName("CTU DAY 2");
        event2.setDate(DateTime.now(DateTimeZone.UTC));
        event2.setInformation("ANOTHER INFORMATION!");
        event2.setOrganizer(u);
        u.addEvent(event2);

        Route r1 = new Route();
        r1.setName("Blue route");
        r1.setDate(DateTime.now(DateTimeZone.UTC));
        r1.setHexColor("006040");
        r1.setInformation("This route is for STM and OI.");

        for (int i = 1; i <= 9; i++) {

            Station s = new Station();
            s.setName("Station " + i);
            s.setInformation("This is information #" + i);
            s.setLocation("ROOM E-00" + i);
            s.setSequencePosition(i);
            s.setRelocationTime(i*2);
            s.setTimeLimit(i*1000);

            r1.addStation(s);

        }

        Route r2 = new Route();
        r2.setName("Red route");
        r2.setDate(DateTime.now(DateTimeZone.UTC));
        r2.setHexColor("80FF20");
        r2.setInformation("This route is for Software Engineers only.");

        event.addRoute(r1);
        event.addRoute(r2);

        dao.saveOrUpdate(u);

        Station s = u.getEvents().get(0).getRoutes().get(0).getStations().get(2);
        s.setName("EDITED NAME");
        dao.saveOrUpdate(s);

    }

    @Test
    public void testUser() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        System.out.println(u.getUserRoles());

        UserDto userDto = UserDto.map(u, new UserDto(), null);
        Assert.assertNotNull(userDto.getId());
        Assert.assertNotNull(userDto.getUsername());
        Assert.assertNotNull(userDto.getEmail());
        Assert.assertNotNull(userDto.getFirstName());
        Assert.assertNotNull(userDto.getLastName());
        Assert.assertNotNull(userDto.getOrganization());
        Assert.assertNotNull(userDto.getUserRoles());
        Assert.assertNotNull(userDto.getEvents());
        System.out.println(userDto);

        userDto = UserDto.map(u, new UserDto(), DtoMapperUtil.getUserIgnoredProperties());
        Assert.assertNotNull(userDto.getId());
        Assert.assertNotNull(userDto.getUsername());
        Assert.assertNotNull(userDto.getEmail());
        Assert.assertNotNull(userDto.getFirstName());
        Assert.assertNotNull(userDto.getLastName());
        Assert.assertNotNull(userDto.getOrganization());
        Assert.assertNull(userDto.getUserRoles());
        Assert.assertNull(userDto.getEvents());
        System.out.println(userDto);

    }

    @Test
    public void testEvent() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        Event e = u.getEvents().get(0);

        EventDto eventDto = EventDto.map(e, new EventDto(), null);
        System.out.println(eventDto);

        Assert.assertNotNull(eventDto.getId());
        Assert.assertNotNull(eventDto.getName());
        Assert.assertNotNull(eventDto.getDate());
        Assert.assertNotNull(eventDto.getInformation());
        Assert.assertNotNull(eventDto.getOrganizer());
        Assert.assertNotNull(eventDto.getRoutes());

    }

    @Test
    public void testRoute() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        Route r = u.getEvents().get(0).getRoutes().get(0);

        RouteDto routeDto = RouteDto.map(r, new RouteDto(), null);
        System.out.println(routeDto);

        Assert.assertNotNull(routeDto.getId());
        Assert.assertNotNull(routeDto.getDate());
        Assert.assertNotNull(routeDto.getInformation());
        Assert.assertNotNull(routeDto.getName());
        Assert.assertNotNull(routeDto.getHexColor());
        Assert.assertNotNull(routeDto.getEvent());
        Assert.assertNotNull(routeDto.getStations());

    }

    @Test
    public void testStation() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        Station s = u.getEvents().get(0).getRoutes().get(0).getStations().get(0);

        StationDto stationDto = StationDto.map(s, new StationDto(), null);
        System.out.println(stationDto);

        Assert.assertNotNull(stationDto.getId());
        Assert.assertNotNull(stationDto.getName());
        Assert.assertNotNull(stationDto.getLocation());
        Assert.assertNotNull(stationDto.getInformation());
        Assert.assertNotNull(stationDto.getTimeLimit());
        Assert.assertNotNull(stationDto.getRelocationTime());
        Assert.assertNotNull(stationDto.getSequencePosition());
        Assert.assertNotNull(stationDto.getRoute());




    }
}
