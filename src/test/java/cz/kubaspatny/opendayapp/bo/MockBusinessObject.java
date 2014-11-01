package cz.kubaspatny.opendayapp.bo;

import javax.persistence.Entity;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 30/10/2014
 * Time: 20:49
 * Copyright 2014 Jakub Spatny
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

@Entity
public class MockBusinessObject extends AbstractBusinessObject {

    String text;
    int category;

    public MockBusinessObject() {
    }

    public MockBusinessObject(String text) {
        this.text = text;
    }

    public MockBusinessObject(String text, int category) {
        this.text = text;
        this.category = category;
    }

    public MockBusinessObject(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public MockBusinessObject(Long id, String text, int category) {
        this.id = id;
        this.text = text;
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "MockBusinessObject {" +
                "id='" + id + '\'' +
                "text='" + text + '\'' +
                ", category=" + category +
                '}';
    }
}
