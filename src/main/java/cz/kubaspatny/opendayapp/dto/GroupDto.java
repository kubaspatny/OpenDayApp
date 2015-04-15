package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.Group;
import cz.kubaspatny.opendayapp.bo.GroupSize;
import cz.kubaspatny.opendayapp.bo.LocationUpdate;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private Integer startingPosition;

    private GroupSizeDto latestGroupSize;
    private LocationUpdateDto latestLocationUpdate;

    private List<GroupSizeDto> groupSizes;
    private List<LocationUpdateDto> locationUpdates;

    private Integer lastStation;

    private boolean active;

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

    public Integer getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Integer startingPosition) {
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

    public GroupSizeDto getLatestGroupSize() {
        return latestGroupSize;
    }

    public LocationUpdateDto getLatestLocationUpdate() {
        return latestLocationUpdate;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getLastStation() {
        return lastStation;
    }

    public void setLastStation(Integer lastStation) {
        this.lastStation = lastStation;
    }

    public void computeLastStation(int numberStations){
        int n = startingPosition - 1;
        if(n <= 0) n = n + numberStations;
        setLastStation(n);
    }

    public boolean isAfterLast(int currentStation){
        return getLastStation().equals(currentStation)
                && (getLatestLocationUpdate() != null)
                && (getLatestLocationUpdate().getType() == LocationUpdate.Type.SKIP || getLatestLocationUpdate().getType() == LocationUpdate.Type.CHECKOUT);
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static GroupDto map(Group source, GroupDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.startingPosition = source.getStartingPosition();
        target.active = source.isActive();

        if(ignoredProperties == null) ignoredProperties = new ArrayList<String>();

        if(!ignoredProperties.contains("route")){
            List<String> routeIgnoredProperties = new ArrayList<String>();
            routeIgnoredProperties.add("stations");
            routeIgnoredProperties.add("stationManagers");
            routeIgnoredProperties.add("groups");
            target.route = RouteDto.map(source.getRoute(), new RouteDto(), routeIgnoredProperties);
        }

        if(!ignoredProperties.contains("guide")){
            target.guide = UserDto.map(source.getGuide(), new UserDto(), DtoMapperUtil.getUserIgnoredProperties());
        }

        if(source.getGroupSizes() != null) Collections.sort(source.getGroupSizes(), GroupSize.GroupSizeTimeComparator);

        if(!ignoredProperties.contains("groupSizes") && source.getGroupSizes() != null){

            List<GroupSizeDto> groupSizeDtos = new ArrayList<GroupSizeDto>();
            for(GroupSize g : source.getGroupSizes()){
                groupSizeDtos.add(GroupSizeDto.map(g, new GroupSizeDto(), null));
            }

            target.groupSizes = groupSizeDtos;

        }

        if(!ignoredProperties.contains("latestGroupSize") && source.getGroupSizes() != null && source.getGroupSizes().size() > 0){
            target.latestGroupSize = GroupSizeDto.map(source.getGroupSizes().get(source.getGroupSizes().size() - 1), new GroupSizeDto(), null);
        }

        if(source.getLocationUpdates() != null) Collections.sort(source.getLocationUpdates(), LocationUpdate.LocationUpdateComparator);

        if(!ignoredProperties.contains("locationUpdates") && source.getLocationUpdates() != null){

            List<LocationUpdateDto> locationUpdateDtos = new ArrayList<LocationUpdateDto>();
            for(LocationUpdate l : source.getLocationUpdates()){
                locationUpdateDtos.add(LocationUpdateDto.map(l, new LocationUpdateDto(), null));
            }

            target.locationUpdates = locationUpdateDtos;
        }

        if(!ignoredProperties.contains("latestLocationUpdate") && source.getLocationUpdates() != null && source.getLocationUpdates().size() > 0){
            target.latestLocationUpdate = LocationUpdateDto.map(source.getLocationUpdates().get(source.getLocationUpdates().size() - 1), new LocationUpdateDto(), null);
        }

        return target;
    }

    @Override
    public String getACLObjectIdentityClass() {
        return Group.class.getName();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GroupDto{");
        sb.append("\n\tid=").append(id);
        sb.append("\n\troute=").append(route);
        sb.append("\n\tguide=").append(guide);
        sb.append("\n\tstartingPosition=").append(startingPosition);
        sb.append("\n\tlatestGroupSize=").append(latestGroupSize);
        sb.append("\n\tlatestLocationUpdate=").append(latestLocationUpdate);
        sb.append("\n\tgroupSizes=").append(groupSizes);
        sb.append("\n\tlocationUpdates=").append(locationUpdates);
        sb.append("\n\tactive=").append(active);
        sb.append("\n}");
        return sb.toString();
    }

    public static Comparator<GroupDto> GroupStartingPosistionComparator = new Comparator<GroupDto>() {
        public int compare(GroupDto group1, GroupDto group2) {
            return ((Integer)group1.getStartingPosition()).compareTo(group2.getStartingPosition());
        }
    };

    public static Comparator<GroupDto> GroupDateComparator = new Comparator<GroupDto>() {
        public int compare(GroupDto group1, GroupDto group2) {
            return RouteDto.RouteDateComparator.compare(group1.getRoute(), group2.getRoute());
        }
    };

}
