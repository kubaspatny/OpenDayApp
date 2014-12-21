package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
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
public class TestService {

    @Autowired private IEventService eventService;
    @Autowired private GenericDao dao;
    private JdbcMutableAclService aclService;

    public TestService(JdbcMutableAclService aclService) {
        Assert.notNull(aclService);
        this.aclService = aclService;
        aclService.setSidIdentityQuery("select currval('acl_sid_id_seq')");
        aclService.setClassIdentityQuery("select currval('acl_class_id_seq')");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getSomeText(){
        return "Hello!";
    }

    public Long addSecuredEvent(Long userId, EventDto eventDto){

        Long id;
        try {
            id = eventService.addEvent(userId, eventDto);
        } catch (DataAccessException e){
            return null;
        }

        ObjectIdentity oi = new ObjectIdentityImpl(Event.class, id);
        Sid sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Permission p = BasePermission.READ;
//
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

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public Event getSecuredEvent(Long id){

        try {
            Event e = dao.getById(id, Event.class);
            return e;
        } catch (Exception e){
            return null;
        }

    }

}
