package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.bb.valueobject.EditEventHolder;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.RouteDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Autowired transient IEventService eventService;
    @Autowired transient IRouteService routeService;
    @Autowired transient IUserService userService;

    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).
                getAutowireCapableBeanFactory().
                autowireBean(this);

        if(eventId != null) {
            try {
                loadEvent();
            } catch (IOException e){
                // TODO: log message (couldn't redirect to error code)
            }
        } else {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                facesContext.getExternalContext().responseSendError(404, "ID parameter missing!");
                facesContext.responseComplete();
            } catch (IOException e){
                // TODO: log message (couldn't redirect to error code)
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        init();
    }

    private String modeCreate;
    private String eventId;

    private boolean errorRegisteringUser;
    private boolean errorUpdatingEvent;
    private EventDto event;
    private String newPersonEmail;

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

    public boolean isErrorRegisteringUser() {
        return errorRegisteringUser;
    }

    public boolean isErrorUpdatingEvent() {
        return errorUpdatingEvent;
    }

    public String getNewPersonEmail() {
        return newPersonEmail;
    }

    public void setNewPersonEmail(String newPersonEmail) {
        this.newPersonEmail = newPersonEmail;
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
            setRoutes(routeService.getRoutes(id));

            editEvent = new EditEventHolder(event.getName(), event.getDate().toDate(), event.getInformation());

        } catch (DataAccessException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(404, "Event not found!");
            facesContext.responseComplete();
        } catch (AccessDeniedException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(401, "Access denied!");
            facesContext.responseComplete();
        }

    }

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

    public boolean isRegistered(String email){
        try {
            return !userService.isEmailFree(email);
        } catch (DataAccessException e){
            // log exception
            return false;
        }
    }

    public boolean areAllEmailsRegistered(ArrayList<String> emails){
        for(String e : emails){
            if(!isRegistered(e)) return false;
        }

        return true;
    }

    public String addNewPersonToEmailList(){

        try {
            if(!isRegistered(newPersonEmail)){
                userService.createGeneratedUser(newPersonEmail);
            }
            eventService.addEmailToList(event.id, newPersonEmail);
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorRegisteringUser", true);
            errorRegisteringUser = true;
            return "";
        }

        try {
            loadEvent();
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("people-form");
        } catch (IOException e){
            // TODO: log message (couldn't redirect to error code)
        }

        newPersonEmail = null;
        return "";
    }

    public String addEmailsToEmailList(ArrayList<String> emails){

        for(String email : emails){

            try {
                if(!isRegistered(email)){
                    userService.createGeneratedUser(email);
                }
                eventService.addEmailToList(event.id, email);
            } catch (DataAccessException e){
                RequestContext.getCurrentInstance().addCallbackParam("errorRegisteringUser", true);
                errorRegisteringUser = true;
                return "";
            }

        }

        try {
            loadEvent();
            RequestContext.getCurrentInstance().addCallbackParam("uploadFinished", true);
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("people-form");
        } catch (IOException e){
            // TODO: log message (couldn't redirect to error code)
        }

        return "";

    }

    public void removePersonFromEmailList(String email){

        try {
            eventService.removeEmailFromList(event.getId(), email);
        } catch (DataAccessException e){
            // TODO display error
        }

        try {
            loadEvent();
        } catch (IOException e){
            // TODO: log message (couldn't redirect to error code)
        }

    }

    public void removeRoute(Long id){

        try {
            routeService.removeRoute(id);
        } catch (DataAccessException e){
            // TODO display error
        }

        try {
            loadEvent();
        } catch (IOException e){
            // TODO: log message (couldn't redirect to error code)
        }

    }

    private List<RouteDto> routes;

    public List<RouteDto> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDto> routes) {
        Collections.sort(routes, RouteDto.RouteDateComparator);
        this.routes = routes;
    }

    private EditEventHolder editEvent;

    public EditEventHolder getEditEvent() {
        return editEvent;
    }

    public void setEditEvent(EditEventHolder editEvent) {
        this.editEvent = editEvent;
    }

    public void updateEvent() throws IOException {

        try {
            event = eventService.getEvent(event.getId());
        } catch (DataAccessException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(404, "Event not found!");
            facesContext.responseComplete();
        } catch (AccessDeniedException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(401, "Access denied!");
            facesContext.responseComplete();
        }

        try {
            event.setName(editEvent.getName());
            event.setDate(new DateTime(editEvent.getDate()));
            event.setInformation(editEvent.getInformation());
            eventService.updateEvent(event);

            for(RouteDto r : routes){
                r.setDate(event.getDate().withTime(r.getDate().getHourOfDay(), r.getDate().getMinuteOfHour(), 0, 0));
                routeService.updateRoute(r);
            }

            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorUpdatingEvent", true);
            errorUpdatingEvent = true;
            return;
        } catch (AccessDeniedException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(401, "Access denied!");
            facesContext.responseComplete();
        }

        errorUpdatingEvent = false;

    }

    public Date getCurrentDate(){
        return new Date();
    }

}