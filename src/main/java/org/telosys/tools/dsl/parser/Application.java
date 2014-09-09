/**
 * Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl.parser;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainModel;

/**
 * @author Jonhathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 * @date 2014-05-22
 */
public final class Application {

    private Application(){}
	static Logger logger = LoggerFactory.getLogger(Application.class);
    /**
     * Call parser from cli
     *
     * @param args
     */
    public static void main(String[] args) {
        // check argument exist
        if (args == null) {
            throw new EntityParserException("No file given");
        }

        if (args.length != 1) {
            throw new EntityParserException("A single parameter is required");
        }
        // call parser tool
        DomainModelParser dm = new DomainModelParser();
        DomainModel model = dm.parse(new File(args[0]));
        logger.info("\n"+model.toString());
    }
}
