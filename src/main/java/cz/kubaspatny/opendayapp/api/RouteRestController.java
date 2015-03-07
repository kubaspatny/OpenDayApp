package cz.kubaspatny.opendayapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.kubaspatny.opendayapp.api.json.CustomExposeExclusionStrategy;
import cz.kubaspatny.opendayapp.api.json.DateTimeSerializer;
import cz.kubaspatny.opendayapp.dto.GroupDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IGroupService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IStationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 4/3/2015
 * Time: 18:28
 * Copyright 2015 Jakub Spatny
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
@RestController
@RequestMapping("/v1/route")
public class RouteRestController extends ExceptionHandlingController {

    @Autowired IStationService stationService;
    @Autowired IRouteService routeService;
    @Autowired IGroupService groupService;

    @RequestMapping(value = "/{routeId}")
    @ResponseBody
    public String getRoute(@PathVariable Long routeId) throws DataAccessException {

        RouteDto routeDto = routeService.getRoute(routeId);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer())
                .create();

        return gson.toJson(routeDto);

    }

    @RequestMapping(value = "/{routeId}/stations")
    @ResponseBody
    public String getStations(@PathVariable Long routeId) throws DataAccessException {

        List<StationDto> stationDtos = stationService.getStations(routeId);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer())
                .create();

        return gson.toJson(stationDtos);

    }

    @RequestMapping(value = "/{routeId}/groups")
    @ResponseBody
    public String getGroups(@PathVariable Long routeId) throws DataAccessException {

        List<GroupDto> groups = groupService.getGroupsWithCurrentLocation(routeId);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer())
                .create();

        return gson.toJson(groups);

    }

}