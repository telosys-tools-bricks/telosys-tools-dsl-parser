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

import org.telosys.tools.commons.YamlFileManager;

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
	 * @return model
	 */
	public static ModelInfo loadModelInformation(File modelYamlFile) {
		if ( modelYamlFile.exists() && modelYamlFile.isFile() ) {
			YamlFileManager yaml = new YamlFileManager();
			return yaml.load(modelYamlFile, ModelInfo.class);
		}
		else {
			return new ModelInfo() ;
		}
	}

}
