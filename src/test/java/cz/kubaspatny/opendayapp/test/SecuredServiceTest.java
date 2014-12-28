package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IUserService;
import cz.kubaspatny.opendayapp.service.TestService;
import org.hibernate.annotations.SourceType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 20/12/2014
 * Time: 22:28
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
public class SecuredServiceTest extends AbstractSecuredTest {

    @Autowired private TestService testService;
    @Autowired private IUserService userService;
    @Autowired private IEventService eventService;
    @Autowired private IRouteService routeService;

    private Long eventID;
    private Long routeID;

    @Before
    public void setUp() throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "user", null);

        SecurityContextHolder.getContext().setAuthentication(token);

        if ((SecurityContextHolder.getContext() == null)) {
            System.out.println("Not authenticated 1.");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Not authenticated 2.");
            return;
        }

        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {

            System.out.println("Not authenticated 3.");
            return;
        }

        UserDto u = new UserDto();
        u.setUsername("user");
        u.setPassword("user");
        u.setEmail("email@email.com");

        Long userID = userService.createUser(u);

        EventDto e = new EventDto();
        e.setName("name");
        e.setDate(DateTime.now());
        e.setInformation("info");

        //eventID = eventService.addEvent(userID, e);
        eventID = testService.addSecuredEvent(userID, e);

        // ------ add route ------

        String name = "CREATED_ROUTE";
        String hexColor = "006080";
        String info = "This is route information.";

        List<DateTime> times = new ArrayList<DateTime>();
        times.add(DateTime.now().plusHours(1));

        routeID = testService.addSecuredRoute(eventID, name, hexColor, info, times, null, null, null);

    }

    @Test
    public void testSecuredMethodCall() throws Exception {

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user"));
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            testService.getSomeText();
            Assert.fail("Should have thrown exception!");
        } catch (AccessDeniedException e) {
            Assert.assertTrue(true);
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        String text = testService.getSomeText();
        Assert.assertNotNull(text);

    }

    @Test
    public void testACLSecuredMethod() throws Exception {

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", null));
            Event e = testService.getSecuredEvent(eventID);
        } catch (AccessDeniedException e) {
            Assert.fail("Shouldn't have thrown Exception!");
        }

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", null));
            Event e = testService.getSecuredEvent(eventID);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testACLSecuredMethodDto() throws Exception {

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", null));
            EventDto e = testService.getSecuredEventDto(eventID);
            Assert.assertNotNull(e);
        } catch (AccessDeniedException e) {
            Assert.fail("Should NOT have thrown Exception!");
        }

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", null));
            EventDto e = testService.getSecuredEventDto(eventID);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testACLServiceMethod() throws Exception {

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", null));
            EventDto e = eventService.getEvent(eventID);
            Assert.assertNotNull(e);
        } catch (AccessDeniedException e) {
            Assert.fail("Should NOT have thrown Exception!");
        }

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", null));
            EventDto e = eventService.getEvent(eventID);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testACLSecuredMethodSubObjectDto() throws Exception {

        try {

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", null));
            RouteDto e = testService.getSecuredRouteDto(routeID);
            Assert.assertNotNull(e);
            System.out.println(e);

        } catch (AccessDeniedException e) {
            Assert.fail("Should NOT have thrown Exception!");
        }

        try {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", null));
            RouteDto e = testService.getSecuredRouteDto(routeID);
            Assert.fail("Should have thrown Exception!");
        } catch (AccessDeniedException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testEmpty() throws Exception {


    }
}
