/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.dsl.parser.utils;

import org.telosys.tools.dsl.DslParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StringUtils {
    private StringUtils(){}
    /**
     * Read content from a file
     *
     * @param inputStream
     * @return The content
     */
    public static String readStream(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder ret = new StringBuilder();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                ret.append(line + "\n");
            }
        } catch (IOException e) {
            throw new DslParserException("Error while reading the stream : " + e.getMessage()
                    + "\n Documentation : " + e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new DslParserException("Error while closing the stream : " + e.getMessage()
                        + "\n Documentation : " + e);
            }
        }

        return ret.toString();
    }
}
