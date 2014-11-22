package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.Event;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

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
    private String information;
    private UserDto organizer;

    private List<RouteDto> routes;

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

            target.routes = routeDtos;

        }

        return target;
    }

    /**
     * Doesn't map attribute "organizer" -> that's service's job.
     * @param source
     * @param target
     * @param ignoredProperties
     * @return
     */
    public static Event map(EventDto source, Event target, List<String> ignoredProperties){

        target.setName(source.getName());
        target.setDate(source.getDate());
        target.setInformation(source.getInformation());

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
}
