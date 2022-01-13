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

import java.util.Map;

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainTag;
import org.telosys.tools.dsl.tags.Tag;
import org.telosys.tools.dsl.tags.TagError;
import org.telosys.tools.dsl.tags.Tags;

public class TagsConverter {
	
	private final DslModelErrors  errors;
	
	public TagsConverter(DslModelErrors errors) {
		super();
		this.errors = errors;
	}


//	private Map<String,String> buildTags(DomainField field) { // new in v 3.4.0
//		Map<String, DomainTag> tagsMap = field.getTags();
//		if ( tagsMap == null) return null;
//		if ( tagsMap.isEmpty()) return null;
//		// Convert tags
//		Map<String,String> newTags = new HashMap<>();
//		for ( DomainTag tag : tagsMap.values() ) {
//			String name = tag.getName() ;
////			String value = tag.getParameterAsString();
////			if ( value == null ) {
////				value = "";
////			}
//			// v 3.4.0
//			String value = "";
//			if ( tag.hasParameter() ) {
////				value = tag.getParameterAsString();
//				value = tag.getParameter();
//			}
//			newTags.put(name, value);
//		}
//		return newTags;
//	}
//	
	private Tags buildTags(Map<String, DomainTag> tagsMap, String entityName, String fieldName) {
		Tags tags = new Tags(); // void tags collection
		if ( tagsMap == null) return tags;
		if ( tagsMap.isEmpty()) return tags;
		for ( DomainTag rawTag : tagsMap.values() ) {
			try {
				tags.addTag( new Tag(rawTag.getName(), rawTag.getParameter()));
			} catch (TagError e) {
				errors.addError(
					new DslModelError( entityName, fieldName, e.getMessage() ) );
			}
		}
		return tags;
	}
	
	public void applyTagsToEntity(DslModelEntity dslEntity, DomainEntity entity) {
		dslEntity.setTagContainer(buildTags(entity.getTags(), entity.getName(), null));
	}
	
	/**
	 * Apply tags to the given attribute
	 * @param dslEntity
	 * @param dslAttribute
	 * @param field
	 */
	public void applyTagsToAttribute(DslModelEntity dslEntity, DslModelAttribute dslAttribute, DomainField field) {
		dslAttribute.setTagContainer(buildTags(field.getTags(), dslEntity.getClassName(), field.getName()));
	}

	/**
	 * Apply tags to the given link
	 * @param dslEntity
	 * @param dslLink
	 * @param field
	 */
	public void applyTagsToLink(DslModelEntity dslEntity, DslModelLink dslLink, DomainField field) {
		dslLink.setTagContainer( buildTags(field.getTags(), dslEntity.getClassName(), field.getName()) );
	}
}
