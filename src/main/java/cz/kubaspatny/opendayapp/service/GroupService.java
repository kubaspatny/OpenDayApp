package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.*;
import cz.kubaspatny.opendayapp.dto.*;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 25/11/2014
 * Time: 22:25
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

@Component("groupService")
public class GroupService extends DataAccessService implements IGroupService {

    @Autowired
    IUserService userService;

    @Autowired
    private IGcmService gcmService;

    @Override
    public Long addGroup(Long routeId, Integer startingPosition, String email) throws DataAccessException {
        User u = dao.getByPropertyUnique("email", email.toLowerCase(), User.class);
        if(u == null || !u.isUserEnabled()) throw new DataAccessException("User was deactivated!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return addGroup(routeId, startingPosition, u.getId());
    }

    @Override
    public Long addGroup(RouteDto route, Integer startingPosition, UserDto guide) throws DataAccessException {
        if(route == null || startingPosition == null || guide == null) throw new DataAccessException("Parameters cannot be null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        return addGroup(route.getId(), startingPosition, guide.getId());
    }

    @Override
    public Long addGroup(Long routeId, Integer startingPosition, Long guideId) throws DataAccessException {

        Route r = dao.getById(routeId, Route.class);
        User u = dao.getById(guideId, User.class);

        if(r == null || u == null) throw new DataAccessException("Could find Route or User!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        Group g = new Group();
        g.setStartingPosition(startingPosition);
        g.setGuide(u);
        r.addGroup(g);
        dao.saveOrUpdate(g);

        ObjectIdentity oi = new ObjectIdentityImpl(Group.class, g.getId());
        ObjectIdentity parentIdentity = new ObjectIdentityImpl(Route.class, r.getId());
        saveOrUpdateACL(oi, parentIdentity, true);

        Sid sid = new PrincipalSid(u.getUsername());
        addPermission(oi, sid, BasePermission.ADMINISTRATION);
        addPermission(parentIdentity, sid, BasePermission.READ);
        addPermission(new ObjectIdentityImpl(Event.class, r.getEvent().getId()), sid, BasePermission.READ);

        return g.getId();

    }

    @Override
    public GroupDto getGroup(Long id, boolean latest, boolean fullLists) throws DataAccessException {

        Group g = dao.getById(id, Group.class);
        if(g == null) throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        List<String> ignorableProperties = new ArrayList<String>();
        if(!latest){
            ignorableProperties.add("latestGroupSize");
            ignorableProperties.add("latestLocationUpdate");
        }

        if(!fullLists){
            ignorableProperties.add("groupSizes");
            ignorableProperties.add("locationUpdates");
        }

        return GroupDto.map(g, new GroupDto(), ignorableProperties);

    }

    @Override
    public void setGroupStartingPosition(Long groupID, Integer startingPosition) throws DataAccessException {

        Group g = dao.getById(groupID, Group.class);

        if(g == null || startingPosition == null) throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        g.setStartingPosition(startingPosition);
        dao.saveOrUpdate(g);

    }

    @Override
    public void addGroupSize(Long groupID, GroupSizeDto size) throws DataAccessException {

        Group g = dao.getById(groupID, Group.class);

        if(g == null || size == null) throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        GroupSize groupSize = GroupSizeDto.map(size, new GroupSize(), null);
        int before = g.getGroupSizes().size();
        g.addGroupSize(groupSize);
        int after = g.getGroupSizes().size();
        System.out.println(before + " -> " + after);

        dao.saveOrUpdate(g);

    }

    @Override
    public void removeGroup(Long id) throws DataAccessException {
        Group g = dao.getById(id, Group.class);

        removePermissionEntry(new ObjectIdentityImpl(Route.class, g.getRoute().getId()), new PrincipalSid(g.getGuide().getUsername()), BasePermission.READ);
        removePermissionEntry(new ObjectIdentityImpl(Event.class, g.getRoute().getEvent().getId()), new PrincipalSid(g.getGuide().getUsername()), BasePermission.READ);
        aclService.deleteAcl(new ObjectIdentityImpl(Group.class, id), false);

        dao.removeById(id, Group.class);
    }

    @Override
    public Long addLocationUpdate(LocationUpdateDto locationUpdate) throws DataAccessException {

        if(locationUpdate == null) throw new DataAccessException("LocationUpdateDto is null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        LocationUpdate update = LocationUpdateDto.map(locationUpdate, new LocationUpdate(), null);
        Station s = dao.getById(locationUpdate.getStation().getId(), Station.class);
        s.addLocationUpdate(update);

        Group g = dao.getById(locationUpdate.getGroup().getId(), Group.class);
        g.addLocationUpdate(update);

        sendSyncNotification(g.getRoute());
        sendCloseGroupNotification(g.getRoute(), g.getStartingPosition(), s.getId(), update.getType());

        return dao.saveOrUpdate(update).getId();
    }

    private static int getAfterIndex(int current, int total){
        return (current + 1) % total;
    }

    private static int getBeforeIndex(int current, int total){
        int result = (current - 1) % total;
        if(result <= 0) result += total;
        return result;
    }

    private void sendCloseGroupNotification(Route route, int groupStartingPosition, Long stationId, LocationUpdate.Type updateType){
        try {

            System.out.println("sendCloseGroupNotification");

            String groupBefore = null;
            List<String> groupBeforeIds = null;
            int groupBeforeIndex = getBeforeIndex(groupStartingPosition, route.getStations().size());

            String groupAfter = null;
            List<String> groupAfterIds = null;
            int groupAfterIndex = getAfterIndex(groupStartingPosition, route.getStations().size());


            for(Group g : route.getGroups()){
                if(groupStartingPosition != groupBeforeIndex && g.getStartingPosition().equals(groupBeforeIndex)){
                    groupBefore = g.getGuide().getUsername();
                } else if(groupStartingPosition != groupAfterIndex && g.getStartingPosition().equals(groupAfterIndex)){
                    groupAfter = g.getGuide().getUsername();
                }
            }

            if(groupBefore != null){
                System.out.println("groupBefore is not null");
                groupBeforeIds = gcmService.getRegisteredDevices(groupBefore);

                HashMap<String, String> data = new HashMap<String, String>();
                data.put(GcmService.EXTRA_NOTIFICATION_TYPE, GcmService.TYPE_LOCATION_UPDATE + "");
                data.put(GcmService.EXTRA_ROUTE_ID, route.getId().toString());
                data.put(GcmService.EXTRA_STATION_ID, stationId.toString());
                data.put(GcmService.EXTRA_UPDATE_TYPE, updateType.toString());
                // From the recipient's point of view, the group after
                // has sent a new location update
                data.put(GcmService.EXTRA_GROUP_AFTER, "true");
                gcmService.sendNotification(data, groupBeforeIds);
            }

            if(groupAfter != null){
                System.out.println("groupAfter is not null");
                groupAfterIds = gcmService.getRegisteredDevices(groupBefore);

                HashMap<String, String> data = new HashMap<String, String>();
                data.put(GcmService.EXTRA_NOTIFICATION_TYPE, GcmService.TYPE_LOCATION_UPDATE + "");
                data.put(GcmService.EXTRA_ROUTE_ID, route.getId().toString());
                data.put(GcmService.EXTRA_STATION_ID, stationId.toString());
                data.put(GcmService.EXTRA_UPDATE_TYPE, updateType.toString());
                // From the recipient's point of view, the group after
                // has sent a new location updateauth
                data.put(GcmService.EXTRA_GROUP_BEFORE, "true");
                gcmService.sendNotification(data, groupAfterIds);
            }

        } catch (Exception e){
            System.out.println("Error sending notification:" + e.getLocalizedMessage());
        }

    }

    private void sendSyncNotification(Route route){

        try {
            List<String> usernames = new ArrayList<String>();
            List<String> regIds = new ArrayList<String>();

            for(Group g : route.getGroups()){
                usernames.add(g.getGuide().getUsername());
            }

            for(String u : usernames){
                try {
                    regIds.addAll(gcmService.getRegisteredDevices(u));
                } catch (Exception e){
                }
            }

            HashMap<String, String> data = new HashMap<String, String>();
            data.put(GcmService.EXTRA_NOTIFICATION_TYPE, GcmService.TYPE_SYNC_ROUTE + "");
            data.put(GcmService.EXTRA_ROUTE_ID, route.getId().toString());

            gcmService.sendNotification(data, regIds);
        } catch (Exception e){
            System.out.println("Error sending notification:" + e.getLocalizedMessage());
        }

    }

    @Override
    public void setLastUpdated(Long groupId, DateTime time) throws DataAccessException {

        Group g = dao.getById(groupId, Group.class);
        if(g == null) throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        g.setLastUpdated(time);
        g.setInactivityNotified(false);
        dao.saveOrUpdate(g);

    }

    @Override
    public Long getGroupCount(String username) throws DataAccessException {
        UserDto u = userService.getUser(username);
        return concreteDao.countUpcomingEventsGroups(username);
    }

    @Override
    public List<GroupDto> getGroups(String username, int page, int pageSize) throws DataAccessException {
        UserDto u = userService.getUser(username);
        List<Group> groups = concreteDao.getUpcomingEventsGroups(username, page, pageSize);
        List<GroupDto> groupDtos = new ArrayList<GroupDto>();

        List<String> ignore = DtoMapperUtil.getGroupIgnoredProperties();
        ignore.add("guide");

        for(Group g : groups){
            groupDtos.add(GroupDto.map(g, new GroupDto(), ignore));
        }

        return groupDtos;

    }

    @Override
    public List<GroupDto> getGroupsWithCurrentLocation(Long routeId) throws DataAccessException {

        Route r = dao.getById(routeId, Route.class);

        List<String> ignore = new ArrayList<String>();
        ignore.add("route");
        ignore.add("groupSizes");
        ignore.add("locationUpdates");

        List<GroupDto> groups = new ArrayList<GroupDto>();

        for(Group g : r.getGroups()){
            groups.add(GroupDto.map(g, new GroupDto(), ignore));
        }

        return groups;

    }
}

























