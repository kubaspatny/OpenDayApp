package cz.kubaspatny.opendayapp.bb.valueobject;

import cz.kubaspatny.opendayapp.dto.StationDto;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 24/2/2015
 * Time: 22:17
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
 *
 * Data Transfer Object to be used when creating a new route.
 */
public class CreateRouteValueObject {

    private String routeColor;
    private String routeName;
    private String routeInfo;

    private List<DateTime> routeTimes;
    private int newTimeHour;
    private int newTimeMinute;
    private boolean errorDuplicateTime;

    private List<String> stationManagers;

    private String newGroupEmail;
    private List<String> groups;
    private List<String> reorderGroups;
    private boolean errorDuplicateGroup;

    private StationDto newStation = new StationDto();
    private List<StationDto> stations;
    private HashMap<String, StationDto> stationReorderMap;
    private List<String> reorderStations;

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(String routeInfo) {
        this.routeInfo = routeInfo;
    }

    public List<DateTime> getRouteTimes() {
        return routeTimes;
    }

    public void setRouteTimes(List<DateTime> routeTimes) {
        Collections.sort(routeTimes);
        this.routeTimes = routeTimes;
    }

    public void addRouteTime(DateTime time){
        if(routeTimes == null){
            routeTimes = new ArrayList<DateTime>();
        }

        routeTimes.add(time);
        Collections.sort(routeTimes);
    }

    public void removeRouteTime(DateTime time){
        routeTimes.remove(time);
    }

    public int getNewTimeHour() {
        return newTimeHour;
    }

    public void setNewTimeHour(int newTimeHour) {
        this.newTimeHour = newTimeHour;
    }

    public int getNewTimeMinute() {
        return newTimeMinute;
    }

    public void setNewTimeMinute(int newTimeMinute) {
        this.newTimeMinute = newTimeMinute;
    }

    public boolean isErrorDuplicateTime() {
        return errorDuplicateTime;
    }

    public void setErrorDuplicateTime(boolean errorDuplicateTime) {
        this.errorDuplicateTime = errorDuplicateTime;
    }

    public List<String> getStationManagers() {
        return stationManagers;
    }

    public void setStationManagers(List<String> stationManagers) {
        this.stationManagers = stationManagers;
    }

    public String getNewGroupEmail() {
        return newGroupEmail;
    }

    public void setNewGroupEmail(String newGroupEmail) {
        this.newGroupEmail = newGroupEmail;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void addGroup(String email){
        if(groups == null){
            groups = new ArrayList<String>();
        }

        groups.add(email);
    }

    public void removeGroup(String email){
        groups.remove(email);
    }

    public List<String> getReorderGroups() {

        if(reorderGroups == null){
            reorderGroups = groups;
        }

        return reorderGroups;
    }

    public void setReorderGroups(List<String> reorderGroups) {
        this.reorderGroups = reorderGroups;
    }

    public boolean isErrorDuplicateGroup() {
        return errorDuplicateGroup;
    }

    public void setErrorDuplicateGroup(boolean errorDuplicateGroup) {
        this.errorDuplicateGroup = errorDuplicateGroup;
    }

    public StationDto getNewStation() {
        return newStation;
    }

    public void setNewStation(StationDto newStation) {
        this.newStation = newStation;
    }

    public List<StationDto> getStations() {
        return stations;
    }

    public void setStations(List<StationDto> stations) {
        this.stations = stations;
    }

    public void addStation(StationDto stationDto){
        if(stations == null){
            stations = new ArrayList<StationDto>();
        }

        stations.add(stationDto);
    }

    public void removeStation(StationDto stationDto){
        stations.remove(stationDto);
        reloadStationReorderObjects();
    }

    public void reloadStationReorderObjects(){
        reorderStations = new ArrayList<String>();
        stationReorderMap = new HashMap<String, StationDto>();

        for(StationDto s : stations){
            reorderStations.add(s.getName());
            stationReorderMap.put(s.getName(), s);
        }
    }

    public List<String> getReorderStations() {

        if(reorderStations == null){
            reloadStationReorderObjects();
        }

        return reorderStations;
    }

    public void setReorderStations(List<String> reorderStations) {
        this.reorderStations = reorderStations;
    }

    public HashMap<String, StationDto> getStationReorderMap() {
        return stationReorderMap;
    }

    public void setStationReorderMap(HashMap<String, StationDto> stationReorderMap) {
        this.stationReorderMap = stationReorderMap;
    }
}
