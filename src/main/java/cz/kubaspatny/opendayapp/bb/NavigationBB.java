package cz.kubaspatny.opendayapp.bb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 8/2/2015
 * Time: 16:35
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

@Component("navBB")
@Scope("request")
public class NavigationBB {

    public String goCreateEvent(){
        return "event?faces-redirect=true&create=true";
    }

    public String goEvent(String id){
        return "event?faces-redirect=true&id=" + id;
    }

}
