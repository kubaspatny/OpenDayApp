package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 7/2/2015
 * Time: 16:38
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

@Component("indexBB")
@Scope("request")
public class IndexBB {

    @Autowired private IUserService userService;
    @Autowired private IEventService eventService;

    private boolean eventLoadingError;

    private List<EventDto> events;

    private List<EventDto> testEvents(){
        List<EventDto> eventDtos = new ArrayList<EventDto>();

        for (int i = 1; i <= 6; i++) {

            EventDto event = new EventDto();
            event.setName("Event " + i);
            event.setDate(DateTime.now().plusDays(i));
            event.setInformation("This is basic information.");

            eventDtos.add(event);
        }

        return eventDtos;
    }

    public void deleteEvent(Long id){

        try {
            eventService.removeEvent(id);
        } catch (DataAccessException e){
            // display error
        }

        loadEvents();

    }

    private void loadEvents(){

        UserDto userDto;

        try {
            userDto = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (DataAccessException e){
            eventLoadingError = true;
            return;
        }

        events = userDto.getEvents();

    }

    public List<EventDto> getEvents(){

        if(events == null) loadEvents();
        return events;
    }

    public boolean isEventLoadingError() {
        return eventLoadingError;
    }

    public void edit(String name){
        System.out.println("EDIT: " + name);
    }
}
