package cz.kubaspatny.opendayapp.bb.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 25/1/2015
 * Time: 00:57
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

@FacesValidator("requiredValidator")
public class RequiredValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if(value == null || ((String)value).isEmpty()){

            FacesMessage msg = new FacesMessage(getValidatorMessage(component));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);

        }

    }

    protected String getValidatorMessage(UIComponent uiComponent) {

        String requiredMessage = (String) uiComponent.getAttributes().get("requiredMessage");

        if(requiredMessage == null) {
            requiredMessage = "Field is required."; // LOCALIZATION NEEDED http://tomaszdziurko.pl/2011/10/parametrizing-custom-validator-jsf-2/
        }

        return requiredMessage;
    }


}
