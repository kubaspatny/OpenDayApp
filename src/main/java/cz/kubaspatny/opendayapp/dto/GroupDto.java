package cz.kubaspatny.opendayapp.dto;

import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:11
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
public class GroupDto {

    private RouteDto route;
    private UserDto guide;
    private StationDto startingPosition;

    private List<GroupSizeDto> groupSizes;

    private List<LocationUpdateDto> locationUpdates;

    public RouteDto getRoute() {
        return route;
    }

    public void setRoute(RouteDto route) {
        this.route = route;
    }

    public UserDto getGuide() {
        return guide;
    }

    public void setGuide(UserDto guide) {
        this.guide = guide;
    }

    public StationDto getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(StationDto startingPosition) {
        this.startingPosition = startingPosition;
    }

    public List<GroupSizeDto> getGroupSizes() {
        return groupSizes;
    }

    public void setGroupSizes(List<GroupSizeDto> groupSizes) {
        this.groupSizes = groupSizes;
    }

    public List<LocationUpdateDto> getLocationUpdates() {
        return locationUpdates;
    }

    public void setLocationUpdates(List<LocationUpdateDto> locationUpdates) {
        this.locationUpdates = locationUpdates;
    }
}
