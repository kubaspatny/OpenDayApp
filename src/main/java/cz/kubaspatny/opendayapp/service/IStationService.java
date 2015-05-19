package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 25/11/2014
 * Time: 14:47
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
public interface IStationService {

    /**
     * Returns station with specified id.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public StationDto getStation(Long id) throws DataAccessException;

    /**
     * Add a new station to the route specified by @routeId.
     */
    @PreAuthorize("hasPermission(#routeId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'WRITE')")
    public Long addStation(Long routeId, StationDto stationDto) throws DataAccessException;

    /**
     * Updates a station, @stationDto must have non-null id.
     */
    @PreAuthorize("hasPermission(#stationDto.id, #stationDto.ACLObjectIdentityClass, 'WRITE')")
    public void updateStation(StationDto stationDto) throws DataAccessException;

    /**
     * Removes station with specified id.
     */
    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Station'), 'WRITE')")
    public void removeStation(Long id) throws DataAccessException;

    /**
     * Returns a list of route's stations.
     * @param routeId route id
     * @throws DataAccessException
     */
    @PreAuthorize("hasPermission(#routeId, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Route'), 'READ')")
    public List<StationDto> getStations(Long routeId) throws DataAccessException;

}
