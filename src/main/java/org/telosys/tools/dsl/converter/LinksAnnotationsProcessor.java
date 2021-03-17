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
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelJoinTable;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.BooleanValue;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;
import org.telosys.tools.generic.model.Optional;

public class LinksAnnotationsProcessor {

	private final DslModel    dslModel;

	/**
	 * Constructor
	 */
	public LinksAnnotationsProcessor(DslModel dslModel) {
		super();
		this.dslModel = dslModel;
	}

	/**
	 * Apply annotations for the given link in the given entity
	 * @param entity 
	 * @param link
	 * @param annotations
	 */
	public void applyAnnotationsForLink(DslModelEntity entity, DslModelLink link, Collection<DomainAnnotation> annotations) {
		for (DomainAnnotation annotation : annotations ) {
			if (AnnotationName.EMBEDDED.equals(annotation.getName())) {
				link.setEmbedded(true);
			}
			else if (AnnotationName.MANY_TO_MANY.equals(annotation.getName())) { // Added in ver 3.3
				link.setCardinality(Cardinality.MANY_TO_MANY);
			}
			else if (AnnotationName.ONE_TO_ONE.equals(annotation.getName())) { // Added in ver 3.3
				link.setCardinality(Cardinality.ONE_TO_ONE);
			}
			else if (AnnotationName.OPTIONAL.equals(annotation.getName())) { // Added in ver 3.3
				link.setOptional(Optional.TRUE);
			}
			else if (AnnotationName.FETCH_TYPE_LAZY.equals(annotation.getName())) { // Added in ver 3.3
				link.setFetchType(FetchType.LAZY);
			}
			else if (AnnotationName.FETCH_TYPE_EAGER.equals(annotation.getName())) { // Added in ver 3.3
				link.setFetchType(FetchType.EAGER);
			}
			else if (AnnotationName.INSERTABLE.equals(annotation.getName())) { // Added in ver 3.3
				processInsertable(entity, link, annotation);
			}
			else if (AnnotationName.UPDATABLE.equals(annotation.getName())) { // Added in ver 3.3
				processUpdatable(entity, link, annotation);
			}
			else if (AnnotationName.MAPPED_BY.equals(annotation.getName())) { // Added in ver 3.3
				processMappedBy(entity, link, annotation);
			}
			else if (AnnotationName.LINK_BY_ATTR.equals(annotation.getName())) { // Added in ver 3.3
				// Example : @LinkByAttr(attr1)   @LinkByAttr(attr1 > ref1 , attr2 > ref2 )
				processLinkByAttr(entity, link, annotation);
			}
			else if (AnnotationName.LINK_BY_COL.equals(annotation.getName())) { // Added in ver 3.3
				// Example : @LinkByCol(col1)   @LinkByCol(col1 > ref1 , col2 > ref2 )
				processLinkByCol(link, annotation);
			}
			else if (AnnotationName.LINK_BY_FK.equals(annotation.getName())) { // Added in ver 3.3
				// Example : @LinkByFK(FK_BOOK_AUTHOR) 
				processLinkByFK(entity, link, annotation);
			}
			else if (AnnotationName.LINK_BY_JOIN_ENTITY.equals(annotation.getName())) { // Added in ver 3.3
				processLinkByJoinEntity(link, annotation);
			}
		}
	}
	
	/**
	 * Process '@MappedBy(attributeName)' annotation <br>
	 * @param entity
	 * @param link
	 * @param annotation
	 */
	private void processMappedBy(DslModelEntity entity, DslModelLink link, DomainAnnotation annotation) {
		String mappedByValue = annotation.getParameterAsString();
		if ( ! StrUtil.nullOrVoid(mappedByValue) ) {
			String targetEntityName = link.getTargetEntityClassName();
			DslModelEntity targetEntity = (DslModelEntity) dslModel.getEntityByClassName(targetEntityName);
			if ( targetEntity != null ) {
				String attributeName = mappedByValue.trim();
// All links are not yet build => cannot check validity				
//				// check owning side link existence in the target entity
//				if ( targetEntity.getLinkByFieldName(attributeName) != null ) {
//					// the MappedBy value is correct
//					link.setMappedBy(attributeName);
//					// has MappedBy => inverse side
//					link.setInverseSide(true);
//					link.setOwningSide(false);
//				}
//				else {
//					throw mappedByError(entity, link, 
//							"cannot found owning side link '"+ targetEntityName + "." + attributeName + "'") ;
//				}
				link.setMappedBy(attributeName);
				// has MappedBy => inverse side
				link.setInverseSide(true);
				link.setOwningSide(false);
			}
			else {
				throw mappedByError(entity, link, "cannot found referenced entity '"+ targetEntityName + "'") ;
			}
		}
		else {
			throw mappedByError(entity, link, "invalid value (attribute name expected)") ;
		}
	}
	private RuntimeException mappedByError(Entity entity, DslModelLink link, String msg) {
		return new IllegalStateException( entity.getClassName() 
				+ "." + link.getFieldName() 
				+ " : @"+ AnnotationName.MAPPED_BY 
				+ " : " + msg );
	}
	
