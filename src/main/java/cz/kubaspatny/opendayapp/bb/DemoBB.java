package cz.kubaspatny.opendayapp.bb;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 17/12/2014
 * Time: 22:44
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
@Component("demoBB")
@Scope("request")
public class DemoBB {

    private DateTime dateTime = DateTime.now(DateTimeZone.UTC);

    public void update(){
        dateTime = DateTime.now(DateTimeZone.UTC);
    }

    public String getText() {
        return "Last updated: " + dateTime.toString("HH:mm:ss");
    }

    // -------------------------------------------------------------------------

    private String colorInline;
    private String colorPopup;

    public String getColorInline() {
        return colorInline;
    }

    public void setColorInline(String colorInline) {
        this.colorInline = colorInline;
    }

    public String getColorPopup() {
        return colorPopup;
    }

    public void setColorPopup(String colorPopup) {
        this.colorPopup = colorPopup;
    }
}
