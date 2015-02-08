package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 8/2/2015
 * Time: 19:14
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

@ManagedBean
@ViewScoped
public class EventBean implements Serializable {

    @Autowired
    transient IEventService eventService;

    @PostConstruct
    public void init() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).
                getAutowireCapableBeanFactory().
                autowireBean(this);

        if(eventId != null) loadEvent();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        init();
    }

    private String modeCreate;
    private String eventId;

    private String createEventName;
    private Date createEventDate;
    private String createEventInfo;
    private boolean errorCreatingEvent;

    private EventDto event;

    public String getModeCreate() {
        return modeCreate;
    }

    public void setModeCreate(String modeCreate) {
        this.modeCreate = modeCreate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

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

    public void loadEvent() throws IOException {

        long id = -1;

        try {
            id = Long.parseLong(eventId);
        } catch (NumberFormatException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(404, "Number format exception!");
            facesContext.responseComplete();
        }

        try {
            event = eventService.getEvent(id);
        } catch (DataAccessException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(404, "Event not found!");
            facesContext.responseComplete();
        } catch (AccessDeniedException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(401, "Access denied!");
            facesContext.responseComplete();
        }

        System.out.println("event is null: " + (event == null));
    }

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }
}
