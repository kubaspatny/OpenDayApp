package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.*;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.GroupDto;
import cz.kubaspatny.opendayapp.dto.LocationUpdateDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IGroupService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IStationService;
import cz.kubaspatny.opendayapp.service.IUserService;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
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
 * Date: 25/11/2014
 * Time: 22:42
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
public class GroupServiceTest extends AbstractTest {

    @Autowired
    private GenericDao dao;
    @Autowired private IRouteService routeService;
    @Autowired private IUserService userService;
    @Autowired private IGroupService groupService;

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

        routeId = u.getEvents().get(0).getRoutes().get(0).getId();

        userService.createGeneratedUser(guide1);
        userService.createGeneratedUser(guide2);
        userService.createGeneratedUser(guide3);
        userService.createGeneratedUser(guide4);

        userService.createGeneratedUser(stationManager1);
        userService.createGeneratedUser(stationManager2);
        userService.createGeneratedUser(stationManager3);
        userService.createGeneratedUser(stationManager4);

        createRoute();

    }

    public void createRoute() throws Exception {

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

            if(i != 4) guides.put(s.getCreationId(), "guide"+ i +"@gmail.com");

            stations.add(s);

        }

        List<String> stationManagerEmails = new ArrayList<String>();
        stationManagerEmails.add(stationManager1);
        stationManagerEmails.add(stationManager2);
        stationManagerEmails.add(stationManager3);
        stationManagerEmails.add(stationManager4);

        List<Long> routeIds = routeService.saveRoute(eventId, name, hexColor, info, times, stations, guides, stationManagerEmails);
        Assert.assertEquals(times.size(), routeIds.size());

    }

    public void printUsers() throws Exception {
        System.out.println("*******************************************************************************************");
        User user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        user = dao.getByPropertyUnique("email", guide1, User.class);
        user.print();

        user = dao.getByPropertyUnique("email", stationManager1, User.class);
        user.print();
        System.out.println("*******************************************************************************************");
    }

    @Test
    public void testGetGroup() throws Exception {

        User user = dao.getByPropertyUnique("email", guide1, User.class);
        user.print();
        Long groupID = user.getGroups().get(0).getId();

        GroupDto g = groupService.getGroup(groupID, false, false);
        System.out.println(g);

        Assert.assertNotNull(g);

        try {
            groupService.getGroup(new Long(123), false, false);
            Assert.fail("Should have thrown Exception!");
        } catch (DataAccessException e){
            Assert.assertEquals(DataAccessException.ErrorCode.INSTANCE_NOT_FOUND, e.getErrorCode());
        }

    }

    @Test
    public void testAddGroup() throws Exception {

        User user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        User user2 = dao.getByPropertyUnique("email", guide4, User.class);
        user2.print();

        Long routeID = user.getEvents().get(0).getRoutes().get(0).getId();
        Long stationID = user.getEvents().get(0).getRoutes().get(0).getStations().get(3).getId();
        Long guideID = user2.getId();

        Long groupID = groupService.addGroup(routeID, stationID, guideID);

        System.out.println("*******************************************************************************************");

        user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        user2 = dao.getByPropertyUnique("email", guide4, User.class);
        user2.print();

    }

    @Test
    public void testRemoveGroup() throws Exception {

        User user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        User user2 = dao.getByPropertyUnique("email", guide1, User.class);
        user2.print();

        System.out.println("*******************************************************************************************");

        Group g = user.getEvents().get(1).getRoutes().get(0).getGroups().get(0);
        Long groupID = g.getId();
        groupService.removeGroup(groupID);

        g = dao.getById(groupID, Group.class);
        Assert.assertNull(g);

        user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        user2 = dao.getByPropertyUnique("email", guide1, User.class);
        user2.print();

        // ADD TEST TO NUMBER OF GROUPS IN ROUTE AND GUIDE

    }

    @Test
    public void testAddLocationUpdate() throws Exception {

        printUsers();

        User user = dao.getByPropertyUnique("username", username, User.class);
        Route r = user.getEvents().get(1).getRoutes().get(0);

        Station s1 = r.getStations().get(0);
        Station s2 = r.getStations().get(1);

        Group g = r.getGroups().get(0);

        LocationUpdateDto update = new LocationUpdateDto();
        update.setType(LocationUpdate.Type.CHECKIN);
        update.setTimestamp(DateTime.now());
        update.setGroup(GroupDto.map(g, new GroupDto(), DtoMapperUtil.getGroupIgnoredProperties()));
        update.setStation(StationDto.map(s1, new StationDto(), DtoMapperUtil.getStationIgnoredProperties()));
        groupService.addLocationUpdate(update);

        update = new LocationUpdateDto();
        update.setType(LocationUpdate.Type.CHECKOUT);
        update.setTimestamp(DateTime.now());
        update.setGroup(GroupDto.map(g, new GroupDto(), DtoMapperUtil.getGroupIgnoredProperties()));
        update.setStation(StationDto.map(s1, new StationDto(), DtoMapperUtil.getStationIgnoredProperties()));
        groupService.addLocationUpdate(update);

        update = new LocationUpdateDto();
        update.setType(LocationUpdate.Type.CHECKIN);
        update.setTimestamp(DateTime.now());
        update.setGroup(GroupDto.map(g, new GroupDto(), DtoMapperUtil.getGroupIgnoredProperties()));
        update.setStation(StationDto.map(s2, new StationDto(), DtoMapperUtil.getStationIgnoredProperties()));
        groupService.addLocationUpdate(update);

        user = dao.getByPropertyUnique("username", username, User.class);
        user.print();

        user = dao.getByPropertyUnique("email", guide1, User.class);
        user.print();

    }

    @Test
    public void testSetStartingPosition() throws Exception {

        printUsers();

        User user = dao.getByPropertyUnique("username", username, User.class);
        Route r = user.getEvents().get(1).getRoutes().get(0);

        Station s1 = r.getStations().get(0);
        Station s2 = r.getStations().get(1);


        Group g = r.getGroups().get(0);

        GroupDto group = groupService.getGroup(g.getId(), false, false);
        Assert.assertNotEquals(s2.getId(), group.getStartingPosition().getId());

        groupService.setGroupStartingPosition(g.getId(), s2.getId());

        group = groupService.getGroup(g.getId(), false, false);
        Assert.assertEquals(s2.getId(), group.getStartingPosition().getId());

        Assert.assertEquals(s2.getStartingGroup().getId(), g.getId());

        System.out.println("*****************************************************");
        user = dao.getByPropertyUnique("username", username, User.class);
        user.print();
        user = dao.getByPropertyUnique("email", guide1, User.class);
        user.print();

    }

    @Test
    public void testGroupActive() throws Exception {

        printUsers();

        User user = dao.getByPropertyUnique("username", username, User.class);
        Route r = user.getEvents().get(1).getRoutes().get(0);

        Station s1 = r.getStations().get(0);
        Station s2 = r.getStations().get(1);


        Group g = r.getGroups().get(0);
        groupService.setLastUpdated(g.getId(), DateTime.now());
        GroupDto group = groupService.getGroup(g.getId(), false, false);
        Assert.assertTrue(group.isActive());
        System.out.println(group);

        groupService.setLastUpdated(g.getId(), DateTime.now().minusMinutes(20));
        group = groupService.getGroup(g.getId(), false, false);
        Assert.assertFalse(group.isActive());
        System.out.println(group);
    }
}
