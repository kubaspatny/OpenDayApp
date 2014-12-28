package cz.kubaspatny.opendayapp.utils;

import cz.kubaspatny.opendayapp.bo.*;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 22/12/2014
 * Time: 02:28
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
public class SpelUtil {

    public static String name(Class clazz){
        return clazz == null ? null : clazz.getName();
    }

    public static String getACLObjectIdentityClass(String className){

        if("Event".equals(className)) return Event.class.getName();
        if("Route".equals(className)) return Route.class.getName();
        if("Station".equals(className)) return Station.class.getName();
        if("Group".equals(className)) return Group.class.getName();
        if("GroupSize".equals(className)) return GroupSize.class.getName();
        if("LocationUpdate".equals(className)) return LocationUpdate.class.getName();
        if("User".equals(className)) return User.class.getName();

        return null;

    }

}
