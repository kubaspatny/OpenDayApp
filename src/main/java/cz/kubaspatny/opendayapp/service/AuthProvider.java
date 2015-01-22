package cz.kubaspatny.opendayapp.service;

import cz.kubaspatny.opendayapp.bo.User;
import cz.kubaspatny.opendayapp.dao.GenericDao;
import cz.kubaspatny.opendayapp.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 19/1/2015
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
 */
public class AuthProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AuthProvider.class);
    private GenericDao genericDao;
    private TransactionTemplate transactionTemplate;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        return (Authentication) transactionTemplate.execute(new TransactionCallback() {

            @Override
            public Object doInTransaction(TransactionStatus status) {

                try {

                    UsernamePasswordAuthenticationToken auth = null;
                    User u;

                    try {
                        u = genericDao.getByPropertyUnique("username", authentication.getPrincipal(), User.class);
                    } catch (DataAccessException e){
                        throw new BadCredentialsException("Wrong username or password.");
                    }

                    String password = (String) authentication.getCredentials();

                    if(u == null || !u.isLoginCorrect(password)){
                        throw new BadCredentialsException("Wrong username or password.");
                    } else {

                        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

                        if(u.getUserRoles() != null){
                            for(User.UserRole role : u.getUserRoles()){
                                grantedAuthorities.add(new SimpleGrantedAuthority(role.name()));
                            }
                        }

                        auth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), grantedAuthorities);

                    }

                    return auth;

                } catch (AuthenticationException e){
                    status.setRollbackOnly();
                    throw e;
                } catch (Exception e){
                    LOG.error("Error occured during authenticate call!", e);
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }

            }
        });

    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return true;
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}