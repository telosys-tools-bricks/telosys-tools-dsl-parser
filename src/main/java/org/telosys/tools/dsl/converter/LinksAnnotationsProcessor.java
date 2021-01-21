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
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelJoinColumn;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.Optional;

public class LinksAnnotationsProcessor {

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
			else if (AnnotationName.OPTIONAL.equals(annotation.getName())) { // Added in ver 3.3
				link.setOptional(Optional.TRUE);
			}
			else if (AnnotationName.FETCH_TYPE_LAZY.equals(annotation.getName())) { // Added in ver 3.3
				link.setFetchType(FetchType.LAZY);
			}
			else if (AnnotationName.FETCH_TYPE_EAGER.equals(annotation.getName())) { // Added in ver 3.3
				link.setFetchType(FetchType.EAGER);
			}
			else if (AnnotationName.MAPPED_BY.equals(annotation.getName())) { // Added in ver 3.3
				String mappedByValue = annotation.getParameterAsString();
				if ( ! StrUtil.nullOrVoid(mappedByValue) ) {
					link.setMappedBy(mappedByValue.trim());
					link.setInverseSide(true);
				}
			}
			else if (AnnotationName.LINK_BY_COL.equals(annotation.getName())) { // Added in ver 3.3
				// Example : @LinkByCol(col1,col2)
				List<JoinColumn> joinColumns = buildJoinColumnsFromString(annotation.getParameterAsString());
				link.setJoinColumns(joinColumns);
			}
			else if (AnnotationName.LINK_BY_FK.equals(annotation.getName())) { // Added in ver 3.3
				// Example : @LinkByFK(FK_BOOK_AUTHOR) 
				String fkName = annotation.getParameterAsString();
				List<JoinColumn> joinColumns = buildJoinColumnsFromForeignKey(entity, link, fkName);
				link.setJoinColumns(joinColumns);
				link.setBasedOnForeignKey(true);
				link.setForeignKeyName(fkName);
			}
		}
	}
	
	/**
	 * Build join columns list from the given string
	 * @param s columns separated by a comma ( eg "col1, col2  , col3 " )
	 * @return
	 */
	protected List<JoinColumn> buildJoinColumnsFromString(String s) {
		List<JoinColumn> joinColumns = new LinkedList<>();
		if ( ! StrUtil.nullOrVoid(s) ) {
			String[] parts = StrUtil.split(s, ',');
			for ( String col : parts ) {
				if ( ! StrUtil.nullOrVoid(col) ) {
					joinColumns.add(new DslModelJoinColumn(col.trim()));
					// No referenced column name in this case
				}
			}
		}
		return joinColumns;
	}

	/**
	 * Build join columns from the given Foreign Key name
	 * @param entity current entity (holding the foreign keys)
	 * @param link
	 * @param fkName foreign key name
	 * @return
	 */
	protected List<JoinColumn> buildJoinColumnsFromForeignKey(DslModelEntity entity, DslModelLink link, String fkName) {
		if ( ! StrUtil.nullOrVoid(fkName) ) {
			ForeignKey fk = entity.getDatabaseForeignKeyByName(fkName);
			if ( fk != null ) {
				checkIfForeignKeyIsCompatible(link, fk);
				return JoinColumnsUtil.buildJoinColumnsFromForeignKey(fk);
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
