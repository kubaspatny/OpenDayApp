package cz.kubaspatny.opendayapp.dto;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 19/4/2015
 * Time: 18:06
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
 * DTO object for calculating station statistics.
 */
public class UpdatePair {

    private LocationUpdateDto checkin;
    private LocationUpdateDto checkout;

    public UpdatePair() {
    }

    public void addUpdate(LocationUpdateDto update){

        switch(update.getType()){
            case CHECKIN:
                checkin = update;
                break;
            case CHECKOUT:
                checkout = update;
                break;
        }

    }

    /**
     * @return elapsed time between the 2 location updates in seconds
     */
    public long calculate(){
        if(checkin == null || checkout == null) return -1;

        Duration d = new Duration(checkin.getTimestamp(), checkout.getTimestamp());
        return d.getStandardSeconds();
    }

}
