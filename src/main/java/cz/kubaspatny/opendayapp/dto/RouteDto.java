package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.Group;
import cz.kubaspatny.opendayapp.bo.Route;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:08
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
public class RouteDto extends BaseDto {

    private String name;
    private String hexColor;
    private String information;
    private DateTime date;

    private EventDto event;
    private List<StationDto> stations;
    private List<UserDto> stationManagers;
    private List<GroupDto> groups;

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

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

    public List<StationDto> getStations() {
        return stations;
    }

    public void setStations(List<StationDto> stations) {
        this.stations = stations;
    }

    public List<UserDto> getStationManagers() {
        return stationManagers;
    }

    public void setStationManagers(List<UserDto> stationManagers) {
        this.stationManagers = stationManagers;
    }

    public List<GroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDto> groups) {
        this.groups = groups;
    }

    @Override
    public String getACLObjectIdentityClass() {
        return Route.class.getName();
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoreProperties names of variables to be ignored
     * @return
     */
    public static RouteDto map(Route source, RouteDto target, List<String> ignoreProperties){

        target.id = source.getId();
        target.name = source.getName();
        target.hexColor = source.getHexColor();
        target.date = source.getDate();
        target.information = source.getInformation();

        if(ignoreProperties == null) ignoreProperties = new ArrayList<String>();

        if(!ignoreProperties.contains("event")){
            target.event = EventDto.map(source.getEvent(), new EventDto(), DtoMapperUtil.getEventIgnoredProperties());
        }

        if(!ignoreProperties.contains("stations") && source.getStations() != null){

            List<StationDto> stationDtos = new ArrayList<StationDto>();
            List<String> stationIgnoredProperties = DtoMapperUtil.getStationIgnoredProperties();

            for(Station s : source.getStations()){
                stationDtos.add(StationDto.map(s, new StationDto(), stationIgnoredProperties));
            }

            target.stations = stationDtos;

        }

        if(!ignoreProperties.contains("stationManagers") && source.getStationManagers() != null){

            List<UserDto> userDtos = new ArrayList<UserDto>();
            List<String> userIgnoredProperties = DtoMapperUtil.getUserIgnoredProperties();

            for(User u : source.getStationManagers()){
                userDtos.add(UserDto.map(u, new UserDto(), userIgnoredProperties));
            }

            target.stationManagers = userDtos;

        }

        if(!ignoreProperties.contains("groups") && source.getGroups() != null){

            List<GroupDto> groupDtos = new ArrayList<GroupDto>();
            List<String> groupIgnoredProperties = DtoMapperUtil.getGroupIgnoredProperties();

            for(Group g : source.getGroups()){
                groupDtos.add(GroupDto.map(g, new GroupDto(), groupIgnoredProperties));
            }

            target.groups = groupDtos;

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
    public static Route map(RouteDto source, Route target, List<String> ignoredProperties){

        target.setName(source.getName());
        target.setHexColor(source.getHexColor());
        target.setInformation(source.getInformation());
        target.setDate(source.getDate());

        return target;

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteDto{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", hexColor='").append(hexColor).append('\'');
        sb.append(", information='").append(information).append('\'');
        sb.append(", date=").append(date);
        sb.append(", event=").append(event);
        sb.append(", stations=").append(stations);
        sb.append(", stationManagers=").append(stationManagers);
        sb.append(", groups=").append(groups);
        sb.append('}');
        return sb.toString();
    }

    public static Comparator<RouteDto> RouteDateComparator = new Comparator<RouteDto>() {
        public int compare(RouteDto route1, RouteDto route2) {
            return route1.getDate().compareTo(route2.getDate());
        }
    };

    public boolean isEditable(){
        return getDate().isAfterNow();
    }

    public boolean isToday(){
        return getDate().withTime(0,0,0,0).isEqual(DateTime.now().withTime(0,0,0,0));
    }

}
