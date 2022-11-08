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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.commons.AttributeFKUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.dsl.model.DslModelForeignKeyAttribute;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyAttribute;

public class ForeignKeysBuilderV2 {

	private final DslModel model ;
	
	private Map<String,DslModelForeignKey> foreignKeys ;
	
	/**
	 * Constructor
	 * @param model
	 */
	public ForeignKeysBuilderV2(DslModel model ) {
		this.model = model;
	}

	/**
	 * Build all the Foreign Keys declared in the given entity (with '@FK' annotations)
	 * and set them in the generic entity
	 * @param entity
	 * @return
	 */
	public void buildForeignKeys(DomainEntity entity) {
		DslModelEntity dslModelEntity = (DslModelEntity) model.getEntityByClassName(entity.getName());
		if (dslModelEntity == null ) {
			throw new IllegalStateException( "Cannot found entity '" + entity.getName() + "' in model" );
		}
		// Build all entity Foreign Keys (stored in a Map)
		foreignKeys = new HashMap<>();
		if (entity.getFields() != null) {
			// for each entity field : store all defined FK (FK part or entire FK ) 
			for (DomainField field : entity.getFields()) {
				if ( field.isAttribute() ) { // FK are defined in attributes not in links
					DslModelAttribute dslModelAttribute = (DslModelAttribute) dslModelEntity.getAttributeByName(field.getName());
					if (dslModelAttribute == null ) {
						throw new IllegalStateException( "Cannot found attribute '" + field.getName() + "' in entity '" + entity.getName() + "'" );
					}
					buildForeignKeysForAttribute(dslModelEntity, dslModelAttribute, field.getFkElements());
				}
			}
		}
		// Convert Foreign Keys to List and set in entity
		for ( ForeignKey fk : foreignKeys.values() ) {
			// check if the FK is valid
			checkForeignKeyValidity(entity.getName(), fk);
			dslModelEntity.addForeignKey(fk);
			// Added 2022-02-16
			AttributeFKUtil.applyFKToAttributes(fk, model);
		}
	}

	/**
	 * Process the given field : build all FK or FK parts defined for this field
	 * @param entity
	 * @param attribute
	 * @param fkElements
	 */
	private void buildForeignKeysForAttribute(DslModelEntity entity, DslModelAttribute attribute, List<FkElement> fkElements) {
		String originEntityName = entity.getClassName();
		String entityField = originEntityName + attribute.getName();
		if ( fkElements != null ) {
			// Get all "@FK" definitions for this field 
			for ( FkElement originalFkDef : fkElements ) {
				// Complete the FK definition if necessary
				FkElement fkElement = completeFK(originEntityName, attribute.getName(), originalFkDef) ;
				// Is FK already defined 
				DslModelForeignKey fk = foreignKeys.get(fkElement.getFkName());
				if ( fk == null ) {
					// Init a new void FK in the map
					DslModelEntity referencedEntity = getReferencedEntity(entityField, fkElement);
					fk = new DslModelForeignKey(fkElement.getFkName(), originEntityName, referencedEntity.getClassName() );
					foreignKeys.put(fkElement.getFkName(), fk);
				}
				// Build a new FK attribute 
				DslModelForeignKeyAttribute fkAttribute = buildFKAttribute(entityField, attribute, fkElement, getNextSequence(fk));
				// and add it in FK
				fk.addAttribute(fkAttribute);
			}
		}
	}
	
	/**
	 * Complete the given FK if necessary 
	 * 
	 * Examples for "Score" entity referenced entity "Student" with single PK "id" 
	 *   FK(Student)        --> "FK_Score_Student", "Student", "id" 
	 *   FK(Student.id)     --> "FK_Score_Student", "Student", "id" 
	 *   FK(FK1,Student)    --> "FK1", "Student", "id"
	 *   FK(FK1,Student.id) --> "FK1", "Student", "id"
	 * Examples for "Student" entity referenced entity "Course" with composite PK  "topic" + "no"
	 *   FK(Course)         --> INVALID 
	 *   FK(Course.topic)   --> "FK_Student_Course", "Course", "topic"  
	 *   FK(Course.no   )   --> "FK_Student_Course", "Course", "no"  
	 *   FK(FK2, Course.topic) --> "FK2", "Course", "topic"  
	 *   FK(FK2, Course.no   ) --> "FK2", "Course", "no"  
	 * 
	 * @param entityName
	 * @param fieldName
	 * @param fkElement
	 * @return 
	 */
	private FkElement completeFK(String entityName, String fieldName, FkElement fkElement) {
		String fkName = fkElement.getFkName() ;
		String referencedEntityName = fkElement.getReferencedEntityName() ;
		
		//--- Check referenced entity ( it must exist and have an ID )
		if ( StrUtil.nullOrVoid(referencedEntityName) ) {
			throw new IllegalStateException( fieldName
					+ " : FK error : no referenced entity " );
		}
		DslModelEntity referencedEntity = (DslModelEntity) this.model.getEntityByClassName(referencedEntityName);
		if (referencedEntity == null ) {
			throw new IllegalStateException( fieldName
					+ " : FK error : unknown entity '" + referencedEntityName + "'" );
		}
		if ( ! referencedEntity.hasId() ) {
			throw new IllegalStateException( fieldName
					+ " : FK error : entity '" + referencedEntityName + "' has no PK" );
		}
		
		//--- Check referenced field ( must exist if specified, ref entity must have a single ID if not specified ) 
		Attribute referencedAttribute = getReferencedAttribute(fieldName, fkElement, referencedEntity ) ;
		
		//--- Build default FK name if not defined
		if ( StrUtil.nullOrVoid(fkName) ) {
			throw new IllegalStateException( fieldName
					+ " : FK error : FK name is null or void" );
		}
		
		//--- Return the complete FK
		return new FkElement(fkName, referencedEntityName, referencedAttribute.getName());
	}
	
