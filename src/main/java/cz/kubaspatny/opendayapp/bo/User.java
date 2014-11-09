package cz.kubaspatny.opendayapp.bo;

import cz.kubaspatny.opendayapp.provider.HashProvider;
import cz.kubaspatny.opendayapp.provider.SaltProvider;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 15:36
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
@Table(name = "users")
@Configurable(preConstruction = true)
public class User extends AbstractBusinessObject {

    @Autowired private transient SaltProvider saltProvider;
    @Autowired private transient HashProvider hashProvider;

    @Column(nullable = false, unique = true)
    private String username;

    /**
     * This variable represents SHA-256 hashed and salted password.
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    private String firstName;
    private String lastName;
    private String organization;

    @Column(nullable = false)
    private boolean userEnabled = false;

    /**
     * User account can be expirable, which means it has set values @validFrom and @validTo.
     * The system does periodic checks on expirable accounts, if @user_account_expirable is
     * equal to true there are several possible outcomes:
     * - current date is before validFrom -> no change of user attributes
     * - current date is equal to validFrom -> @user_enabled is set to true
     * - current date is after validTo -> @user_enabled is set to false and @user_account_expirable is to to false
     */
    @Column(nullable = false)
    private boolean userAccountExpirable = false;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime validFrom = null;
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime validTo = null;

    // ------------------- GETTER AND SETTERS -------------------

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * When a user sets a password, a new securely random salt is generated and hashed.
     * The provided is password is prepended with the salt and hashed using SHA-256 algorithm.
     * This hash is then saved as a user's password.
     * @param password
     */
    public void setPassword(String password) {
        setSalt(hashProvider.hash(saltProvider.generateSalt(32)));
        this.password = hashProvider.hash(password, salt);
    }

    public String getSalt() {
        return salt;
    }

    private void setSalt(String salt) {
        this.salt = salt;
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

    public boolean isUserEnabled() {
        return userEnabled;
    }

    public void setUserEnabled(boolean userEnabled) {
        this.userEnabled = userEnabled;
    }

    public boolean isUserAccountExpirable() {
        return userAccountExpirable;
    }

    public void setUserAccountExpirable(boolean userAccountExpirable) {
        this.userAccountExpirable = userAccountExpirable;
    }

    public DateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(DateTime validFrom) {
        this.validFrom = validFrom;
    }

    public DateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(DateTime validTo) {
        this.validTo = validTo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id='").append(id).append('\'');
        sb.append("username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", salt='").append(salt).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", organization='").append(organization).append('\'');
        sb.append(", userEnabled=").append(userEnabled);
        sb.append(", userAccountExpirable=").append(userAccountExpirable);
        sb.append(", validFrom=").append(validFrom);
        sb.append(", validTo=").append(validTo);
        sb.append('}');
        return sb.toString();
    }
}
