package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IEventService;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
 * Date: 12/2/2015
 * Time: 19:50
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
@RequestScoped
public class CreateEventBean implements Serializable {

    @Autowired
    transient IEventService eventService;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).
                getAutowireCapableBeanFactory().
                autowireBean(this);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        init();
    }

    private String createEventName;
    private Date createEventDate;
    private String createEventInfo;
    private boolean errorCreatingEvent;

    private EventDto event;

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

    public void setErrorCreatingEvent(boolean errorCreatingEvent) {
        this.errorCreatingEvent = errorCreatingEvent;
    }

    private void clearFields(){
        createEventName = null;
        createEventDate = null;
        createEventInfo = null;
    }

    public String createEvent(){
        EventDto e = new EventDto();
        e.setName(createEventName);
        e.setDate(new DateTime(createEventDate));
        e.setInformation(createEventInfo);

        try {
            eventService.addEvent(e);
        } catch (DataAccessException ex){
            RequestContext.getCurrentInstance().addCallbackParam("errorCreatingEvent", true);
            errorCreatingEvent = true;
            return "";
        }

        clearFields();
        return "index?faces-redirect=true";
    }

    public Date getCurrentDate(){
        return new Date();
    }

}
