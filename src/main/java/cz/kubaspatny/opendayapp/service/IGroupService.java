package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.*;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

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

    @PreAuthorize("hasPermission(#route.id, #route.ACLObjectIdentityClass, 'WRITE')")
    public Long addGroup(RouteDto route, Integer startingPosition, UserDto guide) throws DataAccessException;

    @PreAuthorize("hasPermission(#routeId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'WRITE')")
    public Long addGroup(Long routeId, Integer startingPosition, Long guideId) throws DataAccessException;

    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public GroupDto getGroup(Long id, boolean latest, boolean fullLists) throws DataAccessException;

    @PreAuthorize("hasPermission(#groupID, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'WRITE') or hasPermission(#groupID, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'ADMINISTRATION')")
    public void setGroupStartingPosition(Long groupID, Integer startingPosition) throws DataAccessException;

    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'WRITE')")
    public void removeGroup(Long id) throws DataAccessException;

    @PreAuthorize("hasPermission(#locationUpdate.group.id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Group'), 'ADMINISTRATION')")
    public Long addLocationUpdate(LocationUpdateDto locationUpdate) throws DataAccessException;

    /**
     * Sets the time when the group last communicated with the server. This time is used for determining the activity of
     * a group.
     */
    public void setLastUpdated(Long groupId, DateTime time) throws DataAccessException;

}
