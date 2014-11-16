package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.User;

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
public class UserDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String organization;

    private Set<User.UserRole> userRoles;

    private List<EventDto> events;
    private List<RouteDto> managedRoutes;
    private List<GroupDto> groups;

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
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
}
