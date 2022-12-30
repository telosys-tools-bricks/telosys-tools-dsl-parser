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

import org.telosys.tools.dsl.tags.Tags;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.TagContainer;

public class DslModelEntity implements Entity {

	private static final String TABLE = "TABLE";
	private static final String VIEW = "VIEW";

	private final String className; // Entity name = entity class name
	
	private String fullName = "";

	private String packageName = ""; // @Package annotation

	private List<Attribute> attributes = new ArrayList<>();
	private List<ForeignKey> foreignKeys = new ArrayList<>(); // v 3.4.0 

	private List<Link> links = new ArrayList<>();

	// Database 
	private String databaseTable   = ""; // set to 'entity name' by Converter 
	private String databaseCatalog = "";
	private String databaseSchema  = "";
	private String databaseComment = "" ;
	private String databaseTablespace = "" ;
	private boolean databaseView  = false; // v 3.4.0  ( replace databaseType )

	// Other 
	private boolean readOnly = false; // v 3.4.0  annotation @ReadOnly
	private boolean abstractClass = false; // v 3.4.0  annotation @Abstract
	private String  superClass = ""; // v 3.4.0  annotation @Extends

	private boolean aggregateRoot = false; // v 3.4.0  annotation @AggregateRoot
	private String  domain        = ""; // v 3.4.0  annotation @Domain
	private String  context       = ""; // v 3.4.0  annotation @Context
	
	private boolean inMemoryRepository = false; // v 3.4.0  annotation @InMemoryRepository

	private boolean joinEntity = false; // v 4.1.0  annotation @JoinEntity
	
    // Tags added in v 3.4.0 
    private TagContainer tagContainer = new Tags() ;  // Init with void Tags (never null)
    

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
	public void addAttribute(Attribute attribute) { // v 3.4.0
		this.attributes.add(attribute);
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
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getSuperClass() { // v 3.4.0
		return superClass;
	}
	public void setSuperClass(String superClass) { // v 3.4.0
		this.superClass = superClass;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public boolean isAbstract() { // v 3.4.0
		return abstractClass;
	}
	public void setAbstract(boolean abstractClass) { // v 3.4.0
		this.abstractClass = abstractClass;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public boolean isInMemoryRepository() { // v 3.4.0
		return inMemoryRepository;
	}
	public void setInMemoryRepository(boolean inMemoryRepository) { // v 3.4.0
		this.inMemoryRepository = inMemoryRepository;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public boolean isReadOnly() { // v 3.4.0
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) { // v 3.4.0
		this.readOnly = readOnly;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public boolean isAggregateRoot() { // v 3.4.0
		return aggregateRoot;
	}
	public void setAggregateRoot(boolean aggregateRoot) { // v 3.4.0
		this.aggregateRoot = aggregateRoot;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public boolean isJoinEntity() { // v 4.1.0
		return joinEntity;
	}
	public void setJoinEntity(boolean joinEntity) { // v 4.1.0
		this.joinEntity = joinEntity;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
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
	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	public void addForeignKey(ForeignKey fk) { // v 3.4.0
		this.foreignKeys.add(fk);
	}
	public ForeignKey getForeignKeyByName(String fkName) {
		for (ForeignKey fk : this.foreignKeys) {
			if (fkName.equals(fk.getName())) {
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
	public boolean isDatabaseView() {
		return databaseView;
	}
	public void setDatabaseView(boolean databaseView) {
		this.databaseView = databaseView;
	}
	//--------------------------------------------------------------------------
	@Override // TODO : remove
	public String getDatabaseType() {
		return databaseView ? VIEW : TABLE ;
	}
	@Override // TODO : remove
	public Boolean isTableType() {
		return ! isDatabaseView();
	}
	@Override // TODO : remove
	public Boolean isViewType() {
		return isDatabaseView();
	}

	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseComment() {
		return databaseComment;
	}
	public void setDatabaseComment(String databaseComment) {
		this.databaseComment = databaseComment;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseTablespace() {
		return databaseTablespace;
	}
	public void setDatabaseTablespace(String databaseTablespace) {
		this.databaseTablespace = databaseTablespace;
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
	public void addLink(Link link) { // v 3.4.0
		this.links.add(link);
	}
	public Link getLinkByFieldName(String fieldName) {
		for(Link link : links) {
			if(fieldName.equals(link.getFieldName())) {
				return link;
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public List<String> getWarnings() {
		List<String> warnings = new LinkedList<>() ;
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
	@Override
	public Attribute getAttributeByName(String name) {
		for(Attribute attribute : getAttributes()) {
			if(name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}

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
	
	//-----------------------------------------------------------------------------------------
	// TAGS  (added in v 3.4.0) 
	//-----------------------------------------------------------------------------------------
	public void setTagContainer(TagContainer tags) { 
		this.tagContainer = tags;
	}
	
	@Override
	public TagContainer getTagContainer() {
		return this.tagContainer;
	}

}
