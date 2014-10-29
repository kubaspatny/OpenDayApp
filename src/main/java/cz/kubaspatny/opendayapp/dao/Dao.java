package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 26/10/2014
 * Time: 18:37
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
@Component("genericDao")
public class Dao implements GenericDao {


    @Override
    public <ENTITY extends AbstractBusinessObject> ENTITY saveOrUpdate(ENTITY o) {
        return null;
    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void remove(ENTITY o) {

    }

    @Override
    public <ENTITY extends AbstractBusinessObject> void removeById(long id, Class<ENTITY> entity_class) {

    }

    @Override
    public <ENTITY> ENTITY getById(Long id, Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY> List<ENTITY> getAll(Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY> List<ENTITY> getPage(int offset, int pageSize, Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY> List<ENTITY> getPage(int offset, int pageSize, String sortBy, boolean ascending, Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY> List<ENTITY> getPage(int offset, int pageSize, Map<String, Object> parameters, String sortBy, boolean ascending, Class<ENTITY> entity_class) {
        return null;
    }

    @Override
    public <ENTITY> List<ENTITY> searchByProperty(Map<String, Object> properties, Class<ENTITY> entity_class) {
        return null;
    }
}