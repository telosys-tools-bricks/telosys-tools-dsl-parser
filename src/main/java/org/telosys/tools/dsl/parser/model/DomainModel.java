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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.telosys.tools.dsl.DslParserException;

/**
 * Root class for a Domain Model built after Domain Specific Language text file parsing
 *
 * @author L.Guerin
 */
public class DomainModel {

	private final DomainModelInfo domainModelInfo ;

    /*
    NB :
        Do not accept an entity and an enumeration with the same name /!\
     */
    private final Map<String, DomainEntity> entities = new Hashtable<>();

//    private final Map<String, DomainEnumeration<?>> enumerations = new Hashtable<String, DomainEnumeration<?>>();

    /**
     * Constructor
     */
    public DomainModel() {
        super();
        domainModelInfo = new DomainModelInfo();
    }

    /**
     * Constructor
     * @param properties
     */
    public DomainModel(Properties properties) {
    	super();
        domainModelInfo = new DomainModelInfo(properties);
    }

    /**
     * Returns the model name
     *
     * @return
     */
    public final String getName() {
        return domainModelInfo.getName();
    }

    
    /**
     * Returns the model version
     * @return
     */
    public String getVersion() {
		return domainModelInfo.getVersion();
	}

	/**
     * Returns the model description
	 * @return
	 */
	public String getDescription() {
		return domainModelInfo.getDescription();
	}

	private final void checkName(String name) {
        // Do not accept an entity/enumeration with a "neutral type" name
        if (DomainNeutralTypes.exists(name)) {
            throw new DslParserException("Reserved name '" + name + "' (neutral type)");
        }

        // Do not accept an entity and an enumeration with the same name /!\
        if (entities.get(name) != null) {
            throw new DslParserException("An entity already exists with name '" + name + "'");
        }
//        if (enumerations.get(name) != null) {
//            throw new EntityParserException("An enumeration already exists with name '" + name + "'");
//        }
    }

    /*------------------------------------------------------------------------------------------
     ENTITIES
    ------------------------------------------------------------------------------------------*/

    /**
     * Stores a new entity <br>
     * Supposed to be called once for each entity
     *
     * @param entity
     */
    public final void addEntity(DomainEntity entity) {
        checkName(entity.getName());
        entities.put(entity.getName(), entity);
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
        List<String> names = new LinkedList<String>(entities.keySet());
        Collections.sort(names);
        return names;
    }

//    /*------------------------------------------------------------------------------------------
//     ENUMERATION
//    ------------------------------------------------------------------------------------------*/
//
//    /**
//     * Stores a new enumeration <br>
//     * Supposed to be called once for each enumeration
//     *
//     * @param enumeration
//     */
//    public final void addEnumeration(DomainEnumeration<?> enumeration) {
//        checkName(enumeration.getName());
//        enumerations.put(enumeration.getName(), enumeration);
//    }
//
//    /**
//     * Returns an enumeration for the given name (or null if not found)
//     *
//     * @param enumerationName
//     * @return
//     */
//    public final DomainEnumeration<?> getEnumeration(String enumerationName) {
//        return enumerations.get(enumerationName);
//    }
//
//    /**
//     * Returns the number of enumerations currently defined in the model
//     *
//     * @return
//     */
//    public final int getNumberOfEnumerations() {
//        return enumerations.size();
//    }
//
//    /**
//     * Returns all the enumeration names (in alphabetical order)
//     *
//     * @return
//     */
//    public final List<String> getEnumerationNames() {
//        List<String> names = new LinkedList<String>(enumerations.keySet());
//        Collections.sort(names);
//        return names;
//    }

    /*------------------------------------------------------------------------------------------
     ALL
    ------------------------------------------------------------------------------------------*/

    /**
     * Init the entity fields from the given list
     *
     * @param entity
     */
    public void populateEntityFileds(String entityName, List<DomainField> fields) {
    	DomainEntity destinationEntity = getEntity(entityName);
        //entity.addAllFields(getEntity(entity.getName()));
        if (destinationEntity != null) {
            for (DomainField field : fields) {
            	destinationEntity.addField(field);
            }
        }
        else {
            throw new DslParserException("Cannot popumate entity '" + entityName + "' (not found in model).");
        }
    }

    @Override
    public String toString() {
    	String enumerationsString= "";
    	
//    	for (String mapKey : enumerations.keySet()) {
//    		enumerationsString += "\n\t "+mapKey + enumerations.get(mapKey);
//    	}
    	
    	String entitiesString= "";
    	
    	for (String mapKey : entities.keySet()) {
    		entitiesString += "\n\t" + entities.get(mapKey);
    	}
        return this.getName()+"[\n"
        				+ "entities=" + entitiesString + ","
        						+ "\n enumerations=" + enumerationsString + "]";
    }

    @Override
    public int hashCode() {
    	String modelName = this.getName();
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entities == null) ? 0 : entities.hashCode());
        //result = prime * result + ((enumerations == null) ? 0 : enumerations.hashCode());
        result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DomainModel other = (DomainModel) obj;
        if (entities == null) {
            if (other.entities != null) {
                return false;
            }
        } else if (!entities.equals(other.entities)) {
            return false;
        }
//        if (enumerations == null) {
//            if (other.enumerations != null) {
//                return false;
//            }
//        } else if (!enumerations.equals(other.enumerations)) {
//            return false;
//        }
        String thisModelName = this.getName();
        String otherModelName = other.getName();
        if (thisModelName == null) {
            if (otherModelName != null) {
                return false;
            }
        } else if (!thisModelName.equals(otherModelName)) {
            return false;
        }
        return true;
    }

}
