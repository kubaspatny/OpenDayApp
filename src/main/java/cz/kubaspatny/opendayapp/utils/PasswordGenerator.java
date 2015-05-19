package cz.kubaspatny.opendayapp.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 23:29
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
public class PasswordGenerator {

    private static Random random = new Random();
    private static final String alphabet = "abcdefghijklmnopqrstuvxyz0123456789";
    private static final String alphabetStrong = "abcdefghijklmnopqrstuvxyz0123456789~!@#$%^&*()";

    /**
     * Generates a pseudorandom string of given lenght using characters from @see alphabet,
     * which can be user as a password for generated users.
     */
    public static String generatePassword(int length){
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return sb.toString();
    }

    /**
     * Generates a pseudorandom string of given lenght using characters from @see alphabetStrong,
     * which can be user as a password for generated users.
     */
    public static String generateStrongPassword(int length){
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(alphabetStrong.charAt(random.nextInt(alphabetStrong.length())));
        }

        return sb.toString();
    }

}
