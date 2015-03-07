package cz.kubaspatny.opendayapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.kubaspatny.opendayapp.api.json.CustomExposeExclusionStrategy;
import cz.kubaspatny.opendayapp.api.json.DateTimeSerializer;
import cz.kubaspatny.opendayapp.dto.GroupDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IGroupService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 3/3/2015
 * Time: 21:20
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
@RequestMapping("/v1/user")
public class UserRestController extends ExceptionHandlingController {

    @Autowired private IGroupService groupService;

    @RequestMapping(value = "/{username}/groups")
    @ResponseBody
    public String getGroups(@PathVariable String username, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize) throws DataAccessException {

        List<GroupDto> groups = groupService.getGroups(username, page, pageSize);

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer())
                .addSerializationExclusionStrategy(new CustomExposeExclusionStrategy())
                .create();

        return gson.toJson(groups);

    }

    @RequestMapping(value = "/{username}/groups/count")
    @ResponseBody
    public String getGroupCount(@PathVariable String username) throws DataAccessException {
        Gson gson = new Gson();
        return gson.toJson(groupService.getGroupCount(username));
    }

}
