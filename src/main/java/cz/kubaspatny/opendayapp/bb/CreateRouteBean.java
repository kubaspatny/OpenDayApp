package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import cz.kubaspatny.opendayapp.service.IRouteService;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 20/2/2015
 * Time: 16:27
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
public class CreateRouteBean implements Serializable {

    @Autowired transient IRouteService routeService;
    @Autowired transient IEventService eventService;

    private String eventId;
    private EventDto event;

    private String routeColor;
    private String routeName;
    private String routeInfo;

    private List<DateTime> routeTimes;

    private int newTimeHour;
    private int newTimeMinute;
    private boolean errorDuplicateTime;

    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).
                getAutowireCapableBeanFactory().
                autowireBean(this);

        if(eventId != null){
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

    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

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
        this.routeTimes = routeTimes;
    }

    public void addRouteTime(DateTime time){
        if(routeTimes == null){
            routeTimes = new ArrayList<DateTime>();
        }

        routeTimes.add(time);
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

    public String addNewTime(){

        if(routeTimes != null && routeTimes.contains(event.getDate().withHourOfDay(newTimeHour).withMinuteOfHour(newTimeMinute))){
            RequestContext.getCurrentInstance().addCallbackParam("errorDuplicateTime", true);
            errorDuplicateTime = true;
            return "";
        }

        addRouteTime(event.getDate().withHourOfDay(newTimeHour).withMinuteOfHour(newTimeMinute));

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm");
        newTimeHour = 0;
        newTimeMinute = 0;
        errorDuplicateTime = false;
        return "";

    }

    public String formatTime(String number){
        System.out.println("formatTime:" + number);
        return number.length() == 1  ? "0" + number : "" + number;
    }

}
