package cz.kubaspatny.opendayapp.exception;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 1/11/2014
 * Time: 17:20
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
public class DaoException extends Exception {

    public enum DaoErrorCode {
        INVALID_ID, DETACHED_INSTANCE, ILLEGAL_ARGUMENT;
    }

    private DaoErrorCode errorCode;

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, DaoErrorCode code) {
        super(message);
        errorCode = code;
    }

    public DaoErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(DaoErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}