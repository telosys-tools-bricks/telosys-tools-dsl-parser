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
package org.telosys.tools.dsl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.enums.ModelType;
import org.telosys.tools.generic.model.util.EntityClassNameComparator;

public class DslModel implements Model {
	
	private static final ModelType MODEL_TYPE    = ModelType.DOMAIN_SPECIFIC_LANGUAGE ;

	private final String modelName;

	// from model.yaml
	private final ModelInfo modelInfo;
	
	//private Integer databaseId;
	//private String databaseProductName	;
	private String databaseId	;
	private String databaseName	;
	private String databaseType ;
	
	private List<Entity> entities = new ArrayList<>();

	/**
	 * Constructor
	 * @param modelName  model name 
	 * @param modelInfo  model information 
	 */
	public DslModel(String modelName, ModelInfo modelInfo) {
		super();
		if ( StrUtil.nullOrVoid(modelName) ) {
			throw new IllegalArgumentException("Model name is undefined (null or void)");
		}
		this.modelName = modelName;
		this.modelInfo = modelInfo;
	}
	
	/**
	 * Constructor with default model information
	 * @param modelName  model name 
	 */
	public DslModel(String modelName) {
		this(modelName, new ModelInfo());
	}
	
	@Override
	public String getName() {
		// Model name not defined in model properties => use name from file
		return modelName ;
	}

	@Override
	public String getFolderName() {  // v 3.3.0
		// return DslModelUtil.getModelFolderName(modelName);
		// since v 3.4.0 the folder name is the model name
		return modelName ;
	}

	@Override
	public ModelType getType() {
		return MODEL_TYPE;
	}
	
	//----------------------------------------------------------------------------------------
	public ModelInfo getModelInfo() {
		return modelInfo;
	}

	@Override
	public String getTitle() {
		return modelInfo.getTitle();
	}
	
	@Override
	public String getVersion() {
		return modelInfo.getVersion();
	}

	@Override
	public String getDescription() {
		return modelInfo.getDescription();
	}

	//----------------------------------------------------------------------------------------

	@Override
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	@Override
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	//----------------------------------------------------------------------------------------

	@Override
	public List<Entity> getEntities() {
		return entities;
	}

	public void addEntity(Entity entity) { // v 3.4.0
		entities.add(entity);
	}

	@Override
	public Entity getEntityByClassName(String entityClassName) {
		if ( entityClassName == null ) {
			throw new IllegalArgumentException("getEntityByClassName() : entityClassName is null");
		}
		for(Entity entity : getEntities()) {
			if ( entityClassName.equals(entity.getClassName()) ) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public Entity getEntityByTableName(String entityTableName) {
		if ( entityTableName == null ) {
			throw new IllegalArgumentException("getEntityByTableName() : entityTableName is null");
		}
		for(Entity entity : getEntities()) {
			if ( entityTableName.equals(entity.getDatabaseTable() ) ) {
				return entity;
			}
		}
		return null;
	}

	public void sortEntitiesByClassName() {
		Collections.sort(entities, new EntityClassNameComparator() ) ;
	}
	
}
