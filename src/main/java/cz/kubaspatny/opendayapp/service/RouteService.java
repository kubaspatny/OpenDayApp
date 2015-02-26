package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.*;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 24/11/2014
 * Time: 21:34
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

@Component("routeService")
public class RouteService extends DataAccessService implements IRouteService {

    @Override
    public RouteDto getRoute(Long id) throws DataAccessException {

        Route r = dao.getById(id, Route.class);
        if(r == null) throw new DataAccessException("Route with id " + id + " doesn't exist!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return RouteDto.map(r, new RouteDto(), null);

    }

    /**
     * Saves a whole route object with stations, groups/guides and station managers.
     * @param guideEmails HashMap where Key == StationDto.creationId, Value == guide email
     * @throws DataAccessException
     */
    @Override
    public List<Long> saveRoute(Long eventId, String name, String hexColor, String information, List<DateTime> routeStartingTimes, List<StationDto> stations, HashMap<Integer, String> guideEmails, List<String> stationManagerEmails) throws DataAccessException {

        if(eventId == null) throw new DataAccessException("Event id is null!", DataAccessException.ErrorCode.INVALID_ID);
        if(name == null || name.isEmpty() || hexColor == null || hexColor.isEmpty() || routeStartingTimes == null || routeStartingTimes.size() == 0) throw new DataAccessException("Paramaters name, hexColor and routeStartingTimes cannot be null or empty!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        List<Long> routeIds = new ArrayList<Long>(routeStartingTimes.size());

        Event e = dao.getById(eventId, Event.class);

        for(DateTime t : routeStartingTimes){

            Route r = new Route();
            r.setName(name);
            r.setHexColor(hexColor);
            r.setInformation(information);
            r.setDate(t);
            e.addRoute(r);
            dao.saveOrUpdate(r);

            routeIds.add(r.getId());

            // add ACL with parent to acl(eventId)
            ObjectIdentity oi = new ObjectIdentityImpl(Route.class, r.getId());
            ObjectIdentity parentIdentity = new ObjectIdentityImpl(Event.class, eventId);
            saveOrUpdateACL(oi, parentIdentity, false);

            Sid owner = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            addPermission(r, owner, BasePermission.WRITE);
            addPermission(r, owner, BasePermission.READ);

            // -----------------------------------

            if(stations != null) {
                for(StationDto stationDto : stations){

                    Station s = new Station();
                    s.setName(stationDto.getName());
                    s.setInformation(stationDto.getInformation());
                    s.setLocation(stationDto.getLocation());
                    s.setRelocationTime(stationDto.getRelocationTime());
                    s.setSequencePosition(stationDto.getSequencePosition());
                    s.setTimeLimit(stationDto.getTimeLimit());
                    r.addStation(s);
                    dao.saveOrUpdate(s);

                    // add ACL with parent to acl(r.getId)
                    oi = new ObjectIdentityImpl(Station.class, s.getId());
                    parentIdentity = new ObjectIdentityImpl(Route.class, r.getId());
                    saveOrUpdateACL(oi, parentIdentity, true);

                }
            }

            if(guideEmails != null){
                for(Map.Entry<Integer, String> entry : guideEmails.entrySet()){

                    User guide = dao.getByPropertyUnique("email", entry.getValue(), User.class);

                    Group g = new Group();
                    g.setStartingPosition(entry.getKey());
                    g.setGuide(guide);
                    r.addGroup(g);
                    dao.saveOrUpdate(g);

                    // add ACL with parent to acl(r.getId)
                    oi = new ObjectIdentityImpl(Group.class, g.getId());
                    parentIdentity = new ObjectIdentityImpl(Route.class, r.getId());
                    saveOrUpdateACL(oi, parentIdentity, true);

                    Sid sid = new PrincipalSid(guide.getUsername());
                    addPermission(oi, sid, BasePermission.ADMINISTRATION);                                  // Group
                    addPermission(parentIdentity, sid, BasePermission.READ);                                // Route
                    addPermission(new ObjectIdentityImpl(Event.class, eventId), sid, BasePermission.READ);  // Event

                }
            }

            if(stationManagerEmails != null){
                for(String email : stationManagerEmails){
                    User stationManager = dao.getByPropertyUnique("email", email, User.class);
                    r.addStationManager(stationManager);

                    Sid sid = new PrincipalSid(stationManager.getUsername());
                    addPermission(new ObjectIdentityImpl(Route.class, r.getId()), sid, BasePermission.READ);    // Route                                // Route
                    addPermission(new ObjectIdentityImpl(Event.class, eventId), sid, BasePermission.READ);      // Event

                }
                dao.saveOrUpdate(r);
            }

        }

        return routeIds;
    }

    @Override
    public void removeRoute(Long id) throws DataAccessException {

        Route r = dao.getById(id, Route.class);

        if(r.getGroups() != null){
            for(Group g : r.getGroups()){
                removePermissionEntry(new ObjectIdentityImpl(Event.class, r.getEvent().getId()), new PrincipalSid(g.getGuide().getUsername()), BasePermission.READ);
            }
        }

        if(r.getStationManagers() != null){
            for(User u : r.getStationManagers()){
                removePermissionEntry(new ObjectIdentityImpl(Event.class, r.getEvent().getId()), new PrincipalSid(u.getUsername()), BasePermission.READ);
            }
        }

        dao.removeById(id, Route.class);
        aclService.deleteAcl(new ObjectIdentityImpl(Route.class, id), true);
    }

    @Override
    public void updateRoute(RouteDto route) throws DataAccessException {
        if(route.getId() == null) throw new DataAccessException("Trying to update object with null ID!", DataAccessException.ErrorCode.INVALID_ID);

        Route r = dao.getById(route.getId(), Route.class);
        dao.saveOrUpdate(RouteDto.map(route, r, null));

    }

    @Override
    public void addStationManager(Long id, String stationManagerEmail) throws DataAccessException {

        Route r = dao.getById(id, Route.class);
        User stationManager = dao.getByPropertyUnique("email", stationManagerEmail, User.class);
        r.addStationManager(stationManager);

        Sid sid = new PrincipalSid(stationManager.getUsername());
        addPermission(new ObjectIdentityImpl(Route.class, r.getId()), sid, BasePermission.READ);                        // Route                                // Route
        addPermission(new ObjectIdentityImpl(Event.class, r.getEvent().getId()), sid, BasePermission.READ);             // Event

        dao.saveOrUpdate(r);

    }

    @Override
    public void removeStationManager(Long id, String stationManagerEmail) throws DataAccessException {

        Route r = dao.getById(id, Route.class);
        User stationManager = dao.getByPropertyUnique("email", stationManagerEmail, User.class);
        r.removeStationManager(stationManager);

        Sid sid = new PrincipalSid(stationManager.getUsername());
        removePermissionEntry(new ObjectIdentityImpl(Route.class, r.getId()), sid, BasePermission.READ);                // Route                                // Route
        removePermissionEntry(new ObjectIdentityImpl(Event.class, r.getEvent().getId()), sid, BasePermission.READ);     // Event

        dao.saveOrUpdate(r);

    }

    @Override
    public List<RouteDto> getRoutes(Long eventId) throws DataAccessException {   // TODO: add a method to DAO -> searchByProperty with sortBy attribute

        Event event = new Event();
        event.setId(eventId);

        Map<String, Object> searchAttrs = new HashMap<String, Object>();
        searchAttrs.put("event", event);

        List<Route> routes = dao.searchByProperty(searchAttrs, Route.class);
        List<RouteDto> routeDtos = new ArrayList<RouteDto>();

        List<String> routeIgnoredProperties = new ArrayList<String>();
        routeIgnoredProperties.add("stations");
        routeIgnoredProperties.add("stationManagers");
        routeIgnoredProperties.add("groups");

        for(Route r : routes){
            routeDtos.add(RouteDto.map(r, new RouteDto(), null));
        }

        return routeDtos;

    }
}
