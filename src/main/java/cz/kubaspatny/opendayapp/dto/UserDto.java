package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Group;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 20:17
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
public class UserDto extends BaseDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String organization;
    private String password;

    private Set<User.UserRole> userRoles;

    private List<EventDto> events;
    private List<RouteDto> managedRoutes;
    private List<GroupDto> groups;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Set<User.UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<User.UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public void addUserRole(User.UserRole userRole){
        if(userRoles == null){
            userRoles = new HashSet<User.UserRole>();
        }

        userRoles.add(userRole);
    }

    public void removeUserRole(User.UserRole userRole){
        if(userRoles != null){
            userRoles.remove(userRole);
        }
    }

    public List<EventDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventDto> events) {
        this.events = events;
    }

    public List<RouteDto> getManagedRoutes() {
        return managedRoutes;
    }

    public void setManagedRoutes(List<RouteDto> managedRoutes) {
        this.managedRoutes = managedRoutes;
    }

    public List<GroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDto> groups) {
        this.groups = groups;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String getPassword() {
        return password;
    }

    public boolean isPasswordSet(){
        return this.password != null;
    }

    @Override
    public String getACLObjectIdentityClass() {
        return User.class.getName();
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static UserDto map(User source, UserDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.username = source.getUsername();
        target.email = source.getEmail();
        target.firstName = source.getFirstName();
        target.lastName = source.getLastName();
        target.organization = source.getOrganization();

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("events") && source.getEvents() != null){

            List<EventDto> eventDtos = new ArrayList<EventDto>();
            List<String> eventIgnorable = DtoMapperUtil.getEventIgnoredProperties();

            for(Event e : source.getEvents()){
                eventDtos.add(EventDto.map(e, new EventDto(), eventIgnorable));
            }

            target.setEvents(eventDtos);

        }

        if(!ignoredProperties.contains("managedRoutes") && source.getManagedRoutes() != null) {

            List<RouteDto> routeDtos = new ArrayList<RouteDto>();
            List<String> routeIgnoredProperties = DtoMapperUtil.getRouteIgnoredProperties();

            for(Route r : source.getManagedRoutes()){
                routeDtos.add(RouteDto.map(r, new RouteDto(), routeIgnoredProperties));
            }

            target.managedRoutes = routeDtos;

        }

        if(!ignoredProperties.contains("groups") && source.getGroups() != null) {

            List<GroupDto> groupDtos = new ArrayList<GroupDto>();
            List<String> groupIgnoredProperties = DtoMapperUtil.getGroupIgnoredProperties();

            for(Group g : source.getGroups()){
                groupDtos.add(GroupDto.map(g, new GroupDto(), groupIgnoredProperties));
            }

            target.groups = groupDtos;

        }

        if(!ignoredProperties.contains("userRoles") && source.getUserRoles() != null){

            for(User.UserRole userRole : source.getUserRoles()){
                target.addUserRole(userRole);
            }

        }

        return target;

    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static User map(UserDto source, User target, List<String> ignoredProperties){

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("id")){
            target.setId(source.getId());
        }

        if(!ignoredProperties.contains("username")){
            target.setUsername(source.getUsername());
        }

        if(!ignoredProperties.contains("email")){
            target.setEmail(source.getEmail().toLowerCase());
        }

        if(!ignoredProperties.contains("password")){
            target.setPassword(source.getPassword());
        }

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setOrganization(source.getOrganization());
        target.setUserRoles(source.getUserRoles());

        return target;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDto{");
        sb.append("id='").append(id).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", organization='").append(organization).append('\'');
        sb.append(", userRoles=").append(userRoles);
        sb.append(", events=").append(events);
        sb.append(", managedRoutes=").append(managedRoutes);
        sb.append(", groups=").append(groups);
        sb.append('}');
        return sb.toString();
    }
}