	/**
	 * Process '@Insertable(true|false)' annotation <br>
	 * @param entity
	 * @param link
	 * @param annotation
	 */
	private void processInsertable(DslModelEntity entity, DslModelLink link, DomainAnnotation annotation) {
		BooleanValue v = Util.getBooleanValue(entity.getClassName(), link.getFieldName(), annotation );
		link.setInsertable(v);
	}
	
	/**
	 * Process '@Updatable(true|false)' annotation <br>
	 * @param entity
	 * @param link
	 * @param annotation
	 */
	private void processUpdatable(DslModelEntity entity, DslModelLink link, DomainAnnotation annotation) {
		BooleanValue v = Util.getBooleanValue(entity.getClassName(), link.getFieldName(), annotation );
		link.setUpdatable(v);
	}
	
	/**
	 * Process '@LinkByJoinEntity(EntityName)' annotation <br>
	 * @param link
	 * @param annotation
	 */
	// cf https://vladmihalcea.com/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/
	private void processLinkByJoinEntity(DslModelLink link, DomainAnnotation annotation) {
		String joinEntityName = annotation.getParameterAsString();
		DslModelEntity joinEntity = (DslModelEntity) dslModel.getEntityByClassName(joinEntityName);
		if ( joinEntity == null ) {
			throw new IllegalStateException("@"+ AnnotationName.LINK_BY_JOIN_ENTITY 
					+ " : cannot found join entity '"+ joinEntityName + "'");
		}
		List<ForeignKey> foreignKeys = joinEntity.getDatabaseForeignKeys();
		if ( foreignKeys != null && foreignKeys.size() == 2 ) {
			ForeignKey fk1 = foreignKeys.get(0);
			ForeignKey fk2 = foreignKeys.get(1);
			ForeignKey joinColumnsFK = getFKReferencingTable(link.getSourceTableName(), fk1, fk2 );
			ForeignKey inverseJoinColumnsFK = getFKReferencingTable(link.getTargetTableName(), fk1, fk2 );
			if ( joinColumnsFK.getName().equals(inverseJoinColumnsFK.getName()) ) {
				throw new IllegalStateException("@"+ AnnotationName.LINK_BY_JOIN_ENTITY 
						+ " : the 2 foreign keys are identical");
			}
//			List<JoinColumn> joinColumns = JoinColumnsUtil.buildJoinColumnsFromForeignKey(joinColumnsFK);
			JoinColumnsBuilder jcb = new JoinColumnsBuilder("@"+ AnnotationName.LINK_BY_JOIN_ENTITY ) ;
			List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromForeignKey(joinColumnsFK);

//			List<JoinColumn> inverseJoinColumns = JoinColumnsUtil.buildJoinColumnsFromForeignKey(inverseJoinColumnsFK);
			List<JoinColumn> inverseJoinColumns = jcb.buildJoinColumnsFromForeignKey(inverseJoinColumnsFK);
			
			
			JoinTable joinTable =  new DslModelJoinTable(joinEntity, joinColumns, inverseJoinColumns);
			link.setJoinTable(joinTable);
			// has Join Table => owning side
			link.setOwningSide(true);
			link.setInverseSide(false);
		}
		else {
			throw new IllegalStateException("@"+ AnnotationName.LINK_BY_JOIN_ENTITY 
					+ " : join entity '"+ joinEntity + "' does not have 2 foreign keys");
		}
	}
	
	private ForeignKey getForeignKeyByName(DslModelEntity entity, String fkName) {
		if ( ! StrUtil.nullOrVoid(fkName) ) {
			ForeignKey fk = entity.getDatabaseForeignKeyByName(fkName);
			if ( fk != null ) {
				return fk;
			}
			else {
				// FK not found => ERROR
				throw new IllegalStateException("@LinkByFK : cannot found Foreign Key '" + fkName + "' in entity" );
			}
		}
		else {
			// no FK name => ERROR
			throw new IllegalStateException("@LinkByFK : no Foreign Key name");
		}
	}
	
	private ForeignKey getFKReferencingTable(String tableName, ForeignKey fk1, ForeignKey fk2 ) {
		if ( tableName.equals(fk1.getReferencedTableName()) ) {
			return fk1 ;
		}
		else if ( tableName.equals(fk2.getReferencedTableName()) ) {
			return fk2 ;
		}
		else {
			throw new IllegalStateException("@"+ AnnotationName.LINK_BY_JOIN_ENTITY 
					+ " : join entity does not have FK referencing table '" + tableName + "'");
		}
	}
	
