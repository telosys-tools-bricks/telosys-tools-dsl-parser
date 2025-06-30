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
package org.telosys.tools.dsl.parser.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.telosys.tools.dsl.commons.ModelInfo;

/**
 * Root class for a Domain Model built after DSL files parsing
 *
 * @author Laurent GUERIN
 */
public class DomainModel {

	private final String modelName ; // v 3.4.0
	
	/**
	 * Model description and information (from the ".model" file)
	 */
	private final ModelInfo modelInfo ;

    /**
     * Map of all the entities (key is the entity name) 
     * (unicity for the future "Enum" type that must not collide with entity name
     */
    private final Map<String, DomainEntity> entities = new HashMap<>();

    public DomainModel(String modelName, ModelInfo modelInfo) {
    	super();
    	this.modelName = modelName;
        this.modelInfo = modelInfo;
    }

    public ModelInfo getModelInfo() {
    	return this.modelInfo;
    }
    
    public final String getModelName() {
        return modelName;
    }

	//---------------------------------------------------------------------
	// Model ENTITIES 
	//---------------------------------------------------------------------

    /**
     * Stores a new entity <br>
     * Supposed to be called once for each entity
     *
     * @param entity
     */
    public final void addEntity(DomainEntity entity) {
    	if ( entities.get(entity.getName())  != null ) {
    		throw new IllegalStateException("Entity '" + entity.getName() + "' allready defined");
    	}
    	else {
            entities.put(entity.getName(), entity);
    	}
    }

    /**
     * Returns an entity for the given name (or null if not found)
     *
     * @param entityName
     * @return
     */
    public final DomainEntity getEntity(String entityName) {
        return entities.get(entityName);
    }

    public final void setEntity(DomainEntity entity) {
        entities.put(entity.getName(), entity);
    }

    /**
     * Return entities
     * @return entities
     */
    public Collection<DomainEntity> getEntities() {
		return entities.values();
	}

    /**
     * Returns the number of entities currently defined in the model
     *
     * @return
     */
    public final int getNumberOfEntities() {
        return entities.size();
    }

    /**
     * Returns all the entity names (in alphabetical order)
     *
     * @return
     */
    public final List<String> getEntityNames() {
        List<String> names = new LinkedList<>(entities.keySet());
        Collections.sort(names);
        return names;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model : ");
        sb.append(this.getModelName());
        sb.append("\n");
        sb.append("Entities : \n");
    	for (Map.Entry<String, DomainEntity> e : entities.entrySet()) {
    		DomainEntity entity = e.getValue();
            sb.append(" . ");
            sb.append(entity.getName());
            sb.append(" : ");
            sb.append(entity.getNumberOfFields());
            sb.append(" field(s) ");
            sb.append("\n");
    	}
    	return sb.toString();
    }
}