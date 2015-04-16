package cz.kubaspatny.opendayapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.kubaspatny.opendayapp.api.json.DateTimeSerializer;
import cz.kubaspatny.opendayapp.api.json.JsonWrapper;
import cz.kubaspatny.opendayapp.dto.GroupSizeDto;
import cz.kubaspatny.opendayapp.dto.GroupStartingPosition;
import cz.kubaspatny.opendayapp.dto.LocationUpdateDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IGroupService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IStationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 4/3/2015
 * Time: 19:16
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
@RequestMapping("/v1/group")
public class GroupRestController extends ExceptionHandlingController {

    @Autowired IGroupService groupService;

    @RequestMapping(value = "/locationUpdate", method = RequestMethod.POST)
    @ResponseBody
    public String getRoute(@RequestBody String locationJson) throws DataAccessException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        groupService.updateLastUpdated(a.getName());

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer()).create();
        LocationUpdateDto updateDto = gson.fromJson(locationJson, LocationUpdateDto.class);

        return groupService.addLocationUpdate(updateDto).toString();
    }

    @RequestMapping(value = "/startingPosition", method = RequestMethod.POST)
    @ResponseBody
    public void setGroupStartingPosition(@RequestBody String startingPositionJson) throws DataAccessException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        groupService.updateLastUpdated(a.getName());

        Gson gson = new GsonBuilder().create();
        GroupStartingPosition position = gson.fromJson(startingPositionJson, GroupStartingPosition.class);

        if(position == null || position.getGroupId() == null || position.getGroupId().equals(new Long(0)) ||
            position.getStartingPosition() == null) throw new DataAccessException("Wrong arguments!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        groupService.setGroupStartingPosition(position.getGroupId(), position.getStartingPosition());
    }

    @RequestMapping(value = "/groupSize", method = RequestMethod.POST)
    @ResponseBody
    public void addGroupSize(@RequestBody String groupSizeJson) throws DataAccessException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        groupService.updateLastUpdated(a.getName());

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer()).create();
        GroupSizeDto size = gson.fromJson(groupSizeJson, GroupSizeDto.class);

        if(size == null || size.getGroupId() == null || size.getGroupId().equals(new Long(0)) ||
                size.getTimestamp() == null) throw new DataAccessException("Wrong arguments!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        groupService.addGroupSize(size.getGroupId(), size);
    }

}
