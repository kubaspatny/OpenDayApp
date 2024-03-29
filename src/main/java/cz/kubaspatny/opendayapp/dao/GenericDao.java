package cz.kubaspatny.opendayapp.dao;

import cz.kubaspatny.opendayapp.bo.AbstractBusinessObject;
import cz.kubaspatny.opendayapp.exception.DataAccessException;

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
public interface GenericDao {

    /**
     * Saves or updates object to the database. If o has assigned ID, then it's merged, otherwise it is persisted.
     * @param o   Entity to be saved
     * @return    Saved Entity
     */
    public <ENTITY extends AbstractBusinessObject> ENTITY saveOrUpdate(ENTITY o) throws DataAccessException;

    /**
     * Removes an existing Entity.
     * @param o  Entity to be removed
     */
    public <ENTITY extends AbstractBusinessObject> void remove(ENTITY o) throws DataAccessException;

    /**
     * Removes an entity based on its ID.
     * @param id    ID
     * @param entity_class  Class object of the entity to be removed
     */
    public <ENTITY extends AbstractBusinessObject> void removeById(Long id, Class<ENTITY> entity_class) throws DataAccessException;

    /**
     * Gets entity from database based on its ID.
     * @param id    ID
     * @param entity_class  Class object of the entity to be returned
     * @return  Entity from database
     */
    public <ENTITY extends AbstractBusinessObject> ENTITY getById(Long id, Class<ENTITY> entity_class) throws DataAccessException;


    /**
     * Returns a single entity based on the property specified in parameter with the specified value.
     * @param property  property for search
     * @param value value of property for search
     * @return Entity from database
     * @throws cz.kubaspatny.opendayapp.exception.DataAccessException
     */
    public <ENTITY extends AbstractBusinessObject> ENTITY getByPropertyUnique(String property, Object value, Class<ENTITY> entity_class) throws DataAccessException;

    /**
     * Gets all entities of type @entity_class.
     * @param entity_class  Class object of entities to be returned
     * @return  List of entities
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getAll(Class<ENTITY> entity_class);

    /**
     * Method supporting pagination. Returns a page of entities.
     * @param page    Page offset, where 0 is first page
     * @param pageSize  Page size
     * @return  List of entities
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Class<ENTITY> entity_class) throws DataAccessException;

    /**
     * Method supporting pagination. Returns a sorted page of entities.
     * @param page    Page offset, where 0 is first page
     * @param pageSize  Page size
     * @return  List of entities
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, String sortBy, boolean ascending, Class<ENTITY> entity_class) throws DataAccessException;

    /**
     * Method supporting pagination. Returns a sorted page of entities filtered by @parameters.
     * @param page    Page offset, where 0 is first page
     * @param pageSize  Page size
     * @param parameters    Key-Value pairs used for filtering, such as "where key == value"
     * @param sortBy    Column name used for sort ordering
     * @param ascending If true, order will be ascending. If false, order will be descending.
     * @param entity_class
     * @param <ENTITY>
     * @return
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> getPage(int page, int pageSize, Map<String, Object> parameters, String sortBy, boolean ascending, Class<ENTITY> entity_class) throws DataAccessException;

    /**
     * Returns a list of filtered entities based on @properties.
     *
     * @param parameters@return  List of filtered entities.
     */
    public <ENTITY extends AbstractBusinessObject> List<ENTITY> searchByProperty(Map<String, Object> parameters, Class<ENTITY> entity_class) throws DataAccessException;

    /**
     * Returns the number of entities meeting the filtering criteria.
     * @param property filtering property
     * @return number of entities meeting the filtering criteria
     * @throws DataAccessException
     */
    public <ENTITY extends AbstractBusinessObject> Long countByProperty(String property, Object value, Class<ENTITY> entity_class) throws DataAccessException;



}
