package cz.kubaspatny.opendayapp.bb;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dto.UserDto;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IUserService;
import cz.kubaspatny.opendayapp.service.TestService;
import cz.kubaspatny.opendayapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 24/1/2015
 * Time: 23:00
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
@Component("registrationBB")
@Scope("request")
public class RegistrationBB {

    @Autowired
    private IUserService userService;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String organization;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String register(){

        UserDto user = new UserDto();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.addUserRole(User.UserRole.ROLE_USER);
        user.addUserRole(User.UserRole.ROLE_ORGANIZER);
        user.addUserRole(User.UserRole.ROLE_GUIDE);
        user.addUserRole(User.UserRole.ROLE_STATIONMANAGER);

        try {
            userService.createUser(user);
        } catch (DataAccessException e){
            return "registration&error=1";
        }

        return "login?faces-redirect=true&registered=1";
    }

}
