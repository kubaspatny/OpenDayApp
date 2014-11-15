package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.*;
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
    public void testPrintUser() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);
        u.print();

    }

    @Test
    public void testAddStationManager() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);
        u.print();
        System.out.println("--------------------------------------------");

        User uNew = new User();
        String usernameNew = "stationmanager@gmail.com";
        uNew.setUsername(usernameNew);
        uNew.setPassword("djnsdgjnaso;a");
        uNew.setOrganization("CTU");

        u.getEvents().get(0).getRoutes().get(0).addStationManager(uNew);
        dao.saveOrUpdate(u);

        u = dao.getByPropertyUnique("username",username, User.class);
        u.print();

        System.out.println("--------------------------------------------");

        uNew = dao.getByPropertyUnique("username",usernameNew, User.class);
        uNew.print();


    }

    @Test
    public void testAddEvent() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);
        u.print();
        System.out.println("-----------------------------------");

        Event e = new Event();
        e.setName("NEWLY ADDED EVENT");
        e.setInformation("INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION INFORMATION");
        e.setDate(DateTime.now(DateTimeZone.UTC).plusYears(1));
        e = dao.saveOrUpdate(e);

        u.addEvent(e);
        dao.saveOrUpdate(u);

        u = dao.getByPropertyUnique("username",username, User.class);
        u.print();

    }

    @Test
    public void testEvents() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);
        System.out.println(u);

        List<Event> events = u.getEvents();
        Assert.assertNotNull(events);

        Collections.sort(events, new EventDateComparator());

        List<Long> route_ids = new ArrayList<Long>();

        for(Event e : events){
            List<Route> routes = e.getRoutes();
            if(routes == null) continue;
            for(Route r : routes){
                route_ids.add(r.getId());
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
        dao.remove(events.get(0));
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

    //@Test
    public void testCascase() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        List<Event> events = u.getEvents();
        List<Long> ids_event = new ArrayList<Long>();
        List<Long> ids_route = new ArrayList<Long>();
        List<Long> ids_station = new ArrayList<Long>();
        Collections.sort(events, new EventDateComparator());
        for(Event e : events){
            ids_event.add(e.getId());

            if(e.getRoutes() != null){
                for(Route r : e.getRoutes()){
                    ids_route.add(r.getId());
                    if(r.getStations() != null){
                        for(Station s : r.getStations()){
                            ids_station.add(s.getId());

                        }

                    }
                }
            }

        }

        dao.remove(u);

        Event e = dao.getById(ids_event.get(0), Event.class);
        Route r = dao.getById(ids_route.get(0), Route.class);
        Station s = dao.getById(ids_station.get(0), Station.class);

        /**
         * Assert that the user's events have been deleted as well.
         */
        Assert.assertNull(e);
        Assert.assertNull(r);
        Assert.assertNull(s);



    }

    /**
     * Test if we delete event that the user doesn't see it anymore.
     * @throws Exception
     */
    @Test
    public void testDeleteEvent() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);

        Long id = null;

        for(Event e : u.getEvents()){
            if(id == null){
                id = e.getId();
                break;
            }
        }

        u.print();

        dao.removeById(id, Event.class);
        Assert.assertNull(dao.getById(id, Event.class));

        System.out.println("---- PRE RETRIEVE ----");
        u.print();

        System.out.println("---- POST RETRIEVE ----");
        u = dao.getByPropertyUnique("username", username, User.class);
        u.print();


    }

    @Test
    public void testDeleteRouteEventPart() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        Long id = u.getEvents().get(0).getRoutes().get(0).getId();

        u.print();

        dao.removeById(id, Route.class);
        Assert.assertNull(dao.getById(id, Route.class));

        System.out.println("---- PRE RETRIEVE ----");
        u.print();

        System.out.println("---- POST RETRIEVE ----");
        u = dao.getByPropertyUnique("username", username, User.class);
        u.print();

    }

    @Test
    public void testDeleteRouteStationManagerPart() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        User uNew = new User();
        String usernameNew = "stationmanager@gmail.com";
        uNew.setUsername(usernameNew);
        uNew.setPassword("djnsdgjnaso;a");
        uNew.setOrganization("CTU");

        u.getEvents().get(0).getRoutes().get(0).addStationManager(uNew);
        dao.saveOrUpdate(u);

        u = dao.getByPropertyUnique("username",username, User.class);
        u.print();

        System.out.println("--------------------------------------------");

        uNew = dao.getByPropertyUnique("username",usernameNew, User.class);
        uNew.print();

        // remove route

        Long id = u.getEvents().get(0).getRoutes().get(0).getId();
        dao.removeById(id, Route.class);
        Assert.assertNull(dao.getById(id, Route.class));

        u.print();
        System.out.println("--------------------------------------------");
        uNew.print();
        uNew = dao.getByPropertyUnique("username", usernameNew, User.class);
        System.out.println("-- POST RETRIEVE --");
        uNew.print();
    }

    @Test
    public void testRemoveStationRoutePart() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);
        Route r = u.getEvents().get(0).getRoutes().get(0);
        Station s = r.getStations().get(0);

        u.print();

        int sizeBefore = r.getStations().size();
        dao.remove(s);

        Assert.assertNull(dao.getById(s.getId(), Station.class));
        Assert.assertEquals(sizeBefore - 1, r.getStations().size());
        u.print();

    }

    @Test
    public void testDeleteEventCascade() throws Exception {

        User u = dao.getByPropertyUnique("username", username, User.class);
        u.print();
        Long id = u.getEvents().get(0).getId();
        Long idRoute = u.getEvents().get(0).getRoutes().get(0).getId();
        dao.remove(u.getEvents().get(0));
        u.print();

        Assert.assertNull(dao.getById(id, Event.class));
        Assert.assertNull(dao.getById(idRoute, Route.class));

    }
}
