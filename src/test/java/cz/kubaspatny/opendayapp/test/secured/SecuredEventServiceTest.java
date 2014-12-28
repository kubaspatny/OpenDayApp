package cz.kubaspatny.opendayapp.test.secured;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IUserService;
import cz.kubaspatny.opendayapp.test.AbstractSecuredTest;
import org.joda.time.DateTime;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 28/12/2014
 * Time: 15:31
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
public class SecuredEventServiceTest extends AbstractSecuredTest {

    @Autowired private IUserService userService;
    @Autowired private IEventService eventService;

    public String username = "userOrganizer";
    private String eventName = "eventname";
    private DateTime eventDate = DateTime.now();
    private String eventInfo = "eventinfo";

    private Set<String> emailList = new HashSet<String>();

    // set up -> add Event
    // can I: edit, read, remove, add emails?

    public Long eventId;


    @Before
    public void setUp() throws Exception {
        UserDto u = new UserDto();
        u.setUsername(username);
        u.setPassword("password");
        u.setEmail("email@email.com");

        Long userID = userService.createUser(u);

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ORGANIZER"));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(token);

        addEvent();

    }

    public void addEvent(){

        EventDto e = new EventDto();
        e.setName(eventName);
        e.setDate(eventDate);
        e.setInformation(eventInfo);
        e.setEmailList(emailList);

        try {
            eventId = eventService.addEvent(e);
            Assert.assertNotNull(eventId);
        } catch (DataAccessException ex){
            Assert.fail("Should NOT have thrown an exception!");
        }

    }

    @Test
    public void addEventNotAuthorized(){

        EventDto e = new EventDto();
        e.setName(eventName);
        e.setDate(eventDate);
        e.setInformation(eventInfo);
        e.setEmailList(emailList);

        try {
            // doesn't have authority ROLE_ORGANIZER
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);
            eventId = eventService.addEvent(e);
            Assert.fail("Should have thrown an Exception!");
        } catch (AccessDeniedException ex){
            // correct
        } catch (DataAccessException ex){
            Assert.fail("Should NOT have thrown this exception!");
        }

    }

    @Test
    public void testGetEvent() throws Exception {

        EventDto e = eventService.getEvent(eventId);
        Assert.assertNotNull(e);
        Assert.assertEquals(eventName, e.getName());
        Assert.assertEquals(eventDate, e.getDate());
        Assert.assertEquals(eventInfo, e.getInformation());

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("username2", "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);
            e = eventService.getEvent(eventId);
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException ex){
            return;
        }

        // TODO: test get Event after people are routemanagers/guides

    }

    @Test
    public void testUpdate() throws Exception {

        EventDto e = eventService.getEvent(eventId);

        String eventNameNew = eventName + " EDITED";
        DateTime eventDateNew = eventDate.plusYears(10);
        String eventInfoNew = eventInfo + " EDITED";

        e.setName(eventNameNew);
        e.setDate(eventDateNew);
        e.setInformation(eventInfoNew);


        try {
            eventService.updateEvent(e);
        } catch (AccessDeniedException ex){
            Assert.fail("Should NOT have thrown Exception!");
        } catch (DataAccessException ex){
            Assert.fail("Should NOT have thrown Exception!");
        }

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("username2", "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);
            eventService.updateEvent(e);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException ex){
            // corect
        } catch (DataAccessException ex){
            Assert.fail("Should NOT have thrown THIS Exception!");
        }

    }

    @Test
    public void testRemoveEvent() throws Exception {

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("username2", "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);
            eventService.removeEvent(eventId);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException ex){
            // corect
        } catch (DataAccessException ex){
            Assert.fail("Should NOT have thrown THIS Exception!");
        }

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);
            Assert.assertNotNull(eventService.getEvent(eventId));
            eventService.removeEvent(eventId);
        } catch (AccessDeniedException ex){
            Assert.fail("Should NOT have thrown Exception!");
        } catch (DataAccessException ex){
            ex.printStackTrace();
            Assert.fail("Should NOT have thrown THIS Exception!");
        }

    }

    @Test
    public void testAddEmail() throws Exception {

        try {
            String mail = "NEWMAIL@gmail1.com";
            eventService.addEmailToList(eventId, mail);
        } catch (AccessDeniedException ex){
            Assert.fail("Should NOT have thrown Exception!");
        } catch (DataAccessException ex){
            ex.printStackTrace();
            Assert.fail("Should NOT have thrown THIS Exception!");
        }

        try {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("username2", "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);

            String mail = "NEWMAIL@gmail1.com";
            eventService.addEmailToList(eventId, mail);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException ex){
            // correct
        } catch (DataAccessException ex){
            ex.printStackTrace();
            Assert.fail("Should NOT have thrown THIS Exception!");
        }

        try {
            String mail = "NEWMAIL@gmail1.com";
            eventService.removeEmailFromList(eventId, mail);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException ex){
            // correct
        } catch (DataAccessException ex){
            ex.printStackTrace();
            Assert.fail("Should NOT have thrown THIS Exception!");
        }

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, "password", null);
            SecurityContextHolder.getContext().setAuthentication(token);

            String mail = "NEWMAIL@gmail1.com";
            eventService.removeEmailFromList(eventId, mail);
        } catch (AccessDeniedException ex){
            Assert.fail("Should NOT have thrown Exception!");
        } catch (DataAccessException ex){
            ex.printStackTrace();
            Assert.fail("Should NOT have thrown THIS Exception!");
        }



    }

}

