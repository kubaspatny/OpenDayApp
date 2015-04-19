package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 25/11/2014
 * Time: 14:58
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
@Component("stationService")
public class StationService extends DataAccessService implements IStationService {

    @Override
    public StationDto getStation(Long id) throws DataAccessException {
        Station s = dao.getById(id, Station.class);
        if(s == null) throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return StationDto.map(s, new StationDto(), DtoMapperUtil.getStationIgnoredProperties()) ;
    }

    @Override
    public Long addStation(Long routeId, StationDto stationDto) throws DataAccessException {
        Route r = dao.getById(routeId, Route.class);
        if(r == null) throw new DataAccessException("Route with id " + routeId + " not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        Station s = StationDto.map(stationDto, new Station(), null);
        r.addStation(s);
        dao.saveOrUpdate(s);

        ObjectIdentity oi = new ObjectIdentityImpl(Station.class, s.getId());
        ObjectIdentity parentIdentity = new ObjectIdentityImpl(Route.class, r.getId());
        saveOrUpdateACL(oi, parentIdentity, true);

        return s.getId();
    }

    @Override
    public void updateStation(StationDto stationDto) throws DataAccessException {

        if(stationDto == null) throw new DataAccessException("Pameter stationDto is null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        Station s = dao.getById(stationDto.getId(), Station.class);
        if(s == null) throw new DataAccessException("Instance you're trying to update was not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        dao.saveOrUpdate(StationDto.map(stationDto, s, null));
    }

    @Override
    public void removeStation(Long id) throws DataAccessException {
        dao.removeById(id, Station.class);
        aclService.deleteAcl(new ObjectIdentityImpl(Station.class, id), false);
    }

    @Override
    public List<StationDto> getStations(Long routeId) throws DataAccessException {
        Route r = dao.getById(routeId, Route.class);
        List<StationDto> stationDtos = new ArrayList<StationDto>();

        List<String> stationIgnoredProperties = new ArrayList<String>();
        stationIgnoredProperties.add("route");

        for(Station s : r.getStations()){
            stationDtos.add(StationDto.map(s, new StationDto(), stationIgnoredProperties));
        }

        return stationDtos;
    }
}
