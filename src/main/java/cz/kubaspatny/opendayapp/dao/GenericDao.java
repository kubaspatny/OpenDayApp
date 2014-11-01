package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.exception.DaoException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 26/10/2014
 * Time: 18:36
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
public interface GenericDao {   // TODO: Add exceptions!!!

    /**
     * Saves or updates object to the database. If o has assigned ID, then it's merged, otherwise it is persisted.
     * @param o   Entity to be saved
     * @return    Saved Entity
     */
    public <ENTITY extends AbstractBusinessObject> ENTITY saveOrUpdate(ENTITY o) throws DaoException;

    /**
     * Removes an existing Entity.
     * @param o  Entity to be removed
     */
    public <ENTITY extends AbstractBusinessObject> void remove(ENTITY o) throws DaoException;

    /**
     * Removes an entity based on its ID.
     * @param id    ID
     * @param entity_class  Class object of the entity to be removed
     */
    public <ENTITY extends AbstractBusinessObject> void removeById(Long id, Class<ENTITY> entity_class) throws DaoException;

    /**
     * Gets entity from database based on its ID.
     * @param id    ID
     * @param entity_class  Class object of the entity to be returned
     * @return  Entity from database
     */
    public <ENTITY extends AbstractBusinessObject> ENTITY getById(Long id, Class<ENTITY> entity_class) throws DaoException;

    /**
     * Gets all entities of type @entity_class.
     * @param entity_class  Class object of entities to be returned
     * @return  List of entities
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getAll(Class<ENTITY> entity_class);

    /**
     * Method supporting pagination. Returns a page of entities.
     * @param page    Page offset
     * @param pageSize  Page size
     * @return  List of entities
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Class<ENTITY> entity_class);

    /**
     * Method supporting pagination. Returns a sorted page of entities.
     * @param page    Page offset
     * @param pageSize  Page size
     * @return  List of entities
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, String sortBy, boolean ascending, Class<ENTITY> entity_class);

    /**
     * Method supporting pagination. Returns a sorted page of entities filtered by @parameters.
     * @param page    Page offset
     * @param pageSize  Page size
     * @param parameters    Key-Value pairs used for filtering, such as "where key == value"
     * @param sortBy    Column name used for sort ordering
     * @param ascending If true, order will be ascending. If false, order will be descending.
     * @param entity_class
     * @param <ENTITY>
     * @return
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Map<String, Object> parameters, String sortBy, boolean ascending, Class<ENTITY> entity_class);

    /**
     * Returns a list of filtered entities based on @properties.
     * @param properties    Key-Value pairs of property names and values.
     * @return  List of filtered entities.
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> searchByProperty(Map<String, Object> properties, Class<ENTITY> entity_class);

}
