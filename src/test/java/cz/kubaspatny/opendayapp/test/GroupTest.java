package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.*;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 15/11/2014
 * Time: 21:06
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
public class GroupTest extends AbstractTest {

    @Autowired
    private GenericDao dao;

    private String username = "kuba.spatny@gmail.com";
    private String usernameGuide1 = "guide1@gmail.com";
    private String usernameGuide2 = "guide2@gmail.com";

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

        User u2 = new User();
        u2.setFirstName("GUIDE 1");
        u2.setLastName("LASTNAME");
        u2.setUsername(usernameGuide1);
        u2.setPassword("password");
        u2.setOrganization("Czech Technical University in Prague");
        u2.setUserEnabled(true);
        dao.saveOrUpdate(u2);

        User u3 = new User();
        u3.setFirstName("GUIDE 2");
        u3.setLastName("LASTNAME");
        u3.setUsername(usernameGuide2);
        u3.setPassword("password");
        u3.setOrganization("Czech Technical University in Prague");
        u3.setUserEnabled(true);
        dao.saveOrUpdate(u3);

    }

    @Test
    public void testPrintUser() throws Exception {
        User u = dao.getByPropertyUnique("username",username, User.class);
        u.print();
    }

    @Test
    public void testAddGroup() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);
        Long id = g.getId();
        System.out.println(id);
        u.print();

        // -----------------------------

        dao.remove(r);
        u.print();
        Assert.assertNull(dao.getById(id, Group.class));

    }

    @Test
    public void testRemoveGroup() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);
        Long id = g.getId();
        System.out.println(id);
        u.print();

        dao.remove(g);
        u.print();

    }

    @Test
    public void testAddGroupAndGuide() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);
        Long id = g.getId();
        System.out.println(id);
        u.print();

        User u2 = dao.getByPropertyUnique("username", usernameGuide1, User.class);
        g.setGuide(u2);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();

        dao.remove(r);

        u.print();
        u2.print();

    }

    @Test
    public void testChangeGroupAndGuide() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);
        Long id = g.getId();
        System.out.println(id);
        u.print();

        User u2 = dao.getByPropertyUnique("username", usernameGuide1, User.class);
        User u3 = dao.getByPropertyUnique("username", usernameGuide2, User.class);

        g.setGuide(u2);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();
        u3.print();

        System.out.println("CHANGED GUIDE TO guide2@gmail.com");

        g.setGuide(u3);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();
        u3.print();

    }

    @Test
    public void testGroupStartingPosition() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);
        Station s1 = r.getStations().get(0);
        Station s2 = r.getStations().get(1);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);
        Long id = g.getId();
        u.print();

        User u2 = dao.getByPropertyUnique("username", usernameGuide1, User.class);
        g.setGuide(u2);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();

        System.out.println("--- ADDED STARTING POSITION ---");

        g.setStartingPosition(s1);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();

        System.out.println("--- CHANGED STARTING POSITION ---");

        g.setStartingPosition(s2);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();

    }

    @Test
    public void testDeleteGroupWithEverything() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);
        Station s1 = r.getStations().get(0);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);

        User u2 = dao.getByPropertyUnique("username", usernameGuide1, User.class);
        g.setGuide(u2);
        g.setStartingPosition(s1);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();

        System.out.println("--- GROUP DELETED ---");

        dao.remove(g);

        u.print();
        u2.print();

    }

    @Test
    public void testAddGroupSizes() throws Exception {

        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);
        Station s1 = r.getStations().get(0);

        Group g = new Group();
        dao.saveOrUpdate(g);
        r.addGroup(g);
        dao.saveOrUpdate(u);

        User u2 = dao.getByPropertyUnique("username", usernameGuide1, User.class);
        g.setGuide(u2);
        g.setStartingPosition(s1);
        dao.saveOrUpdate(g);

        u.print();
        u2.print();

        System.out.println("-------------- ADDED GROUP SIZES --------------------");

        for (int i = 0; i < 3; i++) {

            GroupSize groupSize = new GroupSize();
            groupSize.setSize((i+1) * 3);
            groupSize.setTimestamp(DateTime.now().plusMinutes(10 * i));
            g.addGroupSize(groupSize);

        }

        dao.saveOrUpdate(g);

        u.print();
        u2.print();

    }
}
