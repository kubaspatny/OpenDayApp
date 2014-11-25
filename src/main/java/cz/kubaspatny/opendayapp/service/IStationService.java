package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.transaction.annotation.Transactional;


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

    @Transactional(readOnly = true)
    public StationDto getStation(Long id) throws DataAccessException;

    public Long addStation(Long routeId, StationDto stationDto) throws DataAccessException;

    public void updateStation(StationDto stationDto) throws DataAccessException;

    public void removeStation(Long id) throws DataAccessException;

}
