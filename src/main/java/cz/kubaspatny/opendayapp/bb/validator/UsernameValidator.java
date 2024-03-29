package cz.kubaspatny.opendayapp.bb.validator;

import cz.kubaspatny.opendayapp.exception.DataAccessException;
import cz.kubaspatny.opendayapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 29/1/2015
 * Time: 20:29
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
 *
 * Custom JSF validator, which checks whether given username is still free
 * (not used by any other user already).
 */

@Component
@Scope("request")
public class UsernameValidator implements Validator {

    @Autowired
    private IUserService userService;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String username = (String) value;
        if(username == null || username.isEmpty()){
            return; // let required=true do its job
        }

        ResourceBundle bundle = ResourceBundle.getBundle("strings", context.getViewRoot().getLocale());
        try {

            if(!userService.isUsernameFree(username)){
                FacesMessage msg = new FacesMessage(bundle.getString("username-taken"));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }

        } catch (DataAccessException e){
            FacesMessage msg = new FacesMessage(bundle.getString("required-username"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

}