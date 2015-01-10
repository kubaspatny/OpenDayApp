package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 22/11/2014
 * Time: 22:37
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
public interface IEventService {

    /**
     * Returns an EventDto object for specified id.
     */
    @Transactional(readOnly = true)
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public EventDto getEvent(Long id) throws DataAccessException;

    /**
     * Adds a new Event with data from @event to a currently authorized user.
     */
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public Long addEvent(EventDto event) throws DataAccessException;

    /**
     * Updates event. The passed @event must have not-null id.
     */
    @PreAuthorize("hasPermission(#event.id, #event.ACLObjectIdentityClass, 'WRITE')")
    public void updateEvent(EventDto event) throws DataAccessException;

    /**
     * Removes the event with id @id.
     */
    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Event'), 'WRITE')")
    public void removeEvent(Long id) throws DataAccessException;

    /**
     * Adds an email address to the list of emails.
     */
    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Event'), 'WRITE')")
    public void addEmailToList(Long id, String email) throws DataAccessException;

    /**
     * Removes an email address from the list of emails.
     */
    @PreAuthorize("hasPermission(#id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).getACLObjectIdentityClass('Event'), 'WRITE')")
    public void removeEmailFromList(Long id, String email) throws DataAccessException;

}
