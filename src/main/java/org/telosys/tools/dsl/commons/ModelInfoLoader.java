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
package org.telosys.tools.dsl.commons;

import java.io.File;
import java.util.Map;

import org.telosys.tools.commons.YamlFileManager;
import org.telosys.tools.commons.exception.TelosysYamlException;
import org.telosys.tools.dsl.DslModelError;

public class ModelInfoLoader {

	/**
	 * Private constructor
	 */
	private ModelInfoLoader() {
	}

	/**
	 * Loads model information from the given YAML file if the file exists <br>
	 * if the file doesn't exist a void ModelInfo is returned 
	 * @param modelYamlFile
	 * @return model information load from YAML file 
	 * @throws DslModelError 
	 */
	public static ModelInfo loadModelInformation(File modelYamlFile) throws DslModelError {
		ModelInfo modelInfo = new ModelInfo();
		if ( modelYamlFile.exists() && modelYamlFile.isFile() ) {
			YamlFileManager yaml = new YamlFileManager(modelYamlFile);
			try {
				Map<String, Object> map = yaml.loadMap();
				if ( map != null ) {
					modelInfo.setDescription(asString( map.get(ModelInfo.DESCRIPTION) ) ); 
					modelInfo.setTitle(      asString( map.get(ModelInfo.TITLE      ) ) ); 
					modelInfo.setVersion(    asString( map.get(ModelInfo.VERSION    ) ) ); 
				}
			} catch (TelosysYamlException e) {
				throw new DslModelError("Invalid model file : YAML error" );
			}
		}
		return modelInfo;
	}

	/**
	 * Converts the given value to String 
	 *   null -> ""
	 *   ""   -> ""
	 *   1.2  -> "1.2"
	 *   true -> "true"
	 * @param value
	 * @return
	 */
	private static String asString(Object value) {
		if ( value != null ) {
			return String.valueOf(value);
		}
		else {
			return "";
		}
	}
}
