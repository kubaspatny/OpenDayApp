package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.LocationUpdate;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:09
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
public class StationDto extends BaseDto {

    private String name;
    private String location;
    private String information;
    private boolean closed;
    private int timeLimit;
    private int relocationTime;
    private int sequencePosition;

    /**
     * ID used for identifying group's starting position on the createRoute Screen.
     */
    private long creationId;

    private RouteDto route;
    private List<LocationUpdateDto> locationUpdates;

    public StationDto() {
    }

    public StationDto(boolean generateCreationId) {
        creationId = System.nanoTime();
    }


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

    public RouteDto getRoute() {
        return route;
    }

    public void setRoute(RouteDto route) {
        this.route = route;
    }

    public List<LocationUpdateDto> getLocationUpdates() {
        return locationUpdates;
    }

    public void setLocationUpdates(List<LocationUpdateDto> locationUpdates) {
        this.locationUpdates = locationUpdates;
    }

    public long getCreationId() {
        return creationId;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String getACLObjectIdentityClass() {
        return Station.class.getName();
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static StationDto map(Station source, StationDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.name = source.getName();
        target.location = source.getLocation();
        target.closed = source.isClosed();
        target.information = source.getInformation();
        target.timeLimit = source.getTimeLimit();
        target.relocationTime = source.getRelocationTime();
        target.sequencePosition = source.getSequencePosition();

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("route")){
            target.route = RouteDto.map(source.getRoute(), new RouteDto(), DtoMapperUtil.getRouteIgnoredProperties());
        }

        if(!ignoredProperties.contains("locationUpdates") && source.getLocationUpdates() != null){
            List<LocationUpdateDto> locationUpdateDtos = new ArrayList<LocationUpdateDto>();
            for(LocationUpdate l : source.getLocationUpdates()){
                locationUpdateDtos.add(LocationUpdateDto.map(l, new LocationUpdateDto(), null));
            }

            target.locationUpdates = locationUpdateDtos;
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
    public static Station map(StationDto source, Station target, List<String> ignoredProperties){

        target.setName(source.getName());
        target.setLocation(source.getLocation());
        target.setInformation(source.getInformation());
        target.setClosed(source.isClosed());
        target.setTimeLimit(source.getTimeLimit());
        target.setRelocationTime(source.getRelocationTime());
        target.setSequencePosition(source.getSequencePosition());

        return target;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StationDto{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", information='").append(information).append('\'');
        sb.append(", isClosed='").append(closed).append('\'');
        sb.append(", timeLimit=").append(timeLimit);
        sb.append(", relocationTime=").append(relocationTime);
        sb.append(", sequencePosition=").append(sequencePosition);
        sb.append(", route=").append(route);
        sb.append(", locationUpdates=").append(locationUpdates);
        sb.append('}');
        return sb.toString();
    }

    public static Comparator<StationDto> StationSequencePosistionComparator = new Comparator<StationDto>() {
        public int compare(StationDto station1, StationDto station2) {
            return ((Integer)station1.getSequencePosition()).compareTo(station2.getSequencePosition());
        }
    };

}
