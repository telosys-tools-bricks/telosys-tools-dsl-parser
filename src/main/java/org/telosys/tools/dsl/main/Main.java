/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl.main;

import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.loader.ModelLoader;
import org.telosys.tools.generic.model.Model;

public class Main {

    public static void main(String[] args) {

        // check argument exist
        if (args == null) {
            throw new EntityParserException("No file given");
        }

        if (args.length != 1) {
            throw new EntityParserException("A single parameter is required");
        }

        String dslFolder = args[0];

        ModelLoader modelLoader = new ModelLoader();
        Model model = modelLoader.loadModel(dslFolder);
        System.out.println("Model loaded (" + model.getEntities().size() + " entities)");
    }

}
