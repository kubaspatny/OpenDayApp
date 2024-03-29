package cz.kubaspatny.opendayapp.bo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 13/3/2015
 * Time: 22:19
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
 * BO for GCM registration id and username pair.
 */
@Entity
public class AndroidDevice extends AbstractBusinessObject {

    @Column(nullable = false, unique = true)
    private String registrationId;

    @Column(nullable = false)
    private String username;

    public AndroidDevice() {
    }

    public AndroidDevice(String registrationId, String username) {
        this.registrationId = registrationId;
        this.username = username;
    }

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

}
