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
package org.telosys.tools.dsl.parser.commons;

import org.telosys.tools.commons.StrUtil;

public class FkElementBuilder {
	
	private class RefParts {
		private final String referencedEntityName ;
		private final String referencedFieldName ;
		
		protected RefParts(String referencedEntityName, String referencedFieldName) {
			super();
			this.referencedEntityName = referencedEntityName.trim();
			this.referencedFieldName = referencedFieldName.trim();
		}

		protected String getReferencedEntityName() {
			return referencedEntityName;
		}

		protected String getReferencedFieldName() {
			return referencedFieldName;
		}
		
	}

	private final String entityName ; 
	
	/**
	 * Constructor
	 * @param entityName
	 */
	public FkElementBuilder(String entityName) {
		super();
		this.entityName = entityName;
	}

	public FkElement build(String s) throws ParamError {
		String[] parts = StrUtil.split(s, ',');
		switch(parts.length) {
		case 1 :
			return buildFkElementWithoutName(parts[0]);
		case 2 :
			return buildFkElementWithName(parts[0], parts[1]);
		default :
			throw new ParamError("invalid parameters (1 or 2 param expected)");
		}
	}
	
	/**
	 * Builds a FK element without name specified in the annotation => use the default FK name
	 * @param ref
	 * @return
	 * @throws ParamError
	 */
	private FkElement buildFkElementWithoutName(String ref) throws ParamError {
		// Parse reference part
		RefParts refParts = parseRef(ref);
		// Build default FK name 
		String fkName = buildDefaultFKName(refParts);	
		return new FkElement(fkName, refParts.getReferencedEntityName(), refParts.getReferencedFieldName() );
	}
	
	/**
	 * Builds a FK element with a name specified before ',' in the annotation
	 * @param fkNameParam
	 * @param ref
	 * @return
	 * @throws ParamError
	 */
	private FkElement buildFkElementWithName(String fkNameParam, String ref) throws ParamError {
		// Parse reference part
		RefParts refParts = parseRef(ref);
		// Check FK name not empty
		String fkName = fkNameParam.trim();
		if ( fkName.isEmpty() ) {
			fkName = buildDefaultFKName(refParts);
		}
		return new FkElement(fkName, refParts.getReferencedEntityName(), refParts.getReferencedFieldName() );
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
	 * @throws ParamError
	 */
	private RefParts parseRef(String s) throws ParamError {
		if ( StrUtil.nullOrVoid(s)) {
			throw new ParamError("parameter expected (at least entity name)" ) ;
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
			throw new ParamError("invalid reference parameter : '" + s + "' (Entity[.field] expected)");
		}
	}
	
	private RefParts buildRefParts(String referencedEntityName, String referencedFieldName) throws ParamError {
		if ( StrUtil.nullOrVoid(referencedEntityName)) {
			throw new ParamError("referenced entity name expected");
		}
		return new RefParts(referencedEntityName, referencedFieldName);
	}
}
