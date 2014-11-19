package cz.kubaspatny.opendayapp.dto;

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
    private int timeLimit;
    private int relocationTime;
    private int sequencePosition;

    private RouteDto route;
    private GroupDto startingGroup;
    private List<LocationUpdateDto> locationUpdates;

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

    public GroupDto getStartingGroup() {
        return startingGroup;
    }

    public void setStartingGroup(GroupDto startingGroup) {
        this.startingGroup = startingGroup;
    }

    public List<LocationUpdateDto> getLocationUpdates() {
        return locationUpdates;
    }

    public void setLocationUpdates(List<LocationUpdateDto> locationUpdates) {
        this.locationUpdates = locationUpdates;
    }
}
