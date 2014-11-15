package cz.kubaspatny.opendayapp.bo;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 16:52
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
public class Event extends AbstractBusinessObject {

    @Column(nullable = false)
    private String name;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime date;

    private String information;

    @ManyToOne
    private User organizer;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
    private List<Route> routes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route){
        if(routes == null){
            routes = new ArrayList<Route>();
        }

        route.setEvent(this);
        routes.add(route);
    }

    public void removeRoute(Route route){
        if(routes == null || !routes.contains(route)) throw new RuntimeException("User collection doesn't contain route " + route.getId());
        routes.remove(route);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("name='").append(name).append('\'');
        sb.append(", date=").append(date);
        sb.append(", information='").append(information).append('\'');
        sb.append(", organizer=").append(organizer.getUsername());
        sb.append('}');
        return sb.toString();
    }

    /**
     * Before deleting this entity, the relationship has to be removed, otherwise the User object
     * might still see this entity in his list of Events (until the moment his entity was retrieved
     * again, but in case he would try to persist this entity before retrieving it again it would
     * cause an Exception).
     */
    @PreRemove
    private void preRemove(){
        getOrganizer().removeEvent(this);
        setOrganizer(null);
    }

}
