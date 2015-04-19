package cz.kubaspatny.opendayapp.dto;

import com.google.gson.annotations.Expose;
import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:04
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
public class EventDto extends BaseDto {

    private String name;
    private DateTime date;
    @Expose(serialize = false) private String information;
    @Expose(serialize = false) private UserDto organizer;

    private List<RouteDto> routes;
    private Set<String> emailList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getDate() {
        return date;
    }

    public String getEventDate(){
        return getDate().toString("dd.MM.yyyy");
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

    public UserDto getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserDto organizer) {
        this.organizer = organizer;
    }

    public List<RouteDto> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDto> routes) {
        this.routes = routes;
    }

    public Set<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(Set<String> emailList) {
        this.emailList = emailList;
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static EventDto map(Event source, EventDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.name = source.getName();
        target.date = source.getDate();
        target.information = source.getInformation();

        target.organizer = UserDto.map(source.getOrganizer(), new UserDto(), DtoMapperUtil.getUserIgnoredProperties());

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("routes") && source.getRoutes() != null){

            List<RouteDto> routeDtos = new ArrayList<RouteDto>();
            List<String> routeIgnoredProperties = DtoMapperUtil.getRouteIgnoredProperties();
            for(Route r : source.getRoutes()){
                routeDtos.add(RouteDto.map(r, new RouteDto(), routeIgnoredProperties));
            }

            Collections.sort(routeDtos, RouteDto.RouteDateComparator);
            target.routes = routeDtos;

        }

        if(!ignoredProperties.contains("emailList") && source.getEmailList() != null){

            Set<String> emails = new HashSet<String>();
            emails.addAll(source.getEmailList());
            target.setEmailList(emails);

        }

        return target;
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null. Doesn't map attribute "organizer" -> that's service's job.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static Event map(EventDto source, Event target, List<String> ignoredProperties){

        target.setName(source.getName());
        target.setDate(source.getDate());
        target.setInformation(source.getInformation());

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("emailList") && source.getEmailList() != null) {
            target.setEmailList(source.getEmailList());
        }

        return target;

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventDto{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", date=").append(date);
        sb.append(", information='").append(information).append('\'');
        sb.append(", organizer=").append(organizer);
        sb.append(", routes=").append(routes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getACLObjectIdentityClass() {
        return Event.class.getName();
    }

    public static Comparator<EventDto> EventDateComparator = new Comparator<EventDto>() {
        public int compare(EventDto event1, EventDto event2) {
            return event1.getDate().compareTo(event2.getDate());
        }
    };

    public boolean isEditable(){
        return getDate().isAfterNow();
    }

    public boolean isToday(){
        return getDate().withTime(0,0,0,0).isEqual(DateTime.now().withTime(0,0,0,0));
    }

}
