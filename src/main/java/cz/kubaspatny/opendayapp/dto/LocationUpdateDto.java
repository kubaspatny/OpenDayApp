package cz.kubaspatny.opendayapp.dto;

import com.google.gson.annotations.Expose;
import cz.kubaspatny.opendayapp.bo.LocationUpdate;
import cz.kubaspatny.opendayapp.utils.DtoMapperUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:14
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
public class LocationUpdateDto extends BaseDto {

    private DateTime timestamp;
    private LocationUpdate.Type type;
    private StationDto station;
    @Expose(serialize = false) private GroupDto group;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public GroupDto getGroup() {
        return group;
    }

    public void setGroup(GroupDto group) {
        this.group = group;
    }

    public LocationUpdate.Type getType() {
        return type;
    }

    public void setType(LocationUpdate.Type type) {
        this.type = type;
    }

    public StationDto getStation() {
        return station;
    }

    public void setStation(StationDto station) {
        this.station = station;
    }

    @Override
    public String getACLObjectIdentityClass() {
        return LocationUpdate.class.getName();
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static LocationUpdateDto map(LocationUpdate source, LocationUpdateDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.type = source.getType();
        target.timestamp = source.getTimestamp();

        target.station = StationDto.map(source.getStation(), new StationDto(), DtoMapperUtil.getStationIgnoredProperties());
        target.group = GroupDto.map(source.getGroup(), new GroupDto(), DtoMapperUtil.getGroupIgnoredProperties());

        return target;
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static LocationUpdate map(LocationUpdateDto source, LocationUpdate target, List<String> ignoredProperties){
        target.setTimestamp(source.getTimestamp());
        target.setType(source.getType());
        return target;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocationUpdateDto{");
        sb.append("id=").append(id);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", type=").append(type);
        sb.append(", station=").append(station);
        sb.append(", group=").append(group);
        sb.append('}');
        return sb.toString();
    }

    public String getElapsedDuration(){
        DateTime now = DateTime.now();
        long difSec = (now.getMillis() - timestamp.getMillis()) / 1000;

        long hours = (difSec / 3600);
        long minutes = (difSec % 3600) / 60;
        long seconds = difSec % 60;


        return formatTime(hours, minutes, seconds);
    }

    private static String formatTime(long hours, long minutes, long seconds){

        if(hours >= 24) return "> 24 hrs";

        DateTime time = new DateTime().withTime((int)hours, (int)minutes, (int)seconds, 0);
        return time.toString("HH:mm:ss");

    }

}
