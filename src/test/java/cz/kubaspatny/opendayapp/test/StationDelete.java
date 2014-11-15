package cz.kubaspatny.opendayapp.test;

import cz.kubaspatny.opendayapp.bo.*;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 15/11/2014
 * Time: 23:20
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
public class StationDelete {


    @Autowired
    private GenericDao dao;

    private String username = "kuba.spatny@gmail.com";
    private String usernameGuide1 = "guide1@gmail.com";
    private String usernameGuide2 = "guide2@gmail.com";

    @Before
    public void setUp() throws Exception {
        User u = dao.getByPropertyUnique("username",username, User.class);

        Route r = u.getEvents().get(0).getRoutes().get(0);
        Station s1 = r.getStations().get(0);
        Station s2 = r.getStations().get(1);
        Station s3 = r.getStations().get(2);

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

        LocationUpdate locationUpdate = new LocationUpdate();
        locationUpdate.setTimestamp(DateTime.now());
        locationUpdate.setType(LocationUpdate.Type.CHECKIN);
        s1.addLocationUpdate(locationUpdate);
        g.addLocationUpdate(locationUpdate);

        locationUpdate = new LocationUpdate();
        locationUpdate.setTimestamp(DateTime.now());
        locationUpdate.setType(LocationUpdate.Type.CHECKOUT);
        s1.addLocationUpdate(locationUpdate);
        g.addLocationUpdate(locationUpdate);

        locationUpdate = new LocationUpdate();
        locationUpdate.setTimestamp(DateTime.now());
        locationUpdate.setType(LocationUpdate.Type.SKIP);
        s2.addLocationUpdate(locationUpdate);
        g.addLocationUpdate(locationUpdate);

        dao.saveOrUpdate(g);

        u.print();
        u2.print();

        // ------------------
        System.out.println("************************************************************************************");
        dao.remove(s1);

        u2 = dao.getByPropertyUnique("username", usernameGuide1, User.class);

        dao.saveOrUpdate(u2);

        u.print();
        u2.print();

        Long id = u2.getGroups().get(0).getLocationUpdates().get(0).getStation().getId();
        Assert.assertNull(dao.getById(id, Station.class));

    }
}
