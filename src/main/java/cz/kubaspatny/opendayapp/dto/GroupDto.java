package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.Group;
import cz.kubaspatny.opendayapp.bo.GroupSize;
import cz.kubaspatny.opendayapp.bo.LocationUpdate;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;

import java.util.ArrayList;
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
public class GroupDto extends BaseDto {

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

    // OBJECT MAPPERS

    public static GroupDto map(Group source, GroupDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.route = RouteDto.map(source.getRoute(), new RouteDto(), DtoMapperUtil.getRouteIgnoredProperties());
        target.guide = UserDto.map(source.getGuide(), new UserDto(), DtoMapperUtil.getUserIgnoredProperties());
        target.startingPosition = StationDto.map(source.getStartingPosition(), new StationDto(), DtoMapperUtil.getStationIgnoredProperties());

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("groupSizes") && source.getGroupSizes() != null){

            List<GroupSizeDto> groupSizeDtos = new ArrayList<GroupSizeDto>();
            for(GroupSize g : source.getGroupSizes()){
                groupSizeDtos.add(GroupSizeDto.map(g, new GroupSizeDto(), null));
            }

            target.groupSizes = groupSizeDtos;

        } else if(!ignoredProperties.contains("locationUpdates") && source.getLocationUpdates() != null){

            List<LocationUpdateDto> locationUpdateDtos = new ArrayList<LocationUpdateDto>();
            for(LocationUpdate l : source.getLocationUpdates()){
                locationUpdateDtos.add(LocationUpdateDto.map(l, new LocationUpdateDto(), null));
            }

            target.locationUpdates = locationUpdateDtos;

        }

        return target;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GroupDto{");
        sb.append("id=").append(id);
        sb.append(", route=").append(route);
        sb.append(", guide=").append(guide);
        sb.append(", startingPosition=").append(startingPosition);
        sb.append(", groupSizes=").append(groupSizes);
        sb.append(", locationUpdates=").append(locationUpdates);
        sb.append('}');
        return sb.toString();
    }
}
