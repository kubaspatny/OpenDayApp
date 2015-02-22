package cz.kubaspatny.opendayapp.bb;

import com.google.gson.Gson;
import cz.kubaspatny.opendayapp.bb.validator.EmailFormatValidator;
import cz.kubaspatny.opendayapp.bo.Station;
import cz.kubaspatny.opendayapp.dto.EventDto;
import cz.kubaspatny.opendayapp.dto.StationDto;
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
import java.util.*;

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

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:panel3");
        newTimeHour = 0;
        newTimeMinute = 0;
        errorDuplicateTime = false;
        return "";

    }

    public String formatTime(String number){
        System.out.println("formatTime:" + number);
        return number.length() == 1  ? "0" + number : "" + number;
    }

    private List<String> stationManagers;

    public List<String> getStationManagers() {
        return stationManagers;
    }

    public void setStationManagers(List<String> stationManagers) {
        this.stationManagers = stationManagers;
    }

    private String newGroupEmail;
    private List<String> groups;
    private List<String> reorderGroups;
    private boolean errorDuplicateGroup;

    public String getNewGroupEmail() {
        return newGroupEmail;
    }

    public void setNewGroupEmail(String newGroupEmail) {
        this.newGroupEmail = newGroupEmail;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void addGroup(String email){
        if(groups == null){
            groups = new ArrayList<String>();
        }

        groups.add(email);
    }

    public void removeGroup(String email){
        groups.remove(email);
    }

    public List<String> getReorderGroups() {

        if(reorderGroups == null){
            reorderGroups = groups;
        }

        return reorderGroups;
    }

    public void setReorderGroups(List<String> reorderGroups) {
        this.reorderGroups = reorderGroups;
    }

    public boolean isErrorDuplicateGroup() {
        return errorDuplicateGroup;
    }

    public void setErrorDuplicateGroup(boolean errorDuplicateGroup) {
        this.errorDuplicateGroup = errorDuplicateGroup;
    }

    public String addNewGroup(){

        if(groups != null && groups.contains(newGroupEmail)){
            RequestContext.getCurrentInstance().addCallbackParam("errorDuplicateGroup", true);
            errorDuplicateGroup = true;
            return "";
        }

        addGroup(newGroupEmail);

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("reorder-group-form");
        newGroupEmail = null;
        errorDuplicateGroup = false;
        return "";

    }

    public String reorderGroups(){

        setGroups(reorderGroups);

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        return "";

    }

    private StationDto newStation = new StationDto();
    private List<StationDto> stations;
    private HashMap<String, StationDto> stationReorderMap;
    private List<String> reorderStations;

    public StationDto getNewStation() {
        return newStation;
    }

    public void setNewStation(StationDto newStation) {
        this.newStation = newStation;
    }

    public List<StationDto> getStations() {
        return stations;
    }

    public void setStations(List<StationDto> stations) {
        this.stations = stations;
    }

    public void addStation(StationDto stationDto){
        if(stations == null){
            stations = new ArrayList<StationDto>();
        }

        stations.add(stationDto);
    }

    public void removeStation(StationDto stationDto){
        stations.remove(stationDto);
        reloadStationReorderObjects();
    }

    private void reloadStationReorderObjects(){
        reorderStations = new ArrayList<String>();
        stationReorderMap = new HashMap<String, StationDto>();

        for(StationDto s : stations){
            reorderStations.add(s.getName());
            stationReorderMap.put(s.getName(), s);
        }
    }

    public List<String> getReorderStations() {

        if(reorderStations == null){
            reloadStationReorderObjects();
        }

        return reorderStations;
    }

    public void setReorderStations(List<String> reorderStations) {
        this.reorderStations = reorderStations;
    }

    public String addNewStation(){

        addStation(newStation);
        reloadStationReorderObjects();

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:stations-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("reorder-station-form");
        newStation = new StationDto();
        return "";

    }

    public void validateStationNameUniqueConstraint(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ResourceBundle bundle = ResourceBundle.getBundle("strings", context.getViewRoot().getLocale());

        if (stationReorderMap != null && stationReorderMap.containsKey(value)) {
//            FacesMessage msg = new FacesMessage(bundle.getString("alreadyregistered"));
            FacesMessage msg = new FacesMessage("Station with such name already exists!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

    public void validateRouteTimes(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if(routeTimes == null || routeTimes.isEmpty()){
            FacesMessage msg = new FacesMessage("You need to add at least one route time!!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

    public void validateGroups(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if((groups != null && stations != null && groups.size() > stations.size()) || (groups != null && !groups.isEmpty() && stations == null)){
            FacesMessage msg = new FacesMessage("There are more groups than stations!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

    public String changeStationsOrder(){

        System.out.println("changeStationsOrder:" + reorderStations);

        List<StationDto> newOrder = new ArrayList<StationDto>();
        for(String key : reorderStations){
            newOrder.add(stationReorderMap.get(key));
        }

        setStations(newOrder);
        reloadStationReorderObjects();

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:stations-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        return "";

    }

    public String createRoute(){

        HashMap<Integer, String> guideMap = new HashMap<Integer, String>();
        if(groups != null){
            for(int i = 0; i < groups.size(); i++){
                guideMap.put(i+1, groups.get(i));
            }
        }

        if(stations != null){
            for(int i = 0; i < stations.size(); i++){
                stations.get(i).setSequencePosition(i+1);
            }
        }

//        System.out.println("CREATE ROUTE:");
//        System.out.println("eventId: " + eventId);
//        System.out.println("routeName: " + routeName);
//        System.out.println("routeColor: " + routeColor);
//        System.out.println("routeInfo: " + routeInfo);
//        System.out.println("routeTimes: " + routeTimes);
//        System.out.println("stations: " + stations);
//        System.out.println("guideMap: " + guideMap);
//        System.out.println("stationManagers: " + stationManagers);

        try {
            routeService.saveRoute(event.getId(), routeName, "#" + routeColor, routeInfo, routeTimes, stations, guideMap, stationManagers);
        } catch (DataAccessException e){
            // TODO: show error
        }

        return "event?faces-redirect=true&id=" + eventId;
    }

}
