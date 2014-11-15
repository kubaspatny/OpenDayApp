package cz.kubaspatny.opendayapp.bo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 22:10
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
public class Station extends AbstractBusinessObject {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;
    private String information;

    /**
     * Time limit for station in seconds.
     */
    @Column(nullable = false)
    private int timeLimit;

    /**
     * Time in seconds needed to get to this station from the previous one.
     */
    @Column(nullable = false)
    private int relocationTime;

    /**
     * Number which is used for ordering stations on a route.
     */
    @Column(nullable = false)
    private int sequencePosition;

    @ManyToOne
    private Route route;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "startingPosition")
    private Group startingGroup;

    @OneToMany(mappedBy = "station")
    private List<LocationUpdate> locationUpdates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getRelocationTime() {
        return relocationTime;
    }

    public void setRelocationTime(int relocationTime) {
        this.relocationTime = relocationTime;
    }

    public int getSequencePosition() {
        return sequencePosition;
    }

    public void setSequencePosition(int sequencePosition) {
        this.sequencePosition = sequencePosition;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Group getStartingGroup() {
        return startingGroup;
    }

    public void setStartingGroup(Group startingGroup) {
        this.startingGroup = startingGroup;
    }

    public List<LocationUpdate> getLocationUpdates() {
        return locationUpdates;
    }

    public void setLocationUpdates(List<LocationUpdate> locationUpdates) {
        this.locationUpdates = locationUpdates;
    }

    public void addLocationUpdate(LocationUpdate locationUpdate){
        if(locationUpdates == null){
            locationUpdates = new ArrayList<LocationUpdate>();
        }

        locationUpdate.setStation(this);
        locationUpdates.add(locationUpdate);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Station{");
        sb.append("name='").append(name).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", information='").append(information).append('\'');
        sb.append(", timeLimit=").append(timeLimit);
        sb.append(", relocationTime=").append(relocationTime);
        sb.append(", sequencePosition=").append(sequencePosition);
        sb.append(", route=").append(route);
        sb.append('}');
        return sb.toString();
    }

    @PreRemove
    private void preRemove(){
        if(getRoute() != null){
            getRoute().removeStation(this);
            setRoute(null);
        }

        if(getStartingGroup() != null){
            getStartingGroup().setStartingPosition(null);
        }

        if(getLocationUpdates() != null){
            for(Iterator<LocationUpdate> it = getLocationUpdates().iterator(); it.hasNext();){
                LocationUpdate l = it.next();
                l.setStation(null);
                it.remove();
            }
        }
    }

}
