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
package org.telosys.tools.dsl.parser.annotations;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.commons.JoinAttributesBuilder;
import org.telosys.tools.dsl.commons.ReferenceDefinition;
import org.telosys.tools.dsl.commons.ReferenceDefinitions;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.commons.ParamError;

public abstract class LinkByAnnotation extends AnnotationDefinition {

	protected LinkByAnnotation(String name, AnnotationParamType type) {
		super(name, type, AnnotationScope.LINK);
	}

//	protected JoinColumnsBuilder getJoinColumnsBuilder() {
//		return new JoinColumnsBuilder("@"+this.getName()) ;
//	}
	protected JoinAttributesBuilder getJoinAttributesBuilder() {
		return new JoinAttributesBuilder("@"+this.getName()) ;
	}

//	protected ReferenceDefinitions getReferenceDefinitions(String paramValue) {
//		ReferenceDefinitions rd = buildReferenceDefinitions(paramValue);
//		checkReferenceDefinitions(rd);
//		return rd;
//	}

	/**
	 * Builds all references definitions (for columns references or attributes references)<br>
	 * No consistency check at this level (just build)
	 * Input examples : <br>
	 *   "col1",  "col1>refCol1 , col2 > refCol2 " <br>
	 *   "attr1", "att1>refAtt1 , att2 > refAtt2 " <br>
	 * @param s  
	 * @return
	 */
	protected ReferenceDefinitions buildReferenceDefinitions(String s) {
		ReferenceDefinitions refDefinitions = new ReferenceDefinitions();
		if ( ! StrUtil.nullOrVoid(s) ) {
			String[] parts = StrUtil.split(s, ',');
			for ( String part : parts ) {
				if ( ! StrUtil.nullOrVoid(part) ) {
					if ( part.contains(">") ) {
						// "name > referencedName "
						String[] pair = StrUtil.split(part, '>');
						String name = "" ;
						String referencedName = "";
						if ( pair.length > 0 ) {
							name = pair[0].trim();
						}
						if ( pair.length > 1 ) {
							referencedName = pair[1].trim();
						}
						if ( ! name.isEmpty() ) {
							refDefinitions.add( new ReferenceDefinition(name, referencedName));
						}
					}
					else {
						// "name" only (without referencedName)
						String name = part.trim();
						refDefinitions.add( new ReferenceDefinition(name));
					}
				}
			}
		}
		return refDefinitions;
	}
	
	/**
	 * Check there's at least 1 reference defined
	 * @param referenceDefinitions
	 */
	protected void checkNotVoid(DslModelEntity entity, DslModelLink link, ReferenceDefinitions referenceDefinitions) throws ParamError {
		if ( referenceDefinitions.count() == 0 ) {
			throw newParamError(entity, link, "no reference definition");
		}
	}
	
	/**
	 * Check referenced names are defined if more than one reference
	 * @param referenceDefinitions
	 */
	protected void checkReferencedNames(DslModelEntity entity, DslModelLink link, ReferenceDefinitions referenceDefinitions) throws ParamError {
		if ( referenceDefinitions.count() > 1 ) {
			int referencedCount = 0;
			for ( ReferenceDefinition rd : referenceDefinitions.getList() ) {
				if ( rd.hasReferencedName() ) {
					referencedCount++;
				}
			}
			if ( referencedCount < referenceDefinitions.count() ) {
				throw newParamError(entity, link, "missing referenced name(s)");
			}
		}
	}
}
