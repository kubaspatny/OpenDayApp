package cz.kubaspatny.opendayapp.dto;

import cz.kubaspatny.opendayapp.bo.GroupSize;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:13
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
public class GroupSizeDto extends BaseDto {

    private Long groupId;
    private DateTime timestamp;
    private int size;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Maps BO to DTO leaving variables specified in @ignoredProperties set to null.
     * @param source Object to be mapped to DTO
     * @param target Object to be mapped from BO to
     * @param ignoredProperties names of variables to be ignored
     * @return
     */
    public static GroupSizeDto map(GroupSize source, GroupSizeDto target, List<String> ignoredProperties){

        target.id = source.getId();
        target.groupId = source.getGroup().getId();
        target.timestamp = source.getTimestamp();
        target.size = source.getSize();

        return target;
    }

    public static GroupSize map(GroupSizeDto source, GroupSize target, List<String> ignoredProperties){
        target.setSize(source.getSize());
        target.setTimestamp(source.getTimestamp());
        return target;
    }


    @Override
    public String getACLObjectIdentityClass() {
        return GroupSize.class.getName();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GroupSizeDto{");
        sb.append("id=").append(id);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
