package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.*;
import org.springframework.util.Assert;

import javax.persistence.Access;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 20:12
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
public class DataAccessService {

    @Autowired
    protected GenericDao dao;

    protected JdbcMutableAclService aclService;

    public DataAccessService() {
    }

    public DataAccessService(JdbcMutableAclService aclService) {
        Assert.notNull(aclService);
        this.aclService = aclService;
        aclService.setSidIdentityQuery("select currval('acl_sid_id_seq')");
        aclService.setClassIdentityQuery("select currval('acl_class_id_seq')");
    }

    public GenericDao getDao() {
        return dao;
    }

    public void setDao(GenericDao dao) {
        this.dao = dao;
    }

    public JdbcMutableAclService getAclService() {
        return aclService;
    }

    public void setAclService(JdbcMutableAclService aclService) {
        this.aclService = aclService;
    }

    public void addPermission(AbstractBusinessObject object, Sid recipient, Permission permission){
        ObjectIdentity oid = new ObjectIdentityImpl(object.getClass(), object.getId());
        addPermission(oid, recipient, permission);
    }

    public void addPermission(ObjectIdentity oid, Sid recipient, Permission permission){
        MutableAcl acl = saveOrUpdateACL(oid, null, false);

        // to make things easier, do not check if user has already permission
        // just add it multiply times, then when removing a group for instance
        // just remove one of the Access Control Entries, so that the user
        // still has permission (until all of his groups were removed).

//        try{
//            if(acl.isGranted(getPermissionAsList(permission), getSidAsList(recipient), false)) return;
//        } catch (NotFoundException ex){
//            // no ACE for given SID found
//        }

        acl.insertAce(acl.getEntries().size(), permission, recipient, true);
        aclService.updateAcl(acl);
    }

    private List<Permission> getPermissionAsList(Permission p){
        List<Permission> list = new ArrayList<Permission>();
        list.add(p);
        return list;
    }

    private List<Sid> getSidAsList(Sid sid){
        List<Sid> list = new ArrayList<Sid>();
        list.add(sid);
        return list;
    }

    public MutableAcl saveOrUpdateACL(ObjectIdentity objectIdentity, ObjectIdentity parentIdentity, boolean isEntriesInheriting){

        MutableAcl acl = null;

        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }

        if(parentIdentity != null){
            MutableAcl aclParent = (MutableAcl) aclService.readAclById(parentIdentity);
            acl.setParent(aclParent);
            acl.setEntriesInheriting(isEntriesInheriting);
        }

        aclService.updateAcl(acl);
        return acl;

    }

    public List<AccessControlEntry> getPermissions(AbstractBusinessObject object, Sid recipient){

        ObjectIdentity oid = new ObjectIdentityImpl(object.getClass(), object.getId());
        return getPermissions(oid, recipient);

    }

    public List<AccessControlEntry> getPermissions(ObjectIdentity oid, Sid recipient){

        List<AccessControlEntry> accessControlEntryList = new ArrayList<AccessControlEntry>();
        MutableAcl acl;

        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
        }

        List<AccessControlEntry> list = acl.getEntries();

        for (AccessControlEntry ace : list) {
            if (ace.getSid().equals(recipient)){
                accessControlEntryList.add(ace);
            }
        }

        return accessControlEntryList;

    }

    public void removePermissions(AbstractBusinessObject object, Sid recipient){

        ObjectIdentity oid = new ObjectIdentityImpl(object.getClass(), object.getId());
        MutableAcl acl;

        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            return; // ACL for object not found
        }

        List<AccessControlEntry> entries = acl.getEntries();

        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getSid().equals(recipient)) acl.deleteAce(i);
        }

        aclService.updateAcl(acl);

    }

    public void removePermissionEntry(ObjectIdentity oid, Sid recipient, Permission permission){

        MutableAcl acl;

        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            return; // ACL for object not found
        }

        List<AccessControlEntry> entries = acl.getEntries();

        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getSid().equals(recipient) && entries.get(i).getPermission().equals(permission)) {
                acl.deleteAce(i);
                break;
            }
        }

        aclService.updateAcl(acl);

    }


}
