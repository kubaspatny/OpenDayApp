package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;


/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 20/12/2014
 * Time: 21:33
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

@Component("testService")
public class TestService extends DataAccessService {

    @Autowired private IEventService eventService;
    @Autowired private IRouteService routeService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getSomeText(){
        return "Hello!";
    }

    public Long addSecuredEvent(EventDto eventDto){

        Long id;
        try {
            id = eventService.addEvent(eventDto);
        } catch (DataAccessException e){
            return null;
        }

        ObjectIdentity oi = new ObjectIdentityImpl(Event.class, id);
        Sid sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Permission p = BasePermission.READ;

        // Create or update the relevant ACL
        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oi);
        }

        // Now grant some permissions via an access control entry (ACE)
        acl.insertAce(acl.getEntries().size(), p, sid, true);
        aclService.updateAcl(acl);

        return id;
    }

    public Long addSecuredRoute(Long eventId, String name, String hexColor, String information, List<DateTime> routeStartingTimes, List<StationDto> stations, HashMap<Integer, String> guideEmails, List<String> stationManagerEmails){

        Long id;
        try {
            id = routeService.saveRoute(eventId, name, hexColor, information, routeStartingTimes, stations, guideEmails, stationManagerEmails).get(0);
        } catch (DataAccessException e){
            return null;
        }

        ObjectIdentity oi = new ObjectIdentityImpl(Route.class, id);

        Sid sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Permission p = BasePermission.READ;

        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oi);
        }

        ObjectIdentity parentIdentity = new ObjectIdentityImpl(Event.class, eventId);
        MutableAcl aclParent = (MutableAcl) aclService.readAclById(parentIdentity);
        acl.setParent(aclParent);

        aclService.updateAcl(acl);

        return id;
    }

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public Event getSecuredEvent(Long id){

        try {
            Event e = dao.getById(id, Event.class);
            return e;
        } catch (Exception e){
            return null;
        }

    }

    //@PostAuthorize("hasPermission(returnObject.id, #clazz.name, 'READ')")
    //@PostAuthorize("hasPermission(returnObject.id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).name(T(cz.kubaspatny.opendayapp.bo.Event).class), 'READ')")
    //@PostAuthorize("hasPermission(returnObject.id, T(cz.kubaspatny.opendayapp.utils.SpelUtil).name(#clazz), 'READ')")
    //@PostAuthorize("hasPermission(returnObject.id, 'cz.kubaspatny.opendayapp.bo.Event', 'READ')")
    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public EventDto getSecuredEventDto(Long id){

        try {
            return eventService.getEvent(id);
        } catch (DataAccessException e){
            return null;
        }

    }

    @PostAuthorize("hasPermission(returnObject.id, returnObject.ACLObjectIdentityClass, 'READ')")
    public RouteDto getSecuredRouteDto(Long id){

        try {
            return routeService.getRoute(id);
        } catch (DataAccessException e){
            return null;
        }

    }

}
