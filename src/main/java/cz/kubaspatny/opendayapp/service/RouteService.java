package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

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

    @Override
    public Long saveRoute(Long eventId, String name, String hexColor, String information, List<DateTime> routeStartingTimes, List<StationDto> stations, HashMap<Long, String> stationCreationID_guideEmail, List<String> stationManagerEmails) throws DataAccessException {
        return null;
    }

    @Override
    public void removeRoute(Long id) throws DataAccessException {

    }

    @Override
    public void updateRoute(RouteDto route) throws DataAccessException {

    }
}
