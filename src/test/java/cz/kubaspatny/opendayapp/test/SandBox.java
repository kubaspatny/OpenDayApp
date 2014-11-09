package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.provider.HashProvider;
import cz.kubaspatny.opendayapp.utils.EventDateComparator;
import org.hibernate.annotations.SourceType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 16:20
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
public class SandBox extends AbstractTest {

    @Autowired private GenericDao dao;

    private String username = "kuba.spatny@gmail.com";

    @Before
    public void setUp() throws Exception {
        User u = new User();
        u.setFirstName("Kuba");
        u.setLastName("Spatny");
        u.setUsername(username);
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
        event2.setName("CTU DAY 2");
        event2.setDate(DateTime.now(DateTimeZone.UTC));
        event2.setInformation("ANOTHER INFORMATION!");
        event2.setOrganizer(u);
        u.addEvent(event2);

        dao.saveOrUpdate(u);

        // event is saved

        Route r1 = new Route();
        r1.setName("Blue route");
        r1.setDate(DateTime.now(DateTimeZone.UTC));
        r1.setHexColor("006040");
        r1.setInformation("This route is for STM and OI.");

        Route r2 = new Route();
        r2.setName("Red route");
        r2.setDate(DateTime.now(DateTimeZone.UTC));
        r2.setHexColor("80FF20");
        r2.setInformation("This route is for Software Engineers only.");

        u.getEvents().get(0).addRoute(r1);
        u.getEvents().get(0).addRoute(r2);

        dao.saveOrUpdate(u);
    }

    @Test
    public void testEvents() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);
        System.out.println(u);

        List<Event> events = u.getEvents();
        Assert.assertNotNull(events);

        Collections.sort(events, new EventDateComparator());
        for(Event e : events){
            System.out.println(e);
            List<Route> routes = e.getRoutes();

            if(routes == null) continue;

            for(Route r : routes){
                System.out.println("\t" + r.toString());
            }

        }


    }

    @Test
    public void testRemoveEvents() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        List<Event> events = u.getEvents();

        Collections.sort(events, new EventDateComparator());
        for(Event e : events){
            System.out.println(e);
        }

        long id = events.get(0).getId();
        u.removeEvent(events.get(0));
        dao.saveOrUpdate(u);

        System.out.println("---------------------------------------------------");
        u = dao.getByPropertyUnique("username",username, User.class);
        events = u.getEvents();

        Collections.sort(events, new EventDateComparator());
        for(Event e : events){
            System.out.println(e);
        }

        Event e = dao.getById(id, Event.class);
        Assert.assertNull(e);

        /**
         * It is necessary to remove the event object from database as well!
         */

    }

    @Test
    public void testCascase() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        List<Event> events = u.getEvents();
        List<Long> ids = new ArrayList<Long>();
        Collections.sort(events, new EventDateComparator());
        for(Event e : events){
            ids.add(e.getId());
        }

        dao.remove(u);

        Event e = dao.getById(ids.get(0), Event.class);
        Assert.assertNull(e);



    }
}
