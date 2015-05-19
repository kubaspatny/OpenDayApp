package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.dao.ConcreteDao;
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
 *
 * Custom Data Access Service for Spring Security framework.
 */
public class DataAccessService {

    @Autowired
    protected GenericDao dao;

    @Autowired
    protected ConcreteDao concreteDao;

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

    public ConcreteDao getConcreteDao() {
        return concreteDao;
    }

    public void setConcreteDao(ConcreteDao concreteDao) {
        this.concreteDao = concreteDao;
    }

    public JdbcMutableAclService getAclService() {
        return aclService;
    }

    public void setAclService(JdbcMutableAclService aclService) {
        this.aclService = aclService;
    }

    /**
     * Adds permission to recipient to a certain object.
     * @param object        Object for which recipient gains permission
     * @param recipient     User who gains permission
     * @param permission    Type of Permission (e.g. READ, WRITE)
     */
    public void addPermission(AbstractBusinessObject object, Sid recipient, Permission permission){
        ObjectIdentity oid = new ObjectIdentityImpl(object.getClass(), object.getId());
        addPermission(oid, recipient, permission);
    }

    /**
     * Adds permission to recipient to a certain object.
     * @param oid           ObjectIdenty of object for which recipient gains permission
     * @param recipient     User who gains permission
     * @param permission    Type of Permission (e.g. READ, WRITE)
     */
    public void addPermission(ObjectIdentity oid, Sid recipient, Permission permission){
        MutableAcl acl = saveOrUpdateACL(oid, null, false);

        acl.insertAce(acl.getEntries().size(), permission, recipient, true);
        aclService.updateAcl(acl);
    }

    /**
     * Returns a single Permission object in a List.
     */
    private List<Permission> getPermissionAsList(Permission p){
        List<Permission> list = new ArrayList<Permission>();
        list.add(p);
        return list;
    }

    /**
     * Returns a single Sid object in a List.
     */
    private List<Sid> getSidAsList(Sid sid){
        List<Sid> list = new ArrayList<Sid>();
        list.add(sid);
        return list;
    }

    /**
     * Method creates or updates Access Control List (ACL) for specified object by its ObjectIdentity.
     * @param objectIdentity    object to create ACL for
     * @param parentIdentity    object whose ACL is hierarchical parent of objectIdentity's ACL
     * @param isEntriesInheriting   if true, parent ACL entries are inheriting to created ACL
     * @return
     */
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

    /**
     * Returns a list of recipient's Permissions to object.
     */
    public List<AccessControlEntry> getPermissions(AbstractBusinessObject object, Sid recipient){

        ObjectIdentity oid = new ObjectIdentityImpl(object.getClass(), object.getId());
        return getPermissions(oid, recipient);

    }

    /**
     * Returns a list of recipient's Permissions to object specified by oid.
     */
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

    /**
     * Removes all recipient's permissions to object.
     */
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

    /**
     * Removes a single recipient's Permission Entry from object's ACL.
     */
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
