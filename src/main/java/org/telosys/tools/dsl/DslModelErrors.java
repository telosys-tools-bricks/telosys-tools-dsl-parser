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
package org.telosys.tools.dsl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;

public class DslModelErrors {

	// Entity name --> EntityParsingError
//	private final Map<String, EntityParsingError> errors = new TreeMap<>();
	private final Map<String, List<String> > errors = new TreeMap<>();

	/**
	 * COntructor without errors
	 */
	public DslModelErrors() {
		super();
	}

	/**
	 * Constructor with errors
	 */
	public DslModelErrors(List<EntityParsingError> entityParsingErrors) {
		super();
    	for ( EntityParsingError e : entityParsingErrors ) {
    		//String entityName = e.getEntityName();
    		//errors.put(entityName, e);
    		errors.put(e.getEntityName(), buildEntityErrors(e));
    	}
	}

	private List<String> buildEntityErrors(EntityParsingError e) {
		String entityName = e.getEntityName();		
		List<String> list = new LinkedList<>();

		// Entity error if any 
		String entityError = e.getError() ;
		if ( entityError != null ) {
			StringBuilder sb = new StringBuilder();
			sb.append("[").append(entityName).append("]");
			sb.append(" : entity error : ").append(entityError);
			list.add(sb.toString());
		}
		
		// Fields errors if any 
		for ( FieldParsingError fpe : e.getFieldsErrors() ) {
			StringBuilder sb = new StringBuilder();
			sb.append("[").append(entityName).append("]");
			sb.append(" : field '").append(fpe.getFieldName()).append("' : ").append(fpe.getError());
			list.add(sb.toString());
		}
		return list.isEmpty() ? null : list ;
	}
	
	/**
	 * Returns all model errors count (for all entities)
	 * @return
	 */
	public int getAllErrorsCount() {
		int count = 0 ;
//		for ( EntityParsingError epe : errors.values() ) {
//			count = count + epe.getErrorsCount();
//		}
		for ( List<String> list : errors.values() ) {
			count = count + list.size();
		}
		return count ;
	}

	/**
	 * Returns all model errors sorted and grouped by entity name
	 * 
	 * @return
	 */
//	public List<FieldParsingError> getAllErrors() {
//		List<FieldParsingError> all = new LinkedList<>();
//		for (Entry<String, EntityParsingError> entry : errors.entrySet()) {
//			EntityParsingError epe = entry.getValue();
//			for ( FieldParsingError fpe : epe.getFieldsErrors() ) {
//				all.add(fpe);
//			}
//		}
//		return all;
//	}

	public List<String> getAllErrors() {
		List<String> all = new LinkedList<>();
		for ( List<String> list : errors.values()) {
//			List<String> list = entry.getValue();
//			for ( String s : list ) {
//				all.add(s);
//			}
			all.addAll(list);
		}
		return all;
	}

	public List<String> getEntities() {
		List<String> entities = new LinkedList<>();
//		for (Entry<String, EntityParsingError> entry : errors.entrySet()) {
//			entities.add(entry.getKey());
//		}
		for (String entityName : errors.keySet()) {
			entities.add(entityName);
		}
		return entities;
	}

	/**
	 * Returns errors count for the given entity name
	 * 
	 * @param entityName
	 * @return
	 */
	public int getErrorsCount(String entityName) {
//		EntityParsingError epe = errors.get(entityName);
//		if (epe != null) {
//			return epe.getFieldsErrors().size();
//		}
//		return 0;
		List<String> list = errors.get(entityName);
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	/**
	 * Returns errors list for the given entity name
	 * 
	 * @param entityName
	 * @return list (void list if no error)
	 */
//	public List<FieldParsingError> getErrors(String entityName) {
//		EntityParsingError epe = errors.get(entityName);
//		if (epe != null) {
//			return epe.getFieldsErrors();
//		}
//		return new LinkedList<>();
//	}
	
	public List<String> getErrors(String entityName) {
		List<String> list = errors.get(entityName);
		if (list != null) {
			return list;
		}
		return new LinkedList<>();
	}

}
