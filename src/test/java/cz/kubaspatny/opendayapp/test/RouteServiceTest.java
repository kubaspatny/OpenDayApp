package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 24/11/2014
 * Time: 21:48
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
public class RouteServiceTest extends AbstractTest {

    @Autowired private GenericDao dao;
    @Autowired private IRouteService routeService;
    @Autowired private IUserService userService;

    private String username = "kuba.spatny@gmail.com";

    private String guide1 = "guide1@gmail.com";
    private String guide2 = "guide2@gmail.com";
    private String guide3 = "guide3@gmail.com";
    private String guide4 = "guide4@gmail.com";

    private String stationManager1 = "stationManager1@gmail.com";
    private String stationManager2 = "stationManager2@gmail.com";
    private String stationManager3 = "stationManager3@gmail.com";
    private String stationManager4 = "stationManager4@gmail.com";

    private Long routeId;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(dao);
        Assert.assertNotNull(routeService);

        User u = new User();
        u.setFirstName("Kuba");
        u.setLastName("Spatny");
        u.setUsername(username);
        u.setEmail(username);
        u.setPassword("password");
        u.setOrganization("Czech Technical University in Prague");
        u.setUserEnabled(true);

        dao.saveOrUpdate(u);

        Event event = new Event();
        event.setName("CTU DAY 1");
        event.setDate(DateTime.now(DateTimeZone.UTC));
        event.setInformation("CTU DAY is an annual conference for all people.");
        u.addEvent(event);

        Event event2 = new Event();
        event2.setName("SERVICE TEST EVENT");
        event2.setDate(DateTime.now(DateTimeZone.UTC));
        event2.setInformation("SERVICE TEST EVENT SERVICE TEST EVENT SERVICE TEST EVENT SERVICE TEST EVENT");
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

        u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

        routeId = u.getEvents().get(0).getRoutes().get(0).getId();

        userService.createGeneratedUser(guide1);
        userService.createGeneratedUser(guide2);
        userService.createGeneratedUser(guide3);
        userService.createGeneratedUser(guide4);

        userService.createGeneratedUser(stationManager1);
        userService.createGeneratedUser(stationManager2);
        userService.createGeneratedUser(stationManager3);
        userService.createGeneratedUser(stationManager4);
    }

    @Test
    public void testGetRoute() throws Exception {

        RouteDto routeDto = routeService.getRoute(routeId);
        System.out.println(routeDto);

    }

    @Test
    public void testRemoveRoute() throws Exception {

        RouteDto r = routeService.getRoute(routeId);
        Assert.assertNotNull(r);

        routeService.removeRoute(routeId);

        try {
            r = routeService.getRoute(routeId);
            Assert.fail("Should have thrown Exception!");
        } catch (DataAccessException e){
            Assert.assertEquals(DataAccessException.ErrorCode.INSTANCE_NOT_FOUND, e.getErrorCode());
        }

        User u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

    }

    @Test
    public void testUpdateRoute() throws Exception {

        long before = System.nanoTime();

        RouteDto r = routeService.getRoute(routeId);
        Assert.assertNotNull(r);

        String newName = "ROUTENAME"+System.nanoTime();
        String newHexColor = "HEXCOLOR"+System.nanoTime();
        String newInfo = "INFO"+System.nanoTime();
        DateTime newTime = DateTime.now().plusYears(100);

        Assert.assertNotEquals(newName, r.getName());
        Assert.assertNotEquals(newHexColor, r.getHexColor());
        Assert.assertNotEquals(newInfo, r.getInformation());
        Assert.assertNotEquals(newTime, r.getDate());

        r.setName(newName);
        r.setHexColor(newHexColor);
        r.setInformation(newInfo);
        r.setDate(newTime);

        routeService.updateRoute(r);
        r = routeService.getRoute(routeId);

        Assert.assertEquals(newName, r.getName());
        Assert.assertEquals(newHexColor, r.getHexColor());
        Assert.assertEquals(newInfo, r.getInformation());
        Assert.assertEquals(newTime, r.getDate());

        double time = (System.nanoTime() - before) / 1000000000.0;

        System.out.println("Total time: " + time + " seconds.");

    }

    @Test
    public void testCreateRoute() throws Exception {

        UserDto u = userService.getUser(username);
        System.out.println(u.getEvents().get(1));
        Long eventId = u.getEvents().get(1).getId();

        String name = "CREATED_ROUTE";
        String hexColor = "006080";
        String info = "This is route information.";

        List<DateTime> times = new ArrayList<DateTime>();
        times.add(DateTime.now().plusHours(1));
        times.add(DateTime.now().plusHours(3));

        HashMap<Long, String> guides = new HashMap<Long, String>();

        List<StationDto> stations = new ArrayList<StationDto>();
        for (int i = 1; i <= 4; i++) {

            StationDto s = new StationDto(true);
            s.setName("Station " + i);
            s.setSequencePosition(i);
            s.setLocation("K-00" + i);
            s.setRelocationTime(i*10);
            s.setTimeLimit(i*100);

            guides.put(s.getCreationId(), "guide"+ i +"@gmail.com");
            stations.add(s);

        }

        List<String> stationManagerEmails = new ArrayList<String>();
        stationManagerEmails.add(stationManager1);
        stationManagerEmails.add(stationManager2);
        stationManagerEmails.add(stationManager3);
        stationManagerEmails.add(stationManager4);

        List<Long> routeIds = routeService.saveRoute(eventId, name, hexColor, info, times, stations, guides, stationManagerEmails);
        Assert.assertEquals(times.size(), routeIds.size());

        User user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        user = dao.getByPropertyUnique("email", guide1, User.class);
        user.print();

        user = dao.getByPropertyUnique("email", stationManager1, User.class);
        user.print();


    }
}