	private int getNextSequence(DslModelForeignKey fk ) {
		return fk.getAttributes().size() + 1 ; 
	}
	
	/**
	 * Returns the entity referenced by the given FK 
	 * @param entityField
	 * @param fkElement
	 * @return
	 */
	private DslModelEntity getReferencedEntity(String entityField, FkElement fkElement) {
		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(fkElement.getReferencedEntityName());
		if ( referencedEntity == null ) {
			throw new IllegalStateException( entityField
				+ " : FK error : invalid referenced entity " + fkElement.getReferencedEntityName() );
		}
		return referencedEntity;
	}
	
	/**
	 * Search the referenced attribute in the given referenced entity 
	 * @param fieldName
	 * @param fkDef
	 * @param referencedEntity
	 * @return 
	 */
	private Attribute getReferencedAttribute(String fieldName, FkElement fkDef, DslModelEntity referencedEntity ) {

		String referencedFieldName = fkDef.getReferencedFieldName() ;
		if ( StrUtil.nullOrVoid(referencedFieldName) ) {
			// The field name is NOT specified in FK definition 
			// => try to find a unique PK attribute in the referenced entity
			List<Attribute> pkAttributes = referencedEntity.getKeyAttributes();
			switch ( pkAttributes.size() ) {
			case 0 :
				throw new IllegalStateException( fieldName
						+ " : FK error : no PK field in referenced entity '" + referencedEntity.getClassName() + "'" );
			case 1 :
				// OK the target PK has a single field (not composite PK)
				return pkAttributes.get(0);
			default :
				throw new IllegalStateException( fieldName
						+ " : FK error : unique PK field expected in referenced entity '" + referencedEntity.getClassName() + "'" );
			}
		}
		else {
			// The field name is specified in FK definition => get it and check it
			Attribute a = referencedEntity.getAttributeByName(referencedFieldName);
			if ( a == null ) {
				throw new IllegalStateException( fieldName
						+ " : FK error : '" + referencedFieldName + "' not found in entity '" + referencedEntity.getClassName() + "'");
			}
			else {
				if ( ! a.isKeyElement() ) {
					throw new IllegalStateException( fieldName
							+ " : FK error : '" + referencedFieldName + "' is not a ID/PK attribute in entity '" + referencedEntity.getClassName() + "'");
				}
			}
			return a;
		}
	}
	
	private DslModelForeignKeyAttribute buildFKAttribute(String entityField, DslModelAttribute field, FkElement fkDef, int ordinal) {

		//--- ORIGIN ATTRIBUTE
		String originAttributeName = field.getName();
		
		//--- REFERENCED ATTRIBUTE 
		// Referenced entity
		DslModelEntity referencedEntity = getReferencedEntity(entityField, fkDef);
		// Referenced field
		DslModelAttribute referencedField = (DslModelAttribute) referencedEntity.getAttributeByName(fkDef.getReferencedFieldName());
		if ( referencedField == null ) {
			throw new IllegalStateException( entityField
				+ " : FK error : invalid referenced field " + fkDef.getReferencedFieldName() );
		}
		String referencedAttributeName = referencedField.getName();
		
		//--- NEW FK ATTRIBUTE 
		return new DslModelForeignKeyAttribute(ordinal, originAttributeName, referencedAttributeName);
	}

	private void checkForeignKeyValidity(String entityName, ForeignKey fk) {
		List<ForeignKeyAttribute> fkAttributes = fk.getAttributes() ;
		if ( fkAttributes.isEmpty() ) {
			throw new IllegalStateException( entityName
					+ " : FK error '" +fk.getName()+"' is empty (no reference)");
		}
		String referencedEntityName = fk.getReferencedEntityName();
		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
		if ( referencedEntity == null ) {
			throw new IllegalStateException( entityName
					+ " : FK '" +fk.getName()+"' references invalid entity '" + referencedEntityName + "'");
		}
		int expectedColumnsCount = referencedEntity.getIdCount();
		if ( fkAttributes.size() != expectedColumnsCount ) {
			throw new IllegalStateException( entityName
					+ " : FK '" +fk.getName()+"' invalid number of references : " + fkAttributes.size() 
					+ " (" + expectedColumnsCount + " expected)" );
		}
	}
}
