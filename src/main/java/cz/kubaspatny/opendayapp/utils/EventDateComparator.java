package cz.kubaspatny.opendayapp.utils;

import cz.kubaspatny.opendayapp.bo.Event;
import org.joda.time.DateTimeComparator;

import java.util.Comparator;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 18:11
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
public class EventDateComparator implements Comparator<Event> {

    private DateTimeComparator dateTimeComparator = DateTimeComparator.getInstance();

    @Override
    public int compare(Event o1, Event o2) {
        return dateTimeComparator.compare(o1.getDate(), o2.getDate());
    }
}
