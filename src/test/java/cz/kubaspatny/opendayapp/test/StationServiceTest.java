package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IStationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 25/11/2014
 * Time: 15:01
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
public class StationServiceTest extends AbstractTest {

    @Autowired private GenericDao dao;
    @Autowired private IStationService stationService;
    @Autowired private IEventService eventService;
    @Autowired private IRouteService routeService;

    private String username = "username@gmail.com";
    private Long stationID;
    private Long route2ID;

    private String stationName = "STATIONNAME";
    private String stationInformation = "STATIONINFORMATION";
    private String stationLocation = "STATIONLOCATION";
    private int timeLimit = 10000;
    private int relocationTime = 20;
    private int sequencePosition = 1;

    @Before
    public void setUp() throws Exception {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "password", null);
        SecurityContextHolder.getContext().setAuthentication(token);

        User u = new User();
        u.setFirstName("Kuba");
        u.setLastName("Spatny");
        u.setUsername(username);
        u.setEmail(username);
        u.setPassword("password");
        u.setOrganization("Czech Technical University in Prague");
        u.setUserEnabled(true);

        dao.saveOrUpdate(u);

        EventDto eventDto = new EventDto();
        eventDto.setName("CTU DAY 1");
        eventDto.setDate(DateTime.now(DateTimeZone.UTC));
        eventDto.setInformation("CTU DAY is an annual conference for all people.");
        Event event = dao.getById(eventService.addEvent(eventDto), Event.class);

        EventDto eventDto2 = new EventDto();
        eventDto2.setName("SERVICE TEST EVENT");
        eventDto2.setDate(DateTime.now(DateTimeZone.UTC));
        eventDto2.setInformation("SERVICE TEST EVENT SERVICE TEST EVENT SERVICE TEST EVENT SERVICE TEST EVENT");
        eventService.addEvent(eventDto2);


        List<DateTime> dateTimes = new ArrayList<DateTime>();
        dateTimes.add(DateTime.now(DateTimeZone.UTC));

        StationDto s = new StationDto();
        s.setName(stationName);
        s.setInformation(stationInformation);
        s.setLocation(stationLocation);
        s.setSequencePosition(sequencePosition);
        s.setRelocationTime(relocationTime);
        s.setTimeLimit(timeLimit);

        List<StationDto> stationDtos = new ArrayList<StationDto>();
        stationDtos.add(s);

        routeService.saveRoute(event.getId(), "Blue route", "006040", "This route is for STM and OI.", dateTimes, stationDtos, null, null);

        // -------------------------------------------------

        routeService.saveRoute(event.getId(), "Red route", "80FF20", "This route is for Software Engineers only.", dateTimes, null, null, null);

        dao.saveOrUpdate(u);

        stationID = u.getEvents().get(0).getRoutes().get(0).getStations().get(0).getId();
        route2ID = u.getEvents().get(0).getRoutes().get(1).getId();

    }

    @Test
    public void testGetStation() throws Exception {

        StationDto stationDto = stationService.getStation(stationID);
        System.out.println(stationDto);

        Assert.assertNotNull(stationDto);
        Assert.assertEquals(stationName, stationDto.getName());
        Assert.assertEquals(stationLocation, stationDto.getLocation());
        Assert.assertEquals(stationInformation, stationDto.getInformation());
        Assert.assertEquals(stationID, stationDto.getId());
        Assert.assertEquals(sequencePosition, stationDto.getSequencePosition());
        Assert.assertEquals(relocationTime, stationDto.getRelocationTime());
        Assert.assertEquals(timeLimit, stationDto.getTimeLimit());

        // ----------------------------------------------------------------

        User u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

    }

    @Test
    public void testAddStation() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        u.print();
        System.out.println("-------------------------------------------------------");

        String localName = "Station " + System.nanoTime();

        StationDto s = new StationDto();
        s.setName(localName);
        s.setLocation(stationLocation);
        s.setInformation(stationInformation);
        s.setTimeLimit(timeLimit);
        s.setRelocationTime(relocationTime);
        s.setSequencePosition(sequencePosition);

        Long sID = stationService.addStation(route2ID, s);

        Assert.assertNotNull(sID);

        Station station = dao.getById(sID, Station.class);
        Assert.assertEquals(localName, station.getName());
        Assert.assertEquals(stationInformation, station.getInformation());
        Assert.assertEquals(stationLocation, station.getLocation());
        Assert.assertEquals(relocationTime, station.getRelocationTime());
        Assert.assertEquals(sequencePosition, station.getSequencePosition());
        Assert.assertEquals(timeLimit, station.getTimeLimit());

        u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

    }

    @Test
    public void testUpdateStation() throws Exception {

        StationDto s = stationService.getStation(stationID);

        String stationName2 = "EDITED";
        String stationInformation2 = "EDITED EDITED EDITED";
        String stationLocation2 = "EDITED LOCATION";
        boolean closed = true;
        int timeLimit2 = 999999;
        int relocationTime2 = 99;
        int sequencePosition2 = 9;

        Assert.assertNotEquals(stationName2, s.getName());
        Assert.assertNotEquals(stationInformation2, s.getInformation());
        Assert.assertNotEquals(closed, s.isClosed());
        Assert.assertNotEquals(stationLocation2, s.getLocation());
        Assert.assertNotEquals(relocationTime2, s.getRelocationTime());
        Assert.assertNotEquals(sequencePosition2, s.getSequencePosition());
        Assert.assertNotEquals(timeLimit2, s.getTimeLimit());

        s.setName(stationName2);
        s.setInformation(stationInformation2);
        s.setClosed(closed);
        s.setLocation(stationLocation2);
        s.setTimeLimit(timeLimit2);
        s.setRelocationTime(relocationTime2);
        s.setSequencePosition(sequencePosition2);

        stationService.updateStation(s);
        s = stationService.getStation(stationID);
        System.out.println(s);

        Assert.assertEquals(stationName2, s.getName());
        Assert.assertEquals(stationInformation2, s.getInformation());
        Assert.assertEquals(closed, s.isClosed());
        Assert.assertEquals(stationLocation2, s.getLocation());
        Assert.assertEquals(relocationTime2, s.getRelocationTime());
        Assert.assertEquals(sequencePosition2, s.getSequencePosition());
        Assert.assertEquals(timeLimit2, s.getTimeLimit());

    }

    @Test
    public void testRemoveStation() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

        System.out.println("-----------------------------------------------------------------");

        StationDto s = stationService.getStation(stationID);
        Assert.assertNotNull(s);

        stationService.removeStation(stationID);

        try {
            s = stationService.getStation(stationID);
            Assert.fail("Should have thrown an Exception!");
        } catch (DataAccessException e){
            Assert.assertEquals(DataAccessException.ErrorCode.INSTANCE_NOT_FOUND, e.getErrorCode());
        }

        u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

    }
}























