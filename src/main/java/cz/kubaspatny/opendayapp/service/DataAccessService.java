package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.util.Assert;

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

}
