package cz.kubaspatny.opendayapp.test.secured;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IStationService;
import cz.kubaspatny.opendayapp.service.IUserService;
import cz.kubaspatny.opendayapp.test.AbstractSecuredTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 2/1/2015
 * Time: 15:34
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
public class SecuredStationServiceTest extends AbstractSecuredTest {

    @Autowired private GenericDao dao;
    @Autowired private IUserService userService;
    @Autowired private IEventService eventService;
    @Autowired private IRouteService routeService;
    @Autowired private IStationService stationService;

    private String username = "kuba.spatny@gmail.com";

    private String guide1 = "guide1@gmail.com";
    private String guide2 = "guide2@gmail.com";

    private String stationManager1 = "stationManager1@gmail.com";
    private String stationManager2 = "stationManager2@gmail.com";

    private Long stationId;
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

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ORGANIZER"));
        authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        setUser(username, authorities);

        EventDto eventDto = new EventDto();
        eventDto.setName("CTU DAY 1");
        eventDto.setDate(DateTime.now(DateTimeZone.UTC));
        eventDto.setInformation("CTU DAY is an annual conference for all people.");
        eventService.addEvent(eventDto);

        EventDto eventDto2 = new EventDto();
        eventDto2.setName("SERVICE TEST EVENT");
        eventDto2.setDate(DateTime.now(DateTimeZone.UTC));
        eventDto2.setInformation("SERVICE TEST EVENT SERVICE TEST EVENT SERVICE TEST EVENT SERVICE TEST EVENT");
        eventService.addEvent(eventDto2);

        userService.createGeneratedUser(guide1);
        userService.createGeneratedUser(guide2);

        userService.createGeneratedUser(stationManager1);
        userService.createGeneratedUser(stationManager2);

        Long eventId = u.getEvents().get(1).getId();

        String name = "CREATED_ROUTE";
        String hexColor = "006080";
        String info = "This is route information.";

        List<DateTime> times = new ArrayList<DateTime>();
        times.add(DateTime.now().plusHours(1));

        HashMap<Integer, String> guides = new HashMap<Integer, String>();

        List<StationDto> stations = new ArrayList<StationDto>();
        for (int i = 1; i <= 2; i++) {

            StationDto s = new StationDto(true);
            s.setName("Station " + i);
            s.setSequencePosition(i);
            s.setLocation("K-00" + i);
            s.setRelocationTime(i*10);
            s.setTimeLimit(i*100);

            guides.put(i, "guide"+ i +"@gmail.com");
            stations.add(s);

        }

        List<String> stationManagerEmails = new ArrayList<String>();
        stationManagerEmails.add(stationManager1);
        stationManagerEmails.add(stationManager2);

        List<Long> routeIds = routeService.saveRoute(eventId, name, hexColor, info, times, stations, guides, stationManagerEmails);
        Assert.assertEquals(times.size(), routeIds.size());
        Assert.assertEquals(1, routeIds.size());

        routeId = routeIds.get(0);
        stationId = routeService.getRoute(routeId).getStations().get(0).getId();

    }

    @Test
    public void testGetStation() throws Exception {

        try {
            setUser(username);
            stationService.getStation(stationId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide1");
            stationService.getStation(stationId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("unknown");
            stationService.getStation(stationId);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

    }

    @Test
    public void testAddStation() throws Exception {

        StationDto s = new StationDto();
        s.setName("Added station");
        s.setSequencePosition(10);
        s.setLocation("K-00-NEW");
        s.setRelocationTime(100);
        s.setTimeLimit(100);

        Long newStationId = null;

        try {
            setUser("guide1");
            newStationId = stationService.addStation(routeId, s);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            newStationId = stationService.addStation(routeId, s);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(username);
            newStationId = stationService.addStation(routeId, s);
            Assert.assertNotNull(newStationId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser(username);
            stationService.getStation(newStationId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide1");
            stationService.getStation(newStationId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("unknown");
            stationService.getStation(newStationId);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

    }

    @Test
    public void testUdpdateStation() throws Exception {


        StationDto s = stationService.getStation(stationId);
        s.setName("Updated station");
        s.setSequencePosition(10);
        s.setLocation("K-00-NEW");
        s.setRelocationTime(100);
        s.setTimeLimit(100);

        try {
            setUser("guide1");
            stationService.updateStation(s);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            stationService.updateStation(s);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(username);
            stationService.updateStation(s);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

    }

    @Test
    public void testRemoveStation() throws Exception {

        try {
            setUser("guide1");
            stationService.removeStation(stationId);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            stationService.removeStation(stationId);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(username);
            Assert.assertNotNull(stationService.getStation(stationId));
            stationService.removeStation(stationId);

            stationService.getStation(stationId);
            Assert.fail("Should have thrown an Exception!");
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown AccessDeniedException exception!");
        } catch (DataAccessException e){
            Assert.assertEquals(DataAccessException.ErrorCode.INSTANCE_NOT_FOUND, e.getErrorCode());
        }

    }

}