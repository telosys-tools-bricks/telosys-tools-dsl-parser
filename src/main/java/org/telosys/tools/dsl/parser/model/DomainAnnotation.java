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

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationScope;
import org.telosys.tools.dsl.parser.annotation.Annotations;

public class DomainAnnotation extends DomainAnnotationOrTag {

    public DomainAnnotation(String name) {
    	super(name);
    }
    
    public DomainAnnotation(String name, String param) {
    	super(name,param);
    }
    
    public DomainAnnotation(String name, Number param) {
    	super(name,param);
    }
    
    public DomainAnnotation(String name, Boolean param) {
    	super(name,param);
    }
    
    @Override
    public String toString() {
    	return "@" + super.toString();
    }

	/**
	 * Apply annotation on the given attribute
	 * @param model
	 * @param entity
	 * @param attribute
	 */
	public void applyToAttribute(DslModel model, DslModelEntity entity, DslModelAttribute attribute ) {
		String annotationName = this.getName();
    	AnnotationDefinition annotationDefinition = getAnnotationDefinition(annotationName);
    	if ( annotationDefinition.getScope() == AnnotationScope.ATTRIBUTE ) {
        	annotationDefinition.apply(model, entity, attribute, this.getParameter());
    	}
    	else {
    		throw new IllegalStateException("annotation '" + annotationName + "' not applicable on an attribute" );
    	}
	}

	/**
	 * Apply annotation on the given link
	 * @param model
	 * @param entity
	 * @param link
	 */
	public void applyToLink(DslModel model, DslModelEntity entity, DslModelLink link ) {
		String annotationName = this.getName();
    	AnnotationDefinition annotationDefinition = getAnnotationDefinition(annotationName);
    	if ( annotationDefinition.getScope() == AnnotationScope.LINK ) {
    		annotationDefinition.apply(model, entity, link, this.getParameter());
    	}
    	else {
    		throw new IllegalStateException("annotation '" + annotationName + "' not applicable on a link" );
    	}
    }
	
	/**
	 * @param annotationName
	 * @return
	 */
	private AnnotationDefinition getAnnotationDefinition(String annotationName) {
    	AnnotationDefinition annotationDefinition = Annotations.get(this.getName());
    	if ( annotationDefinition != null ) {
    		return annotationDefinition ;
    	}
    	else {
    		throw new IllegalStateException("Unknown annotation '" + annotationName + "'" );
    	}
	}
}
