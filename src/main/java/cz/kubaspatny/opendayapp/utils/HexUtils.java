package cz.kubaspatny.opendayapp.utils;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 9/11/2014
 * Time: 13:25
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
public class HexUtils {

    /**
     * Returns a string hexadecimal representation of byte array @bytes.
     */
    public static String convertToHex(byte[] bytes){

        StringBuffer hexResult = new StringBuffer();

        for (int i=0; i<bytes.length; i++) {

            String temp = Integer.toHexString(0xff & bytes[i]);

            if(temp.length()==1) hexResult.append('0');
            hexResult.append(temp);
        }

        return hexResult.toString();
    }
}
