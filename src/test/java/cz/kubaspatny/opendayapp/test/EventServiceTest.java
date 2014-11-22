package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 22/11/2014
 * Time: 23:00
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
public class EventServiceTest extends AbstractTest {

    @Autowired private IEventService eventService;
    @Autowired private IUserService userService;
    @Autowired private GenericDao dao;

    private Long userID;
    private Long userID2;
    private Long eventID;

    private String eventName = "eventname";
    private DateTime eventDate = DateTime.now();
    private String eventInfo = "eventinfo";

    @Before
    public void setUp() throws Exception {

        UserDto u = new UserDto();
        u.setUsername("username");
        u.setPassword("password");
        u.setEmail("email@email.com");

        userID = userService.createUser(u);

        // -----------------------------------

        UserDto u2 = new UserDto();
        u2.setUsername("username2");
        u2.setPassword("password2");
        u2.setEmail("email2@email.com");

        userID2 = userService.createUser(u2);

        Event e = new Event();
        e.setName(eventName);
        e.setDate(eventDate);
        e.setInformation(eventInfo);
        User user = dao.getById(userID2, User.class);
        user.addEvent(e);

        eventID = dao.saveOrUpdate(e).getId();
        dao.saveOrUpdate(user);

    }

    @Test
    public void testAddEvent() throws Exception {

        String name = "Super Event Name";
        DateTime time = DateTime.now().plusYears(1);
        String info = "Information about Super Event";

        EventDto e = new EventDto();
        e.setName(name);
        e.setDate(time);
        e.setInformation(info);

        Long id = eventService.addEvent(userID, e);

        Assert.assertNotNull(id);
        Event event = dao.getById(id, Event.class);
        Assert.assertNotNull(event);
        Assert.assertEquals(name, event.getName());
        Assert.assertEquals(time, event.getDate());
        Assert.assertEquals(info, event.getInformation());
        Assert.assertNotNull(event.getOrganizer());
        Assert.assertEquals(userID, event.getOrganizer().getId());

        UserDto u = userService.getUser(userID);
        Assert.assertEquals(1, u.getEvents().size());
        Assert.assertEquals(id, u.getEvents().get(0).getId());

    }

    @Test
    public void testAddEventIllegalArguments() throws Exception {

        try {
            Long id = eventService.addEvent(userID, null);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DataAccessException e){
            Assert.assertEquals(DataAccessException.ErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

        try {
            EventDto e = new EventDto();
            e.setName("");
            e.setDate(DateTime.now());
            e.setInformation("");

            Long id = eventService.addEvent(null, e);
            Assert.fail("Should have thrown Exception by now!");
        } catch (DataAccessException e){
            Assert.assertEquals(DataAccessException.ErrorCode.ILLEGAL_ARGUMENT, e.getErrorCode());
        }

    }

    @Test
    public void testGetEvent() throws Exception {

        EventDto e = eventService.getEvent(eventID);
        Assert.assertNotNull(e);
        Assert.assertEquals(eventName, e.getName());
        Assert.assertEquals(eventDate, e.getDate());
        Assert.assertEquals(eventInfo, e.getInformation());

        // -------------------------------------------------

        try {
            e = eventService.getEvent(System.nanoTime());
        } catch (DataAccessException ex){
            Assert.assertEquals(DataAccessException.ErrorCode.INSTANCE_NOT_FOUND, ex.getErrorCode());
        }

    }

    @Test
    public void testRemoveEvent() throws Exception {

        User u = dao.getById(userID2, User.class);
        u.print();

        eventService.removeEvent(eventID);

        u = dao.getById(userID2, User.class);
        u.print();

        Assert.assertNull(dao.getById(eventID, Event.class));
        Assert.assertEquals(0, u.getEvents().size());

    }

    @Test
    public void testUpdate() throws Exception {

        EventDto e = eventService.getEvent(eventID);
        Assert.assertEquals(eventName, e.getName());
        Assert.assertEquals(eventDate, e.getDate());
        Assert.assertEquals(eventInfo, e.getInformation());
        System.out.println(e);

        String eventNameNew = eventName + " EDITED";
        DateTime eventDateNew = eventDate.plusYears(10);
        String eventInfoNew = eventInfo + " EDITED";

        e.setName(eventNameNew);
        e.setDate(eventDateNew);
        e.setInformation(eventInfoNew);

        eventService.updateEvent(e);

        e = eventService.getEvent(eventID);
        Assert.assertEquals(eventNameNew, e.getName());
        Assert.assertEquals(eventDateNew, e.getDate());
        Assert.assertEquals(eventInfoNew, e.getInformation());

        System.out.println(e);

    }
}
