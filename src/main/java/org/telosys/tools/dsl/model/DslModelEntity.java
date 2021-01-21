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
package org.telosys.tools.dsl.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Link;

public class DslModelEntity implements Entity {

	private final String className; // Entity name = entity class name
	
	private String fullName = "";

	private String packageName = "";

	private List<Attribute> attributes = new ArrayList<>();
	private List<ForeignKey> databaseForeignKeys = new ArrayList<>();

	private List<Link> links = new ArrayList<>();

	// Database 
	private String databaseTable   = ""; // set to 'entity name' by Converter 
	private String databaseCatalog = "";
	private String databaseSchema  = "";
	private String databaseComment = "" ;
	private String databaseType    = "TABLE";
	private Boolean tableType = true ;
	private Boolean viewType  = false;

	/**
	 * Constructor
	 * @param className
	 */
	public DslModelEntity(String className) {
		super();
		this.className = className;
	}
	//--------------------------------------------------------------------------
	@Override
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Returns all attributes being part of the PK 
	 * @return
	 * @since  3.3.x
	 */
	public List<Attribute> getKeyAttributes() {
		LinkedList<Attribute> attributesList = new LinkedList<>();
		for ( Attribute a : this.attributes ) {
			if ( a.isKeyElement() ) {
            	attributesList.add(a);
			}
		}
		return attributesList ;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getClassName() {
		return className;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getPackageName() {
		return packageName;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseCatalog() {
		return databaseCatalog;
	}
	public void setDatabaseCatalog(String databaseCatalog) {
		this.databaseCatalog = databaseCatalog;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public List<ForeignKey> getDatabaseForeignKeys() {
		return databaseForeignKeys;
	}
	public void setDatabaseForeignKeys(List<ForeignKey> databaseForeignKeys) {
		this.databaseForeignKeys = databaseForeignKeys;
	}
	/**
	 * Try to found a Foreign Key with the given name
	 * @param name
	 * @return
	 */
	public ForeignKey getDatabaseForeignKeyByName(String name) {
		for(ForeignKey fk : this.databaseForeignKeys ) {
			if(name.equals(fk.getName())) {
				return fk;
			}
		}
		return null;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseSchema() {
		return databaseSchema;
	}
	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseTable() {
		return databaseTable;
	}
	public void setDatabaseTable(String databaseTable) {
		this.databaseTable = databaseTable;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseComment() {
		return databaseComment;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public Link getLinkByFieldName(String fieldName) {
		for(Link link : links) {
			if(fieldName.equals(link.getFieldName())) {
				return link;
			}
		}
		return null;
	}
	
//	public String getPackage() {
//		return _package;
//	}
//	public void setPackage(String _package) {
//		this._package = _package;
//	}
	
	//--------------------------------------------------------------------------
	@Override
	public Boolean isTableType() {
		return tableType;
	}
	public void setTableType(Boolean tableType) {
		this.tableType = tableType;
	}

	//--------------------------------------------------------------------------
	@Override
	public Boolean isViewType() {
		return viewType;
	}
	public void setViewType(Boolean viewType) {
		this.viewType = viewType;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public List<String> getWarnings() {
		List<String> warnings = new LinkedList<String>() ;
		if ( hasId() == false ) {
			warnings.add("No ID");
		}
		return warnings;
	}
	
	/**
	 * Returns true if the entity has at least one attribute with "@Id"
	 * @return
	 */
	public boolean hasId() {
		for ( Attribute attribute : this.attributes ) {
			if ( attribute.isKeyElement() ) {
				return true ;
			}
		}
		return false ; // No attribute with "@Id"
	}
	
	/**
	 * Returns the number of attributes having a 'Id' annotation
	 * @return
	 */
	public int getIdCount() {
		int count = 0 ;
		for ( Attribute attribute : this.attributes ) {
			if ( attribute.isKeyElement() ) {
				count++ ;
			}
		}
		return count ;
	}
	
	/**
	 * Returns true if this entity has a composite ID (multiple fields with 'Id' annotation)
	 * @return
	 */
	public boolean hasCompositeId() {
		return getIdCount() > 1 ;
	}
	
	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------
	public Attribute getAttributeByName(String name) {
		for(Attribute attribute : getAttributes()) {
			if(name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}

	public DslModelAttribute getAttributeByDatabaseName(String dbName) {
		for(Attribute attribute : getAttributes()) {
			if(dbName.equals(attribute.getDatabaseName())) {
				return (DslModelAttribute) attribute;
			}
		}
		return null;
	}
	//--------------------------------------------------------------------------
	/**
	 * Replaces the attribute identified by the given name by another one
	 * @param name
	 * @param newAttribute
	 * @return
	 */
	public Attribute replaceAttribute(String name, Attribute newAttribute) {
		List<Attribute> list = this.attributes  ;
		for ( int index = 0 ; index < list.size() ; index++ ) {
			Attribute attribute = list.get(index);
			if ( name.equals(attribute.getName()) ) { // Found
				list.set(index, newAttribute); // Replace
				return attribute ;
			}
		}
		return null;
	}

}
