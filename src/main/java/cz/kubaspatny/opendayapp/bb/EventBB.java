package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 8/2/2015
 * Time: 15:49
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

@Component("eventBB")
@Scope("request")
public class EventBB {

    @Autowired
    private IEventService eventService;

    private String createEventName;
    private Date createEventDate;
    private String createEventInfo;
    private boolean errorCreatingEvent;

    public String getCreateEventName() {
        return createEventName;
    }

    public void setCreateEventName(String createEventName) {
        this.createEventName = createEventName;
    }

    public Date getCreateEventDate() {
        return createEventDate;
    }

    public void setCreateEventDate(Date createEventDate) {
        this.createEventDate = createEventDate;
    }

    public String getCreateEventInfo() {
        return createEventInfo;
    }

    public void setCreateEventInfo(String createEventInfo) {
        this.createEventInfo = createEventInfo;
    }

    public boolean isErrorCreatingEvent() {
        return errorCreatingEvent;
    }

    public String createEvent(){
        EventDto e = new EventDto();
        e.setName(createEventName);
        e.setDate(new DateTime(createEventDate));
        e.setInformation(createEventInfo);

        try {
            eventService.addEvent(e);
        } catch (DataAccessException ex){
            errorCreatingEvent = true;
            return "";
        }

        return "index?faces-redirect=true";
    }

}
