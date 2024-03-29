package cz.kubaspatny.opendayapp.bo;

import cz.kubaspatny.opendayapp.provider.HashProvider;
import cz.kubaspatny.opendayapp.provider.SaltProvider;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 15:36
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

@Entity
@Table(name = "users")
@Configurable(preConstruction = true)
public class User extends AbstractBusinessObject {

    public enum UserRole {
        ROLE_ORGANIZER, ROLE_STATIONMANAGER, ROLE_GUIDE, ROLE_USER;
    }

    @Autowired private transient SaltProvider saltProvider;
    @Autowired private transient HashProvider hashProvider;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * This variable represents SHA-256 hashed and salted password.
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    private String firstName;
    private String lastName;
    private String organization;

    @Column(nullable = false)
    private boolean userEnabled = true;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "organizer")
    private List<Event> events;

    @ManyToMany(mappedBy = "stationManagers")
    private List<Route> managedRoutes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "guide")
    private List<Group> groups;

    @ElementCollection
    private Set<UserRole> userRoles;

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

    public String getPassword() {
        return password;
    }

    /**
     * When a user sets a password, a new securely random salt is generated and hashed.
     * The provided is password is prepended with the salt and hashed using SHA-256 algorithm.
     * This hash is then saved as a user's password.
     * @param password
     */
    public void setPassword(String password) {
        setSalt(hashProvider.hash(saltProvider.generateSalt(32)));
        this.password = hashProvider.hash(password, salt);
    }

    public boolean isLoginCorrect(String password){
        return this.password.equals(hashProvider.hash(password, salt));
    }

    public String getSalt() {
        return salt;
    }

    private void setSalt(String salt) {
        this.salt = salt;
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

    public boolean isUserEnabled() {
        return userEnabled;
    }

    public void setUserEnabled(boolean userEnabled) {
        this.userEnabled = userEnabled;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event){
        if(events == null){
            events = new ArrayList<Event>();
        }

        event.setOrganizer(this);
        this.events.add(event);
    }

    public void removeEvent(Event event){
        if(events == null || !events.contains(event)) throw new RuntimeException("User collection doesn't contain event " + event.getId());
        events.remove(event);
    }

    public List<Route> getManagedRoutes() {
        return managedRoutes;
    }

    public void setManagedRoutes(List<Route> managedRoutes) {
        this.managedRoutes = managedRoutes;
    }

    public void addManagedRoute(Route route){
        if(managedRoutes == null){
            managedRoutes = new ArrayList<Route>();
        }

        managedRoutes.add(route);
    }

    public void removeManagedRoute(Route route){
        if(managedRoutes == null || !managedRoutes.contains(route)) throw new RuntimeException("User managed routes collection doesn't contain route " + route.getId());
        managedRoutes.remove(route);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group){
        if(groups == null){
            groups = new ArrayList<Group>();
        }
        if(!groups.contains(group)) groups.add(group);
    }

    public void removeGroup(Group group){
        if(groups == null || !groups.contains(group)) throw new RuntimeException("User doesn't guide group with id: " + group.getId());
        groups.remove(group);
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public void addUserRole(UserRole userRole){
        if(userRoles == null){
            userRoles = new HashSet<UserRole>();
        }

        userRoles.add(userRole);
    }

    public void removeUserRole(UserRole userRole){
        if(userRoles != null){
            userRoles.remove(userRole);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", salt='").append(salt).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", organization='").append(organization).append('\'');
        sb.append(", userEnabled=").append(userEnabled);
        sb.append('}');
        return sb.toString();
    }

    public void print(){
        System.out.println(toString());
        System.out.println("ORGANIZING EVENTS:");
        if(events != null){
            for(Event e : events){
                System.out.println("\t" + e);
                if(e.getRoutes() != null){
                    for(Route r : e.getRoutes()){
                        System.out.println("\t\t" + r);

                        System.out.println("\t\t\t STATIONS:");
                        if(r.getStations() != null){
                            for(Station s : r.getStations()){
                                System.out.println("\t\t\t\t" + s);
                            }
                        }

                        System.out.println("\t\t\t STATION MANAGERS:");
                        if(r.getStationManagers() != null){
                            for(User stationManager : r.getStationManagers()){
                                System.out.println("\t\t\t\t" + stationManager);
                            }
                        }

                        System.out.println("\t\t\t GROUPS:");
                        if(r.getGroups() != null){
                            for(Group group : r.getGroups()){
                                System.out.println("\t\t\t\t" + group);

                                System.out.println("\t\t\t\t\tLOCATION UPDATES:");
                                if(group.getGroupSizes() != null){
                                    for(GroupSize groupSize : group.getGroupSizes()){
                                        System.out.println("\t\t\t\t\t" + groupSize);
                                    }
                                }

                                System.out.println("\t\t\t\t\tLOCATION UPDATES:");
                                if(group.getLocationUpdates() != null){
                                    for(LocationUpdate locationUpdate : group.getLocationUpdates()){
                                        System.out.println("\t\t\t\t\t" + locationUpdate);
                                    }
                                }

                            }
                        }


                    }
                }
            }
        }

        System.out.println("MANAGER AT ROUTES:");
        if(managedRoutes != null){
            for(Route r : managedRoutes){
                System.out.println("\t" + r);
            }
        }

        System.out.println("GUIDING GROUPS:");
        if(groups != null){
            for(Group g : groups){
                System.out.println("\t" + g);
                System.out.println("\t\tGROUP SIZES:");
                if(g.getGroupSizes() != null){
                    for(GroupSize groupSize : g.getGroupSizes()){
                        System.out.println("\t\t" + groupSize);
                    }
                }

                System.out.println("\t\tLOCATION UPDATES:");
                if(g.getLocationUpdates() != null){
                    for(LocationUpdate locationUpdate : g.getLocationUpdates()){
                        System.out.println("\t\t" + locationUpdate);
                    }
                }
            }
        }

        System.out.println("USER ROLES:");
        if(userRoles != null){
            for(UserRole userRole : userRoles){
                System.out.println("\t" + userRole);
            }
        }

        System.out.println();
    }

    @PreRemove
    private void preRemove(){
        throw new RuntimeException("Users cannot be deleted from the database! Use User#setEnabled(false) instead!");
    }

    /**
     * Builder class for user class.
     */
    public static class Builder {

        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String organization;
        private Set<UserRole> userRoles;

        public Builder(String username, String email, String password) {
            if(username == null || email == null || password == null) throw new IllegalArgumentException("Parameters cannot be null!");

            this.username = username;
            this.email = email;
            this.password = password;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        public Builder addUserRole(UserRole userRole){
            if(userRoles == null){
                userRoles = new HashSet<UserRole>();
            }

            userRoles.add(userRole);
            return this;
        }

        public User build(){
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(password);
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setOrganization(organization);
            u.setUserRoles(userRoles);
            return u;
        }
    }

}
