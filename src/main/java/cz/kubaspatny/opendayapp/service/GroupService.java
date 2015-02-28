package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.*;
import cz.kubaspatny.opendayapp.dto.*;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        return dao.saveOrUpdate(update).getId();
    }

    @Override
    public void setLastUpdated(Long groupId, DateTime time) throws DataAccessException {

        Group g = dao.getById(groupId, Group.class);
        if(g == null) throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        g.setLastUpdated(time);
        g.setInactivityNotified(true);
        dao.saveOrUpdate(g);

    }
}

























