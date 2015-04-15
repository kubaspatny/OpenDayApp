package cz.kubaspatny.opendayapp.dto;

import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 14/4/2015
 * Time: 23:37
 * Copyright 2015 Jakub Spatny
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
public class StationWrapper {

    public StationDto station;
    public List<GroupDto> groupsAtStation;
    public List<GroupDto> groupsAfterStation;

    public StationWrapper() {
    }

    public StationWrapper(StationDto station, List<GroupDto> groupsAtStation, List<GroupDto> groupsAfterStation) {
        this.station = station;
        this.groupsAtStation = groupsAtStation;
        this.groupsAfterStation = groupsAfterStation;
    }

    public StationDto getStation() {
        return station;
    }

    public void setStation(StationDto station) {
        this.station = station;
    }

    public List<GroupDto> getGroupsAtStation() {
        return groupsAtStation;
    }

    public void setGroupsAtStation(List<GroupDto> groupsAtStation) {
        this.groupsAtStation = groupsAtStation;
    }

    public List<GroupDto> getGroupsAfterStation() {
        return groupsAfterStation;
    }

    public void setGroupsAfterStation(List<GroupDto> groupsAfterStation) {
        this.groupsAfterStation = groupsAfterStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationWrapper that = (StationWrapper) o;

        if (station != null ? !station.equals(that.station) : that.station != null) return false;

        return true;
    }

}
