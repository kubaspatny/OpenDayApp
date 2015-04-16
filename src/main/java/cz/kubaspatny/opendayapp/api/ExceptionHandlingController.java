package cz.kubaspatny.opendayapp.api;

import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 4/3/2015
 * Time: 17:00
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
public class ExceptionHandlingController {

    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public String handleDataAccessException(HttpServletResponse response, DataAccessException e) {
        switch (e.getErrorCode()){
            case INSTANCE_NOT_FOUND:
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return "Instance not found! " + e.getLocalizedMessage();
            case ILLEGAL_ARGUMENT:
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return "Bad request! "+ e.getLocalizedMessage();
            default:
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return "Oops! Something went wrong..";
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(HttpServletResponse response, AccessDeniedException e) {
        return "Access denied! Seems like you're trying to access something that's not yours..";
    }

}
