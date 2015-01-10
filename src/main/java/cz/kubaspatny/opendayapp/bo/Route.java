package cz.kubaspatny.opendayapp.bo;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 19:06
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
public class Route extends AbstractBusinessObject {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String hexColor;

    private String information;

    @Column(nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime date;

    @ManyToOne
    private Event event;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "route")
    private List<Station> stations;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<User> stationManagers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "route")
    private List<Group> groups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void addStation(Station station){
        if(stations == null){
            stations = new ArrayList<Station>();
        }

        station.setRoute(this);
        stations.add(station);
    }

    public void removeStation(Station station){
        if(stations == null || !stations.contains(station)) throw new RuntimeException("User collection doesn't contain route " + station.getId());
        stations.remove(station);
    }

    public List<User> getStationManagers() {
        return stationManagers;
    }

    public void setStationManagers(List<User> stationManagers) {
        this.stationManagers = stationManagers;
    }

    public void addStationManager(User stationManager){
        if(stationManagers == null){
            stationManagers = new ArrayList<User>();
        }

        stationManagers.add(stationManager);
        stationManager.addManagedRoute(this);
    }

    public void removeStationManager(User stationManager){
        if(stationManagers == null || stationManagers.contains(stationManager)) return;

        stationManagers.remove(stationManager);
        stationManager.removeManagedRoute(this);
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

        group.setRoute(this);
        groups.add(group);
    }

    public void removeGroup(Group group){
        if(groups == null || !groups.contains(group)) throw new RuntimeException("Route doesn't have group with ID: " + group.getId());
        groups.remove(group);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Route{");
        sb.append("name='").append(name).append('\'');
        sb.append(", hexColor='").append(hexColor).append('\'');
        sb.append(", information='").append(information).append('\'');
        sb.append(", date=").append(date);
        sb.append(", event=").append(event.getName());
        sb.append('}');
        return sb.toString();
    }


    /**
     * Before deleting this entity, the relationships has to be removed, otherwise the Event, Station, Group and User objects
     * might still hold a reference to this entity (until the moment the Event/Station/Group/User entity was retrieved
     * again, but in case he would try to persist this entity before retrieving it again it would
     * cause an Exception).
     */
    @PreRemove
    private void preRemove(){

        // remove Route from Event
        if(getEvent() != null){
            getEvent().removeRoute(this);
            setEvent(null);
        }

        // remove Route from users' managedRoutes
        if(stationManagers != null){
            for(User u : stationManagers){
                u.removeManagedRoute(this);
            }
            stationManagers.clear();
        }

        // set Stations' ids to null
        if(stations != null){
            for(Station s : stations){
                s.setRoute(null);
            }
        }

        if(groups != null){
            for(Group g : groups){
                g.setRoute(null);
            }
        }


    }
}
