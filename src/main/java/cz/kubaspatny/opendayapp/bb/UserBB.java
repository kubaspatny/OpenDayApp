package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 1/2/2015
 * Time: 10:37
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

@Component("userBB")
@Scope("session")
public class UserBB {

    @Autowired private IUserService userService;

    private Authentication auth;
    private UserDto user;
    private UserDto editUser;

    @PostConstruct
    public void init(){
        refresh();
    }

    public void refresh(){
        auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            user = userService.getUser(getName());
        } catch (DataAccessException e){
            e.printStackTrace();
            user = new UserDto();
        }
    }

    public String getName(){
        return auth.getName();
    }

    public UserDto getUser() {
        return user;
    }

    public String deactivate(){

        try {
            userService.deactivateUser(user.id);
            SecurityContextHolder.clearContext();
            return "login?faces-redirect=true";
        } catch (DataAccessException e){
            e.printStackTrace();
        }

        return "";
    }

    public UserDto getEditUser() {
        if(editUser == null){
            editUser = new UserDto();
            editUser.setFirstName(user.getFirstName());
            editUser.setLastName(user.getLastName());
            editUser.setOrganization(user.getOrganization());
        }

        return editUser;
    }

    public void setEditUser(UserDto editUser) {
        this.editUser = editUser;
    }

    public void updateUserInformation() throws IOException {

        try {
            if(editUser != null){
                user.setFirstName(editUser.getFirstName());
                user.setLastName(editUser.getLastName());
                user.setOrganization(editUser.getOrganization());
            }

            userService.updateUser(user);
            refresh();
        } catch (DataAccessException e){
            RequestContext.getCurrentInstance().addCallbackParam("errorUpdatingUser", true);
            return;
        } catch (AccessDeniedException e){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().responseSendError(401, "Access to route denied!");
            facesContext.responseComplete();
            return;
        }

    }
}
