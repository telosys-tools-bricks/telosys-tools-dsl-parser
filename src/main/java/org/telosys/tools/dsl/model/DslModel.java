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

import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.ModelType;
import org.telosys.tools.generic.model.util.EntityClassNameComparator;

public class DslModel implements Model {
	
	private static final ModelType MODEL_TYPE    = ModelType.DOMAIN_SPECIFIC_LANGUAGE ;
	private static final String    MODEL_VERSION = DslModelVersion.VERSION ;

	private String name = "";
	private String description = "";
	private Integer databaseId;
	private String databaseProductName	;
	private List<Entity> entities = new ArrayList<>();

	@Override
	public Entity getEntityByClassName(String entityClassName) {
		for(Entity entity : getEntities()) {
			if ( entityClassName.equals(entity.getClassName()) ) {
				return entity;
			}
		}
		return null;
	}
	@Override
	public Entity getEntityByTableName(String entityTableName) {
		for(Entity entity : getEntities()) {
			if ( entityTableName.equals(entity.getDatabaseTable() ) ) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getVersion() {
		return MODEL_VERSION;
	}

	@Override
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ModelType getType() {
		return MODEL_TYPE;
	}
	
	@Override
	public Integer getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(Integer databaseId) {
		this.databaseId = databaseId;
	}

	@Override
	public String getDatabaseProductName() {
		return databaseProductName;
	}
	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	@Override
	public List<Entity> getEntities() {
		return entities;
	}

	public void sortEntitiesByClassName() {
		Collections.sort(entities, new EntityClassNameComparator() ) ;
	}
	
}
