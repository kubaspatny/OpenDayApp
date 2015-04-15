package cz.kubaspatny.opendayapp.bb;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import cz.kubaspatny.opendayapp.bb.valueobject.CreateRouteValueObject;
import cz.kubaspatny.opendayapp.bb.valueobject.EditRouteHolder;
import cz.kubaspatny.opendayapp.bo.Group;
import cz.kubaspatny.opendayapp.bo.LocationUpdate;
import cz.kubaspatny.opendayapp.dto.*;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.*;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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
public class RouteBean implements Serializable {

    @Autowired transient IRouteService routeService;
    @Autowired transient IStationService stationService;
    @Autowired transient IEventService eventService;
    @Autowired transient IGroupService groupService;

    private String eventId;
    private String routeId;
    private String mode;
    private EventDto event;
    private RouteDto route;

    private List<StationWrapper> liveStations;

    private CreateRouteValueObject cvo;
    private EditRouteHolder editRouteHolder;

    private boolean errorUpdatingRoute;
    private boolean errorUpdatingStation;

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

        if(cvo == null){
            cvo = new CreateRouteValueObject();
        }

    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        init();
    }

    public void loadEvent() throws IOException {

        if(mode == null || !(mode.equals("view") || mode.equals("create") || mode.equals("edit"))){
            redirectToError(404, "Wrong mode selected!");
            return;
        }

        // load Event
        try {
            long parsedEventId = Long.parseLong(eventId);
            event = eventService.getEvent(parsedEventId);
        } catch (NumberFormatException e){
            redirectToError(404, "Number format exception!");
            return;
        } catch (DataAccessException e){
            redirectToError(404, "Event not found!");
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to event denied!");
            return;
        }

        if(mode.equals("view") || mode.equals("edit")){
            try {
                long parsedRouteId = Long.parseLong(routeId);
                route = routeService.getRoute(parsedRouteId);
                Collections.sort(route.getStations(), StationDto.StationSequencePosistionComparator);
                Collections.sort(route.getGroups(), GroupDto.GroupStartingPosistionComparator);
            } catch (NumberFormatException e){
                redirectToError(404, "Number format exception!");
                return;
            } catch (DataAccessException e){
                redirectToError(404, "Route not found!");
                return;
            } catch (AccessDeniedException e){
                redirectToError(401, "Access to route denied!");
                return;
            }
        }

    }

    private HashMap<Long, List<GroupDto>> processGroups(List<GroupDto> groups){

        HashMap<Long, List<GroupDto>> groupMap = new HashMap<Long, List<GroupDto>>();

        for(GroupDto g : groups){

            if(g.getLatestLocationUpdate() == null){ // Group hasn't sent any location updates yet..
                continue;
            }

            Long stationId = g.getLatestLocationUpdate().getStation().getId();

            if(groupMap.containsKey(stationId)){
                groupMap.get(stationId).add(g);
            } else {
                List<GroupDto> groupsAtStation = new ArrayList<GroupDto>();
                groupsAtStation.add(g);
                groupMap.put(stationId, groupsAtStation);
            }

        }

        return groupMap;
    }

    public void refreshRoute() throws IOException {

        try {
            if(mode != null && mode.equals("view") && route != null){
                route = routeService.getRoute(route.getId());
                route.setGroups(groupService.getGroupsWithCurrentLocation(route.getId()));
                Collections.sort(route.getStations(), StationDto.StationSequencePosistionComparator);
                Collections.sort(route.getGroups(), GroupDto.GroupStartingPosistionComparator);

                HashMap<Long, List<GroupDto>> groups = processGroups(route.getGroups());

                List<StationWrapper> stationWrappers = new ArrayList<StationWrapper>();

                StationWrapper stationWrapper;
                for(StationDto s : route.getStations()){

                    stationWrapper = new StationWrapper();
                    stationWrapper.station = s;

                    List<GroupDto> groupsAtStation;
                    List<GroupDto> groupsAfterStation;

                    if(groups.containsKey(s.getId())){

                        groupsAtStation = new ArrayList<GroupDto>();
                        groupsAfterStation = new ArrayList<GroupDto>();

                        Iterator<GroupDto> groupIterator = groups.get(s.getId()).iterator();
                        while(groupIterator.hasNext()){
                            GroupDto g = groupIterator.next();
                            g.computeLastStation(route.getStations().size());
                            if(!g.isAfterLast(s.getSequencePosition())){
                                if(g.getLatestLocationUpdate().getType() == LocationUpdate.Type.CHECKIN){
                                    groupsAtStation.add(g);
                                } else {
                                    groupsAfterStation.add(g);
                                }
                            }
                        }
                        stationWrapper.groupsAtStation = groupsAtStation;
                        stationWrapper.groupsAfterStation = groupsAfterStation;
                    } else {
                        stationWrapper.groupsAtStation = Collections.emptyList();
                        stationWrapper.groupsAfterStation = Collections.emptyList();
                    }

                    stationWrappers.add(stationWrapper);

                }

                liveStations = stationWrappers;

            }
        } catch (NumberFormatException e){
            redirectToError(404, "Number format exception!");
            return;
        } catch (DataAccessException e){
            redirectToError(404, "Route not found!");
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }

    }

    private void redirectToError(int code, String message) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().responseSendError(code, message);
        facesContext.responseComplete();
    }

    public CreateRouteValueObject getCvo() {
        return cvo;
    }

    public EditRouteHolder getEditRouteHolder() {
        return editRouteHolder;
    }

    public boolean isErrorUpdatingRoute() {
        return errorUpdatingRoute;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

    public RouteDto getRoute() {
        return route;
    }

    public void setRoute(RouteDto route) {
        this.route = route;
    }

    public List<StationWrapper> getLiveStations() {
        return liveStations;
    }

    public void setLiveStations(List<StationWrapper> liveStations) {
        this.liveStations = liveStations;
    }

    public String addNewTime(){

        if(cvo.getRouteTimes() != null && cvo.getRouteTimes().contains(event.getDate().withHourOfDay(cvo.getNewTimeHour()).withMinuteOfHour(cvo.getNewTimeMinute()))){
            RequestContext.getCurrentInstance().addCallbackParam("errorDuplicateTime", true);
            cvo.setErrorDuplicateTime(true);
            return "";
        }

        cvo.addRouteTime(event.getDate().withHourOfDay(cvo.getNewTimeHour()).withMinuteOfHour(cvo.getNewTimeMinute()));

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:panel3");
        cvo.setNewTimeHour(0);
        cvo.setNewTimeMinute(0);
        cvo.setErrorDuplicateTime(false);
        return "";

    }

    public String formatTime(String number){
        System.out.println("formatTime:" + number);
        return number.length() == 1  ? "0" + number : "" + number;
    }

    public String addNewGroup(){

        if(cvo.getGroups() != null && cvo.getGroups().contains(cvo.getNewGroupEmail())){
            RequestContext.getCurrentInstance().addCallbackParam("errorDuplicateGroup", true);
            cvo.setErrorDuplicateGroup(true);
            return "";
        }

        cvo.addGroup(cvo.getNewGroupEmail());

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("reorder-group-form");
        cvo.setNewGroupEmail(null);
        cvo.setErrorDuplicateGroup(false);
        return "";

    }

    public String reorderGroups(){

        cvo.setGroups(cvo.getReorderGroups());

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        return "";

    }

    public String addNewStation(){

        cvo.addStation(cvo.getNewStation());
        cvo.reloadStationReorderObjects();

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:stations-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("reorder-station-form");
        cvo.setNewStation(new StationDto());
        return "";

    }

    public void clearStation(){
        editRouteHolder = new EditRouteHolder(new StationDto());
    }

    public void updateStation() throws IOException {

        try {
            stationService.updateStation(editRouteHolder.getStation());
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorUpdatingStation", true);
            errorUpdatingStation = true;
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }

        errorUpdatingStation = false;

    }

    public void removeStation(Long id){

        try {
            stationService.removeStation(id);

            List<StationDto> stationDtos = routeService.getRoute(route.id).getStations();
            if(stationDtos != null && !stationDtos.isEmpty()){
                Collections.sort(stationDtos, StationDto.StationSequencePosistionComparator);

                for(int i = 0; i < stationDtos.size(); i++){
                    stationDtos.get(i).setSequencePosition(i + 1);
                    stationService.updateStation(stationDtos.get(i));
                }

            }

        } catch (DataAccessException e){
            // TODO display error
        }

        try {
            loadEvent();
        } catch (IOException e){
            // TODO: log message (couldn't redirect to error code)
        }

    }

    public void removeStationManager(String email) throws IOException {
        try {
            routeService.removeStationManager(route.id, email);
            loadEvent();
        } catch (DataAccessException e){
            e.printStackTrace();
            // TODO display error
        }
    }

    public void removeGroup(Long groupId) throws IOException {
        try {
            groupService.removeGroup(groupId);

            List<GroupDto> groupDtos = routeService.getRoute(route.id).getGroups();
            if(groupDtos != null && !groupDtos.isEmpty()){
                Collections.sort(groupDtos, GroupDto.GroupStartingPosistionComparator);

                for(int i = 0; i < groupDtos.size(); i++){
                    groupService.setGroupStartingPosition(groupDtos.get(i).id, i + 1);
                }
            }

            loadEvent();
        } catch (DataAccessException e){
            e.printStackTrace();
            // TODO display error
        }
    }

    public void validateStationNameUniqueConstraint(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ResourceBundle bundle = ResourceBundle.getBundle("strings", context.getViewRoot().getLocale());

        if(mode.equals("create")){
            if (cvo.getStationReorderMap() != null && cvo.getStationReorderMap().containsKey(value)) {
//            FacesMessage msg = new FacesMessage(bundle.getString("alreadyregistered"));
                FacesMessage msg = new FacesMessage("Station with such name already exists!");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        } else if(mode.equals("view")){

            for(StationDto s : route.getStations()){
                if(s.getName().equals(value) && !s.getId().equals(editRouteHolder.getStation().getId())){

//                    FacesMessage msg = new FacesMessage(bundle.getString("alreadyregistered"));
                    FacesMessage msg = new FacesMessage("Station with such name already exists!");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);

                }
            }
        }

    }

    public void validateGroupUniqueConstraint(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        ResourceBundle bundle = ResourceBundle.getBundle("strings", context.getViewRoot().getLocale());

        if(route.getGroups() != null && !route.getGroups().isEmpty()){
            for(GroupDto g : route.getGroups()){
                if(g.getGuide().getEmail().equals(value)){
//                    FacesMessage msg = new FacesMessage(bundle.getString("alreadyregistered"));
                    FacesMessage msg = new FacesMessage("Group with such name already exists!");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }
            }
        }
    }

    public void validateRouteTimes(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if(cvo.getRouteTimes() == null || cvo.getRouteTimes().isEmpty()){
            FacesMessage msg = new FacesMessage("You need to add at least one route time!!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

    public void validateGroups(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if((cvo.getGroups() != null && cvo.getStations() != null && cvo.getGroups().size() > cvo.getStations().size()) || (cvo.getGroups() != null && !cvo.getGroups().isEmpty() && cvo.getStations() == null)){
            FacesMessage msg = new FacesMessage("There are more groups than stations!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

    public void validateStationManagerUniqueConstraint(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if((route.getStationManagers() != null && !route.getStationManagers().isEmpty())){
            for(UserDto m : route.getStationManagers()){
                if(m.getEmail().equals(value)){
                    FacesMessage msg = new FacesMessage("This station manager was already added!");
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }
            }
        }
    }

    public String changeStationsOrder(){

        List<StationDto> newOrder = new ArrayList<StationDto>();
        for(String key : cvo.getReorderStations()){
            newOrder.add(cvo.getStationReorderMap().get(key));
        }

        cvo.setStations(newOrder);
        cvo.reloadStationReorderObjects();

        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:stations-container");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("createRouteForm:groups-container");
        return "";

    }

    public String createRoute(){

        HashMap<Integer, String> guideMap = new HashMap<Integer, String>();
        if(cvo.getGroups() != null){
            for(int i = 0; i < cvo.getGroups().size(); i++){
                guideMap.put(i+1, cvo.getGroups().get(i));
            }
        }

        if(cvo.getStations() != null){
            for(int i = 0; i < cvo.getStations().size(); i++){
                cvo.getStations().get(i).setSequencePosition(i+1);
            }
        }

        try {
            routeService.saveRoute(event.getId(), cvo.getRouteName(), "#" + cvo.getRouteColor(), cvo.getRouteInfo(), cvo.getRouteTimes(), cvo.getStations(), guideMap, cvo.getStationManagers());
        } catch (DataAccessException e){
            // TODO: show error
        }

        return "event?faces-redirect=true&id=" + eventId;
    }

    private int selectedStation;

    public int getSelectedStation() {
        return selectedStation;
    }

    public void setSelectedStation(int selectedStation) {
        this.selectedStation = selectedStation;
    }

    public void showStation(int selectedStation){
        setSelectedStation(selectedStation);
    }

    public void editStation(Long id) throws IOException {
        try {
            editRouteHolder = new EditRouteHolder(stationService.getStation(id));
        } catch (DataAccessException e){
            redirectToError(404, "Route not found!");
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }
    }

    public void editRouteInfo(){
        editRouteHolder = new EditRouteHolder(route.getName(),
                route.getDate().getHourOfDay(),
                route.getDate().getMinuteOfHour(),
                route.getInformation(),
                route.getHexColor().replace("#",""));
    }

    public void updateRouteInfo() throws IOException {
        RouteDto routeDto;

        try {
            routeDto = routeService.getRoute(route.getId());
        } catch (NumberFormatException e){
            redirectToError(404, "Number format exception!");
            return;
        } catch (DataAccessException e){
            redirectToError(404, "Route not found!");
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }

        try {
            routeDto.setName(editRouteHolder.getName());
            routeDto.setDate(routeDto.getDate().withTime(editRouteHolder.getNewTimeHour(), editRouteHolder.getNewTimeMinute(), 0, 0));
            routeDto.setInformation(editRouteHolder.getInformation());
            routeDto.setHexColor("#" + editRouteHolder.getColor());

            routeService.updateRoute(routeDto);
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorUpdatingRoute", true);
            errorUpdatingRoute = true;
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }

        errorUpdatingRoute = false;

    }

    public void addNewStationToExistingRoute() throws IOException {

        try {
            editRouteHolder.getStation().setSequencePosition(route.getStations().size() + 1);
            stationService.addStation(route.id, editRouteHolder.getStation());
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorAddingStation", true);
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }

    }

    public void addNewGroupToExistingRoute() throws IOException {

        try {
            groupService.addGroup(route.getId(), route.getGroups().size() + 1, editRouteHolder.getNewGroupEmail());
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorAddingGroup", true);
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }

    }

    public void changeStationsOrderExistingRoute() throws IOException {
        String key;
        StationDto station;

        try {
            for(int i = 0; i < editRouteHolder.getReorderStations().size(); i++){
                key = editRouteHolder.getReorderStations().get(i);
                station = editRouteHolder.getStationReorderMap().get(key);
                station.setSequencePosition(i + 1);
                stationService.updateStation(station);
            }
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorUpdatingStationOrder", true);
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }
    }

    public void changeGroupOrderExistingRoute() throws IOException {
        String key;
        GroupDto group;

        try {
            for(int i = 0; i < editRouteHolder.getReorderGroups().size(); i++){
                key = editRouteHolder.getReorderGroups().get(i);
                group = editRouteHolder.getGroupReorderMap().get(key);
                groupService.setGroupStartingPosition(group.id, i + 1);
            }
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorUpdateGroupOrder", true);
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }
    }

    public void setUpStationReorderMap(){
        editRouteHolder = new EditRouteHolder();
        editRouteHolder.setStations(route.getStations());

        List<String> reorderStations = new ArrayList<String>();
        HashMap<String, StationDto> reorderStationMap = new HashMap<String, StationDto>();

        for(StationDto s : route.getStations()){
            reorderStations.add(s.getName());
            reorderStationMap.put(s.getName(), s);
        }

        editRouteHolder.setReorderStations(reorderStations);
        editRouteHolder.setStationReorderMap(reorderStationMap);
    }

    public void setUpGroupReorderMap(){
        editRouteHolder = new EditRouteHolder();
        editRouteHolder.setGroups(route.getGroups());

        List<String> reorderGroups = new ArrayList<String>();
        HashMap<String, GroupDto> reorderGroupMap = new HashMap<String, GroupDto>();

        for(GroupDto g : route.getGroups()){
            reorderGroups.add(g.getGuide().getEmail());
            reorderGroupMap.put(g.getGuide().getEmail(), g);
        }

        editRouteHolder.setReorderGroups(reorderGroups);
        editRouteHolder.setGroupReorderMap(reorderGroupMap);
    }

    public void setUpEmptyRouteHolder(){
        editRouteHolder = new EditRouteHolder();
    }

    public void addNewStationManager() throws IOException {
        try {
            routeService.addStationManager(route.id, editRouteHolder.getNewStationManager());
            loadEvent();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorAddingStationManager", true);
            return;
        } catch (AccessDeniedException e){
            redirectToError(401, "Access to route denied!");
            return;
        }
    }
}
