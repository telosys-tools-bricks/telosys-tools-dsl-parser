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
package org.telosys.tools.dsl.converter;

import java.util.Collection;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

public class AnnotationsApplicator {

	private final DslModel model;
	
	public AnnotationsApplicator(DslModel model) {
		this.model = model;
	}

	/**
	 * Apply the given annotations to an attribute
	 * @param entity
	 * @param attribute
	 * @param annotations
	 */
	public void applyAnnotationsToAttribute(DslModelEntity entity, DslModelAttribute attribute, Collection<DomainAnnotation> annotations) {
		for (DomainAnnotation annotation : annotations) {
			annotation.applyToAttribute(model, entity, attribute);
		}		
	}

	/**
	 * Apply the given annotations to a link
	 * @param entity
	 * @param link
	 * @param annotations
	 */
	public void applyAnnotationsToLink(DslModelEntity entity, DslModelLink link, Collection<DomainAnnotation> annotations) {
		for (DomainAnnotation annotation : annotations) {
			annotation.applyToLink(model, entity, link);
		}		
	}
}
