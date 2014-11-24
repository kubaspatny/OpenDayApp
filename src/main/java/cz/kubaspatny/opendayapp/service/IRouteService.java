package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
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

    @Transactional(readOnly = true)
    public RouteDto getRoute(Long id) throws DataAccessException;

    public Long saveRoute(Long eventId, String name, String hexColor, String information, List<DateTime> routeStartingTimes,
                          List<StationDto> stations, HashMap<Long, String> stationCreationID_guideEmail,
                          List<String> stationManagerEmails) throws DataAccessException;

    public void removeRoute(Long id) throws DataAccessException;

    /**
     * This method only updates route's color, name, time and information. For other changes use StationService, GroupService or RouteService#add/removeStationManager.
     * @param route
     * @throws DataAccessException
     */
    public void updateRoute(RouteDto route) throws DataAccessException;

}
