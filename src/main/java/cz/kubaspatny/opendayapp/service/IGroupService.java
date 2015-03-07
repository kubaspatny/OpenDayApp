package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.*;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 25/11/2014
 * Time: 22:13
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

@Transactional
public interface IGroupService {

    /**
     * Adds a new group to the @route for @guide with @startingPosition.
     */
    @PreAuthorize("hasPermission(#route.id, #route.ACLObjectIdentityClass, 'WRITE')")
    public Long addGroup(RouteDto route, Integer startingPosition, UserDto guide) throws DataAccessException;

    /**
     * Adds a new group to the route with specified id for @guide with @startingPosition.
     */
    @PreAuthorize("hasPermission(#routeId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'WRITE')")
    public Long addGroup(Long routeId, Integer startingPosition, Long guideId) throws DataAccessException;

    /**
     * Adds a new group to the route with specified id for guide identified by @email and with @startingPosition.
     */
    @PreAuthorize("hasPermission(#routeId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'WRITE')")
    public Long addGroup(Long routeId, Integer startingPosition, String email) throws DataAccessException;

    /**
     * Returns a group with specified id.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public GroupDto getGroup(Long id, boolean latest, boolean fullLists) throws DataAccessException;

    /**
     * Sets group's starting position parameter.
     */
    @PreAuthorize("hasPermission(#groupID, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'WRITE') or hasPermission(#groupID, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'ADMINISTRATION')")
    public void setGroupStartingPosition(Long groupID, Integer startingPosition) throws DataAccessException;

    /**
     * Removes group with specified id.
     */
    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'WRITE')")
    public void removeGroup(Long id) throws DataAccessException;

    /**
     * Adds location update according to the passed object @locationUpdate. @locationUpdate object's parameters cannot be null!
     */
    @PreAuthorize("hasPermission(#locationUpdate.group.id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'ADMINISTRATION')")
    public Long addLocationUpdate(LocationUpdateDto locationUpdate) throws DataAccessException;

    /**
     * Sets the time when the group last communicated with the server. This time is used for determining the activity of
     * a group.
     */
    public void setLastUpdated(Long groupId, DateTime time) throws DataAccessException;

    /**
     * Returns a number of groups user with given username guides.
     */
    public Long getGroupCount(String username) throws DataAccessException;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject.id, filterObject.ACLObjectIdentityClass, 'READ')")
    public List<GroupDto> getGroups(String username, int page, int pageSize) throws DataAccessException;

    @PreAuthorize("hasPermission(#routeId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'READ')")
    public List<GroupDto> getGroupsWithCurrentLocation(Long routeId) throws DataAccessException;

}
