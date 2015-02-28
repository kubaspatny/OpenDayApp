package cz.kubaspatny.opendayapp.bb.valueobject;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 28/2/2015
 * Time: 09:58
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
public class EditRouteHolder {

    private String name;
    private int newTimeHour;
    private int newTimeMinute;
    private String information;
    private String color;

    public EditRouteHolder(String name, int newTimeHour, int newTimeMinute, String information, String color) {
        this.name = name;
        this.newTimeHour = newTimeHour;
        this.newTimeMinute = newTimeMinute;
        this.information = information;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
