package cz.kubaspatny.opendayapp.bo;

import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Comparator;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 15/11/2014
 * Time: 22:31
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
public class LocationUpdate extends AbstractBusinessObject {

    public enum Type {
        CHECKIN, CHECKOUT, SKIP;
    }

    @Column(nullable = false, updatable = false)
    @org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime timestamp;

    @ManyToOne
    private Group group;

    @Column(nullable = false, updatable = false)
    private Type type;

    @ManyToOne
    private Station station;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocationUpdate{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", group=").append(group);
        sb.append(", type=").append(type);
        sb.append(", station=").append(station);
        sb.append('}');
        return sb.toString();
    }

    public static Comparator<LocationUpdate> LocationUpdateComparator = new Comparator<LocationUpdate>() {
        public int compare(LocationUpdate locationUpdate1, LocationUpdate locationUpdate2) {
            return locationUpdate1.getTimestamp().compareTo(locationUpdate2.getTimestamp());
        }
    };

}
