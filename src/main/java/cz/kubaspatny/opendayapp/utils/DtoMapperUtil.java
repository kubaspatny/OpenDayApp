package cz.kubaspatny.opendayapp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 19/11/2014
 * Time: 21:22
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
public class DtoMapperUtil {

    /**
     * Returns a list of properties which can be ignored by User Dto mapping methods between DTO->BO and BO->DTO.
     */
    public static List<String> getUserIgnoredProperties(){
        List<String> userIgnoredProperties = new ArrayList<String>();
        userIgnoredProperties.add("events");
        userIgnoredProperties.add("managedRoutes");
        userIgnoredProperties.add("groups");
        userIgnoredProperties.add("userRoles");

        return userIgnoredProperties;
    }

    /**
     * Returns a list of properties which can be ignored by Station Dto mapping methods between DTO->BO and BO->DTO.
     */
    public static List<String> getStationIgnoredProperties(){
        List<String> stationIgnoredProperties = new ArrayList<String>();
        stationIgnoredProperties.add("route");
        stationIgnoredProperties.add("locationUpdates");

        return stationIgnoredProperties;
    }

    /**
     * Returns a list of properties which can be ignored by Route Dto mapping methods between DTO->BO and BO->DTO.
     */
    public static List<String> getRouteIgnoredProperties(){
        List<String> routeIgnoredProperties = new ArrayList<String>();
        routeIgnoredProperties.add("event");
        routeIgnoredProperties.add("stations");
        routeIgnoredProperties.add("stationManagers");
        routeIgnoredProperties.add("groups");

        return routeIgnoredProperties;
    }

    /**
     * Returns a list of properties which can be ignored by Event Dto mapping methods between DTO->BO and BO->DTO.
     */
    public static List<String> getEventIgnoredProperties(){
        List<String> eventIgnoredProperties = new ArrayList<String>();
        eventIgnoredProperties.add("routes");
        eventIgnoredProperties.add("emailList");

        return eventIgnoredProperties;
    }

    /**
     * Returns a list of properties which can be ignored by Group Dto mapping methods between DTO->BO and BO->DTO.
     */
    public static List<String> getGroupIgnoredProperties(){
        List<String> groupIgnoredProperties = new ArrayList<String>();
        groupIgnoredProperties.add("groupSizes");
        groupIgnoredProperties.add("locationUpdates");
        groupIgnoredProperties.add("latestGroupSize");
        groupIgnoredProperties.add("latestLocationUpdate");

        return groupIgnoredProperties;
    }

}
