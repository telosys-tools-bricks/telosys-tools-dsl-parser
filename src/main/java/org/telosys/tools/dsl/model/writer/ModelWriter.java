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
package org.telosys.tools.dsl.model.writer;

import java.io.File;

import org.telosys.tools.commons.DirUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.generic.model.Entity;

/**
 * DSL model writer (to create a model and write all its files)  
 * 
 * @author Laurent Guerin
 *
 */
public class ModelWriter {

	/**
	 * Writes the given model in the given directory
	 * @param model
	 * @param modelDirectory
	 */
	public void writeModel(DslModel model, String modelDirectory) {
		
		// 1) check model directory existence (create it if not exist)
		DirUtil.createDirectory(new File(modelDirectory) );
		
		// 2) write model info file in the model directory (model.yaml)
		ModelInfoFileWriter modelInfoFilewriter = new ModelInfoFileWriter(modelDirectory);
		modelInfoFilewriter.writeModelInfoFile(model.getModelInfo());
		
		// 3) write all entities in the model directory (N xxx.entity)
		EntityFileWriter entityWriter = new EntityFileWriter(modelDirectory) ;
		for ( Entity entity : model.getEntities() ) {
			entityWriter.writeEntity((DslModelEntity) entity); 
		}
	}

}
