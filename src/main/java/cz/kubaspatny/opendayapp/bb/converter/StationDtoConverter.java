package cz.kubaspatny.opendayapp.bb.converter;

import com.google.gson.Gson;
import cz.kubaspatny.opendayapp.dto.StationDto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 22/2/2015
 * Time: 00:52
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
@FacesConverter("stationDtoConverter")
public class StationDtoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("getAsObject:" + value);
        Gson gson = new Gson();
        return gson.fromJson(value, StationDto.class);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("getAsString:" + value.toString());
        Gson gson = new Gson();
        return gson.toJson(value.toString());
    }

}