	/**
	 * Process '@LinkByAttr(xxx)' annotation <br>
	 * @param entity
	 * @param link
	 * @param annotation
	 */
	private void processLinkByAttr(DslModelEntity entity, DslModelLink link, DomainAnnotation annotation) {
		String referencedEntityName = link.getTargetEntityClassName();
		DslModelEntity referencedEntity = (DslModelEntity) dslModel.getEntityByClassName(referencedEntityName);
		if ( referencedEntity == null ) {
			throw new IllegalStateException("@"+ AnnotationName.LINK_BY_ATTR 
					+ " : cannot found referenced entity '"+ referencedEntityName + "'");
		}
		ReferenceDefinitions attributesRefDef = buildReferenceDefinitions(annotation);
		ReferenceDefinitions columnsRefDef = convertAttribRefToColRef(entity, referencedEntity, attributesRefDef);
//		List<JoinColumn> joinColumns = buildJoinColumns(columnsRefDef, AnnotationName.LINK_BY_ATTR); 
		JoinColumnsBuilder jcb = new JoinColumnsBuilder("@"+AnnotationName.LINK_BY_ATTR) ;
		List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromRefDef(columnsRefDef); 
		link.setJoinColumns(joinColumns);
	}
	
	/**
	 * Process '@LinkByCol(xxx)' annotation <br>
	 * Examples : <br> 
	 * . @LinkByCol(col1) <br> 
	 * . @LinkByCol(col1 > referencedCol1 , col2 > referencedCol2 ) <br> 
	 * 
	 * @param link
	 * @param annotation
	 */
	private void processLinkByCol(DslModelLink link, DomainAnnotation annotation) {
		ReferenceDefinitions columnsRefDef = buildReferenceDefinitions(annotation);
//		List<JoinColumn> joinColumns = buildJoinColumns(columnsRefDef, annotation.getName()); 
		JoinColumnsBuilder jcb = new JoinColumnsBuilder("@"+annotation.getName()) ;
		List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromRefDef(columnsRefDef); 		
		link.setJoinColumns(joinColumns);
	}

	/**
	 * Process '@LinkByFK(FKName)' annotation <br>
	 * @param entity
	 * @param link
	 * @param annotation
	 */
	private void processLinkByFK(DslModelEntity entity, DslModelLink link, DomainAnnotation annotation) {
		String fkName = annotation.getParameterAsString();
		
//		List<JoinColumn> joinColumns = buildJoinColumnsFromForeignKey(entity, link, fkName);
		ForeignKey fk = getForeignKeyByName(entity, fkName);
		checkIfForeignKeyIsCompatible(link, fk);
		JoinColumnsBuilder jcb = new JoinColumnsBuilder("@LinkByFK("+fkName+")") ;
		List<JoinColumn> joinColumns = jcb.buildJoinColumnsFromForeignKey(fk);

		link.setJoinColumns(joinColumns);
		link.setBasedOnForeignKey(true);
		link.setForeignKeyName(fkName);
	}
	
	protected ReferenceDefinitions buildReferenceDefinitions(DomainAnnotation annotation) {
		ReferenceDefinitions rd = buildReferenceDefinitions(annotation.getParameterAsString());
		checkReferenceDefinitions(annotation, rd);
		return rd;
	}
	
	/**
	 * Builds all references definitions (for columns references or attributes references)<br>
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
	
	private void checkReferenceDefinitions(DomainAnnotation annotation, ReferenceDefinitions referenceDefinitions ) {
		if ( referenceDefinitions.count() == 0 ) {
			throw new IllegalStateException("@" + annotation.getName() 
						+ " : no reference definition");
		}
		else {
			if ( referenceDefinitions.count() > 1 ) {
				int referencedCount = 0;
				for ( ReferenceDefinition rd : referenceDefinitions.getList() ) {
					if ( rd.hasReferencedName() ) {
						referencedCount++;
					}
				}
				if ( referencedCount < referenceDefinitions.count() ) {
					throw new IllegalStateException("@" + annotation.getName() 
					+ " : missing referenced name(s)");
				}
			}
		}
	}
	
	/**
	 * Converts attributes references definitions ( "attr", "attr > referencedAttr" ) <br>
	 * to columns references definitions ( "col", "col > referencedCol" ) <br>
	 * @param entity
	 * @param referencedEntity
	 * @param referenceDefinitions
	 * @return
	 */
	private ReferenceDefinitions convertAttribRefToColRef(DslModelEntity entity, 
			DslModelEntity referencedEntity, ReferenceDefinitions referenceDefinitions) {
		ReferenceDefinitions referenceDefinitionsForColumns = new ReferenceDefinitions();
		for ( ReferenceDefinition rd : referenceDefinitions.getList()) {
			String columnName = getAttribColumnName(entity, rd.getName());
			if ( rd.getReferencedName().isEmpty() ) {
				referenceDefinitionsForColumns.add(new ReferenceDefinition(columnName));
			}
			else {
				String referencedColumnName = getAttribColumnName(referencedEntity, rd.getReferencedName());
				referenceDefinitionsForColumns.add(new ReferenceDefinition(columnName,referencedColumnName));
			}
		}
		return referenceDefinitionsForColumns;
	}
	
