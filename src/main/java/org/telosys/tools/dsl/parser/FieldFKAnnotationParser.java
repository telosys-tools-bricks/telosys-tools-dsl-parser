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
package org.telosys.tools.dsl.parser;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainFK;

public class FieldFKAnnotationParser {
	
	private class RefParts {
		private final String referencedEntityName ;
		private final String referencedFieldName ;
		
		public RefParts(String referencedEntityName, String referencedFieldName) {
			super();
			this.referencedEntityName = referencedEntityName.trim();
			this.referencedFieldName = referencedFieldName.trim();
		}

		public String getReferencedEntityName() {
			return referencedEntityName;
		}

		public String getReferencedFieldName() {
			return referencedFieldName;
		}
		
	}

	private static final String ANNOTATION_NAME = AnnotationName.FK ; 

	private final String entityName ; 
	private final String fieldName ; 
	
	/**
	 * Constructor
	 * @param entityName
	 * @param fieldName
	 */
	public FieldFKAnnotationParser(String entityName, String fieldName) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
	}

	/**
	 * Parse the content of the given 'FK' annotation to build the FK definition 
	 * @param annotation
	 * @return
	 * @throws AnnotationOrTagError
	 */
	public DomainFK parse(DomainAnnotation annotation) throws AnnotationOrTagError {
		if ( ! ANNOTATION_NAME.equals(annotation.getName()) ) {
			throw new AnnotationOrTagError(entityName, fieldName, annotation.getName(), "unexpected annotation name");
		}
		String s = annotation.getParameterAsString();
		if ( StrUtil.nullOrVoid(s)) {
			throw new AnnotationOrTagError(entityName, fieldName, ANNOTATION_NAME, "parameter expected");
		}
		String[] parts = StrUtil.split(s, ',');
		switch(parts.length) {
		case 1 :
			return buildFKWithoutName(parts[0]);
		case 2 :
			return buildFKWithName(parts[0], parts[1]);
		default :
			throw new AnnotationOrTagError(entityName, fieldName, ANNOTATION_NAME, "invalid parameter (0 or 1 ',' expected)");
		}
	}
	
	/**
	 * Builds a FK without name specified in the annotation => use the default FK name
	 * @param ref
	 * @return
	 * @throws AnnotationOrTagError
	 */
	private DomainFK buildFKWithoutName(String ref) throws AnnotationOrTagError {
		// Parse reference part
		RefParts refParts = parseRef(ref);
		// Build default FK name 
		String fkName = buildDefaultFKName(refParts);	
		return new DomainFK(fkName, refParts.getReferencedEntityName(), refParts.getReferencedFieldName() );
	}
	
	/**
	 * Builds a FK with a name specified before ',' in the annotation
	 * @param fkNameParam
	 * @param ref
	 * @return
	 * @throws AnnotationOrTagError
	 */
	private DomainFK buildFKWithName(String fkNameParam, String ref) throws AnnotationOrTagError {
		// Parse reference part
		RefParts refParts = parseRef(ref);
		// Check FK name not empty
		String fkName = fkNameParam.trim();
		if ( fkName.isEmpty() ) {
			fkName = buildDefaultFKName(refParts);
		}
		return new DomainFK(fkName, refParts.getReferencedEntityName(), refParts.getReferencedFieldName() );
	}
	
	/**
	 * Builds default FK name
	 * @param refParts
	 * @return
	 */
	private String buildDefaultFKName(RefParts refParts) {
		return "FK_" + entityName + "_" + refParts.getReferencedEntityName() ;	
	}
	
	/**
	 * Parse the 'reference' part of the annotation 
	 * @param s FK reference ('Entity' or 'Entity.field' expected )
	 * @return
	 * @throws AnnotationOrTagError
	 */
	protected RefParts parseRef(String s) throws AnnotationOrTagError {
		if ( StrUtil.nullOrVoid(s)) {
			throw new AnnotationOrTagError(entityName, fieldName, ANNOTATION_NAME, 
					"reference parameter expected (at least entity name)");
		}
		String[] parts = StrUtil.split(s, '.');
		switch(parts.length) {
		case 1 :
			// 1 part only : Referenced entity name ( no Referenced field name )
			return buildRefParts(parts[0], "");
		case 2 :
			// 2 parts : Referenced entity name + Referenced field name)
			return buildRefParts(parts[0], parts[1]);
		default :
			throw new AnnotationOrTagError(entityName, fieldName, ANNOTATION_NAME, 
					"invalid reference parameter : '" + s + "' (0 or 1 '.' expected)");
		}
	}
	
	private RefParts buildRefParts(String referencedEntityName, String referencedFieldName) throws AnnotationOrTagError {
		if ( StrUtil.nullOrVoid(referencedEntityName)) {
			throw new AnnotationOrTagError(entityName, fieldName, ANNOTATION_NAME, 
					"referenced entity name expected");
		}
		return new RefParts(referencedEntityName, referencedFieldName);
	}
}
