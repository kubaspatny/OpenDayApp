package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.file.AccessDeniedException;

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
    public Long addEvent(EventDto event) throws DataAccessException {

        String username = null;

        try {
            username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        } catch(Exception e){
            throw new DataAccessException("User not found!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);
        }

        if(username == null || event == null) throw new DataAccessException("Passed Argument is null!", DataAccessException.ErrorCode.ILLEGAL_ARGUMENT);

        User u = dao.getByPropertyUnique("username", username, User.class);
        Event e = EventDto.map(event, new Event(), null);
        u.addEvent(e);

        dao.saveOrUpdate(e);
        dao.saveOrUpdate(u);

        // --------------------- ACL ---------------------
        Sid sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        addPermission(e, sid, BasePermission.WRITE);
        addPermission(e, sid, BasePermission.READ);

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
        aclService.deleteAcl(new ObjectIdentityImpl(Event.class, id), true);
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