	/**
	 * Returns the database column name associated with the given attribute name
	 * @param entity
	 * @param attribName
	 * @return
	 */
	private String getAttribColumnName(DslModelEntity entity, String attribName) {
		if ( ! StrUtil.nullOrVoid(attribName) ) {
			Attribute attrib = entity.getAttributeByName(attribName);
			if ( attrib != null ) {
				String columnName = attrib.getDatabaseName();
				if ( ! StrUtil.nullOrVoid(columnName) ) {
					return columnName;
				}
				else {
					throw new IllegalStateException("@LinkByAttr : no database column for attribute '" + attribName + "'");
				}
			}
			else {
				throw new IllegalStateException("@LinkByAttr : unknown attribute '" + attribName + "'");
			}
		}
		return "";
	}

//	private List<JoinColumn> buildJoinColumns(ReferenceDefinitions referenceDefinitions, String annotationName) {
//		List<ReferenceDefinition> refDefList = referenceDefinitions.getList();
////		int nbCol = refDefList.size();
//	    if ( refDefList.isEmpty() ) {
//	    	throw new IllegalStateException("@"+annotationName+" : no join column (empty list)");
//	    }
//		List<JoinColumn> joinColumns = new LinkedList<>();
//		for ( ReferenceDefinition rd : refDefList) {
//		    String columnName = rd.getName();
//		    String referencedColumnName = rd.getReferencedName();
//	    	/**
//		    if ( ! columnName.isEmpty() ) {
//		    	// col
//				DslModelJoinColumn jc = new DslModelJoinColumn(columnName);
//				if ( ! referencedColumnName.isEmpty() ) {
//			    	// col > refCol
//					jc.setReferencedColumnName(referencedColumnName);
//				}
//				else {
//					if ( nbCol > 1 ) {
//						// ERROR : referenced column mandatory for each column !
//					}
//				}
//				joinColumns.add(jc);
//		    }
//			**/
//		    if ( StrUtil.nullOrVoid(columnName) ) {
//		    	throw new IllegalStateException("@"+annotationName+" : column name null or void");
//		    }
//		    if ( StrUtil.nullOrVoid(referencedColumnName) ) {
//		    	throw new IllegalStateException("@"+annotationName+" : referenced column name null or void");
//		    }
//			DslModelJoinColumn jc = new DslModelJoinColumn(columnName, referencedColumnName);
//			
//			// TODO
////			jc.setInsertable(insertable);
////			jc.setUpdatable(updatable);
////			jc.setNullable(nullable);
//			
//			joinColumns.add(jc);
//		}
//		return joinColumns;
//	}
	
//	/**
//	 * Build join columns from the given Foreign Key name
//	 * @param entity current entity (holding the foreign keys)
//	 * @param link
//	 * @param fkName foreign key name
//	 * @return
//	 */
//	private List<JoinColumn> buildJoinColumnsFromForeignKey(DslModelEntity entity, DslModelLink link, String fkName) {
//		if ( ! StrUtil.nullOrVoid(fkName) ) {
//			ForeignKey fk = entity.getDatabaseForeignKeyByName(fkName);
//			if ( fk != null ) {
//				checkIfForeignKeyIsCompatible(link, fk);
////				return JoinColumnsUtil.buildJoinColumnsFromForeignKey(fk);
//				JoinColumnsBuilder jcb = new JoinColumnsBuilder("@LinkByFK("+fkName+")") ;
//				return jcb.buildJoinColumnsFromForeignKey(fk);
//			}
//			else {
//				// FK not found => ERROR
//				throw new IllegalStateException("@LinkByFK : cannot found Foreign Key '" + fkName + "' in entity" );
//			}
//		}
//		else {
//			// no FK name => ERROR
//			throw new IllegalStateException("@LinkByFK : no Foreign Key name");
//		}
//	}

	private void checkIfForeignKeyIsCompatible(DslModelLink link, ForeignKey fk) {
		String linkTable = link.getTargetTableName();
		String fkTable = fk.getReferencedTableName();
		if ( linkTable != null ) {
			if ( ! linkTable.equals(fkTable) ) {
				throw new IllegalStateException("@LinkByFK : invalid Foreign Key : ref table '" + fkTable + "' != '" + linkTable + "'");
			}
		}
		else {
			throw new IllegalStateException("@LinkByFK : cannot check reference because link table is null");
		}
	}
}
