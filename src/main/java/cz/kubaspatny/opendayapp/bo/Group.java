package cz.kubaspatny.opendayapp.bo;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 15/11/2014
 * Time: 21:02
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

@Entity(name = "StationGroups")
public class Group extends AbstractBusinessObject {

    @ManyToOne
    private Route route;

    @ManyToOne
    private User guide;

    private Integer startingPosition;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private List<GroupSize> groupSizes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private List<LocationUpdate> locationUpdates;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdated;
    private boolean inactivityNotified = true;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public User getGuide() {
        return guide;
    }

    public void setGuide(User guide) {
        if(this.guide != null) this.guide.removeGroup(this);
        this.guide = guide;
        this.guide.addGroup(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Group{");
        sb.append("route=").append(route);
        sb.append(", guide=").append(guide);
        sb.append(", startingPosition=").append(startingPosition);
        sb.append(", active=").append(isActive());
        sb.append(", notified=").append(isInactivityNotified());
        sb.append('}');
        return sb.toString();
    }

    public Integer getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Integer startingPosition) {
        this.startingPosition = startingPosition;
    }

    public List<GroupSize> getGroupSizes() {
        return groupSizes;
    }

    public void setGroupSizes(List<GroupSize> groupSizes) {
        this.groupSizes = groupSizes;
    }

    public void addGroupSize(GroupSize groupSize){
        if(groupSizes == null){
            groupSizes = new ArrayList<GroupSize>();
        }

        groupSize.setGroup(this);
        groupSizes.add(groupSize);
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

        locationUpdate.setGroup(this);
        locationUpdates.add(locationUpdate);
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        Duration d = new Duration(getLastUpdated(), DateTime.now());
        return d.getStandardMinutes() < 10;
    }

    public boolean isInactivityNotified() {
        return inactivityNotified;
    }

    public void setInactivityNotified(boolean inactivityNotified) {
        this.inactivityNotified = inactivityNotified;
    }

    @PreRemove
    private void preRemove(){
        if(route != null){
            route.removeGroup(this);
            setRoute(null);
        }

        if(guide != null){
            guide.removeGroup(this);
            guide = null;
        }
    }
}
