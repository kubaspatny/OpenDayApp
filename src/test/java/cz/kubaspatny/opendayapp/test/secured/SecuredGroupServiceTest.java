package cz.kubaspatny.opendayapp.test.secured;

import cz.kubaspatny.opendayapp.bo.LocationUpdate;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.*;
import cz.kubaspatny.opendayapp.service.*;
import cz.kubaspatny.opendayapp.test.AbstractSecuredTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 2/1/2015
 * Time: 17:16
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
public class SecuredGroupServiceTest extends AbstractSecuredTest {

    @Autowired private GenericDao dao;
    @Autowired private IUserService userService;
    @Autowired private IEventService eventService;
    @Autowired private IRouteService routeService;
    @Autowired private IStationService stationService;
    @Autowired private IGroupService groupService;

    private String username = "kuba.spatny@gmail.com";

    private String guide1 = "guide1@gmail.com";
    private String guide2 = "guide2@gmail.com";
    private String guide3 = "guide3@gmail.com";

    private String stationManager1 = "stationManager1@gmail.com";
    private String stationManager2 = "stationManager2@gmail.com";

    private Long stationId;
    private Long routeId;
    private Long groupId;

    @Before
    public void setUp() throws Exception {

        UserDto u = new UserDto();
        u.setFirstName("Kuba");
        u.setLastName("Spatny");
        u.setUsername(username);
        u.setEmail(username);
        u.setPassword("password");
        u.setOrganization("Czech Technical University in Prague");
        Long userId = userService.createUser(u);

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
        userService.createGeneratedUser(guide3);

        userService.createGeneratedUser(stationManager1);
        userService.createGeneratedUser(stationManager2);

        Long eventId = dao.getById(userId, User.class).getEvents().get(1).getId();

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
        groupId = routeService.getRoute(routeId).getGroups().get(0).getId();

    }

    @Test
    public void testGetGroup() throws Exception {

        try {
            setUser(username);
            groupService.getGroup(groupId, true, false);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide1");
            groupService.getGroup(groupId, true, false);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("unknown");
            groupService.getGroup(groupId, true, false);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

    }

    @Test
    public void testAddGroup() throws Exception {

        RouteDto r = routeService.getRoute(routeId);

        setUser("guide3");
        UserDto u = userService.getUser("guide3");

        try {
            setUser("guide3");
            routeService.getRoute(r.getId());
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("guide1");
            groupService.addGroup(r.getId(), 1, u.getId());
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("guide1");
            groupService.addGroup(r, 1, u);
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            groupService.addGroup(r.getId(), 1, u.getId());
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            groupService.addGroup(r, 1, u);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        Long newGroupId = null;
        Long newGroupId2 = null;

        try {
            setUser(username);
            newGroupId = groupService.addGroup(r.getId(), 1, u.getId());
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser(username);
            newGroupId2 = groupService.addGroup(r, 1, u);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        //

        try {
            setUser("guide3");
            routeService.getRoute(r.getId());
            eventService.getEvent(r.getEvent().getId());
            groupService.getGroup(newGroupId, false, false);
            groupService.getGroup(newGroupId2, false, false);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser(username);
            groupService.getGroup(newGroupId, false, false);
            groupService.getGroup(newGroupId2, false, false);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

    }

    @Test
    public void testChangeStartingPosition() throws Exception {

        try {
            setUser(username);
            groupService.setGroupStartingPosition(groupId, 2);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide1");
            groupService.setGroupStartingPosition(groupId, 2);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide2");
            groupService.setGroupStartingPosition(groupId, 2);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            groupService.setGroupStartingPosition(groupId, 2);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

    }

    @Test
    public void testRemoveGroup() throws Exception {

        try {
            setUser("guide1");
            groupService.removeGroup(groupId);
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("unknown");
            groupService.removeGroup(groupId);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(username);
            groupService.removeGroup(groupId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

    }

    @Test
    public void testAddLocationUpdate() throws Exception {

        LocationUpdateDto updateDto = new LocationUpdateDto();
        updateDto.setGroup(groupService.getGroup(groupId, false, false));
        updateDto.setStation(stationService.getStation(stationId));
        updateDto.setTimestamp(DateTime.now());
        updateDto.setType(LocationUpdate.Type.CHECKIN);

        try {
            setUser("unknown");
            groupService.addLocationUpdate(updateDto);
            Assert.fail("Should have thrown an exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser(username);
            groupService.addLocationUpdate(updateDto);
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("guide1");
            groupService.addLocationUpdate(updateDto);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

    }

    @Test
    public void testAddGroupPermissions() throws Exception {

        RouteDto r = routeService.getRoute(routeId);

        setUser("guide3");
        UserDto u = userService.getUser("guide3");
        Long newGroupId = null;
        Long newGroupId2 = null;

        try {
            setUser(username);
            newGroupId = groupService.addGroup(r.getId(), 1, u.getId());
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser(username);
            newGroupId2 = groupService.addGroup(r.getId(), 2, u.getId());
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide3");
            routeService.getRoute(r.getId());
            eventService.getEvent(r.getEvent().getId());
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser(username);
            groupService.removeGroup(newGroupId);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide3");
            routeService.getRoute(r.getId());
            eventService.getEvent(r.getEvent().getId());
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser(username);
            groupService.removeGroup(newGroupId2);
        } catch (AccessDeniedException e){
            Assert.fail("Should NOT have thrown exception!");
        }

        try {
            setUser("guide3");
            routeService.getRoute(r.getId());
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

        try {
            setUser("guide3");
            eventService.getEvent(r.getEvent().getId());
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e){
            // correct
        }

    }

}