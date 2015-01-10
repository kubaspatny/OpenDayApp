package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 23/11/2014
 * Time: 15:26
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
public interface IRouteService {

    /**
     * Returns a RouteDto object for the specified id.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public RouteDto getRoute(Long id) throws DataAccessException;

    /**
     * Creates a route in several copies. The method created as many routes as @routeStartingTimes.size(), where each route
     * has different starting time from @routeStartingTimes. All routes have all @stations that are also created and saved, groups created from @startingPosition_guideEmail
     * and station managers with emails @stationManagerEmails. Guides' and station managers' profiles must be created before calling this method!
     * @param eventId               id of the parent Event
     * @param name                  name of the create route(s)
     * @param hexColor              color code in hexadecimal format, e.g. "0266C8"
     * @param information           route information
     * @param routeStartingTimes    list of starting times for created route(s)
     * @param stations              stations
     * @param startingPosition_guideEmail   map of guides' emails and starting position, e.g. {1, "example@gmail.com"}
     * @param stationManagerEmails  email addresses of station managers at this route
     * @return  List of ids of created Route(s)
     */
    @PreAuthorize("hasPermission(#eventId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Event'), 'WRITE')")
    public List<Long> saveRoute(Long eventId, String name, String hexColor, String information, List<DateTime> routeStartingTimes,
                          List<StationDto> stations, HashMap<Integer, String> startingPosition_guideEmail,
                          List<String> stationManagerEmails) throws DataAccessException;

    /**
     * Removes route with specified Id.
     */
    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'WRITE')")
    public void removeRoute(Long id) throws DataAccessException;

    /**
     * This method only updates route's color, name, time and information. For other changes use StationService, GroupService or RouteService#add/removeStationManager.
     * @param route
     * @throws DataAccessException
     */
    @PreAuthorize("hasPermission(#route.id, #route.ACLObjectIdentityClass, 'WRITE')")
    public void updateRoute(RouteDto route) throws DataAccessException;

}
