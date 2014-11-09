package cz.kubaspatny.opendayapp.provider;

import cz.kubaspatny.opendayapp.utils.HexUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 12:50
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

@Component("hashProvider")
public class SHA256Provider implements HashProvider {

    @Override
    public String hash(String value) {
        try {

            return hash(value.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public String hash(byte[] value) {

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(value);
            return HexUtils.convertToHex(messageDigest.digest());

        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public String hash(String password, String salt) {
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(salt.getBytes("UTF-8"));
            return HexUtils.convertToHex(messageDigest.digest(password.getBytes("UTF-8")));

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
