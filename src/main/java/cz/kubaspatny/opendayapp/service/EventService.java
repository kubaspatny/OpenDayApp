package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.stereotype.Component;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 22/11/2014
 * Time: 22:47
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

@Component("eventService")
public class EventService extends DataAccessService implements IEventService {


    @Override
    public EventDto getEvent(Long id) throws DataAccessException {

        Event e = dao.getById(id, Event.class);
        if(e == null)  throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);
        return EventDto.map(e, new EventDto(), null);
    }

    @Override
    public Long addEvent(Long userId, EventDto event) throws DataAccessException {

        if(userId == null || event == null) throw new DataAccessException("Passed Argument is null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        User u = dao.getById(userId, User.class);
        Event e = EventDto.map(event, new Event(), null);
        u.addEvent(e);

        dao.saveOrUpdate(e);
        dao.saveOrUpdate(u);

        return e.getId();
    }

    @Override
    public void updateEvent(EventDto event) throws DataAccessException {

        if(event == null || event.getId() == null) throw new DataAccessException("Passed argument event or its ID is null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        Event e = dao.getById(event.getId(), Event.class);
        dao.saveOrUpdate(EventDto.map(event, e, null));

    }

    @Override
    public void removeEvent(Long id) throws DataAccessException {
        dao.removeById(id, Event.class);
    }

    @Override
    public void addEmailToList(Long id, String email) throws DataAccessException {
        Event e = dao.getById(id, Event.class);
        if(e == null)  throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        e.addEmailToList(email);
        dao.saveOrUpdate(e);
    }

    @Override
    public void removeEmailFromList(Long id, String email) throws DataAccessException {
        Event e = dao.getById(id, Event.class);
        if(e == null)  throw new DataAccessException("Instance not found!", DataAccessException.ErrorCode.INSTANCE_NOT_FOUND);

        e.removeEmailFromList(email);
        dao.saveOrUpdate(e);
    }
}
