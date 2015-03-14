package cz.kubaspatny.opendayapp.dto;

import com.sun.org.apache.xpath.internal.operations.And;
import cz.kubaspatny.opendayapp.bo.AndroidDevice;

import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 13/3/2015
 * Time: 22:22
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
public class AndroidDeviceDto extends BaseDto {

    private String registrationId;
    private String username;

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getACLObjectIdentityClass() {
        return AndroidDevice.class.getSimpleName();
    }

    public static AndroidDeviceDto map(AndroidDevice source, AndroidDeviceDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.registrationId = source.getRegistrationId();
        target.username = source.getUsername();

        return target;
    }

    public static AndroidDevice map(AndroidDeviceDto source, AndroidDevice target, List<String> ignoredProperties){

        target.setRegistrationId(source.getRegistrationId());
        target.setUsername(source.getUsername());

        return target;

    }
}
