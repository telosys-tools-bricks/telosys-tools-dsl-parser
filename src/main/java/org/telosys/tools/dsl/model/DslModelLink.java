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

import java.util.List;

import org.telosys.tools.dsl.tags.Tags;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.LinkAttribute;
import org.telosys.tools.generic.model.TagContainer;
import org.telosys.tools.generic.model.enums.BooleanValue;
import org.telosys.tools.generic.model.enums.Cardinality;
import org.telosys.tools.generic.model.enums.FetchType;
import org.telosys.tools.generic.model.enums.Optional;

public class DslModelLink implements Link {
	
    private final String fieldName; 

//    private String id = ""; // removed in v 3.4.0
    
//    private List<JoinColumn> joinColumns; // v 3.4.0
//    private List<JoinAttribute> joinAttributes;  // v 3.4.0
    private List<LinkAttribute> linkAttributes;  // v 3.4.0
    
//    private String sourceTableName; // table associated with the entity holding the link // v 3.4.0
    private String referencedEntityName;
//    private String targetTableName; // table associated with the target entity  // v 3.4.0
    
    private boolean owningSide;
    private String mappedBy;
    
    private boolean inverseSide;
//    private String  inverseSideLinkId; // removed in v 3.4.0
    
    private Optional       optional    = Optional.UNDEFINED;
    private Cardinality    cardinality = Cardinality.UNDEFINED;
    private FetchType      fetchType   = FetchType.UNDEFINED;
    private CascadeOptions cascadeOptions;
    
    private boolean basedOnForeignKey = false ;
    private String foreignKeyName;
    
    private boolean basedOnAttributes = false; // v 3.4.0
    
//    private boolean basedOnJoinTable; // v 3.4.0
    private boolean basedOnJoinEntity  = false; // v 3.4.0
//    private JoinTable joinTable; // v 3.4.0
//    private String joinTableName; // v 3.4.0
    private String joinEntityName; // v 3.4.0
    
//    private String comparableString; // removed in v 3.4.0
    private boolean isEmbedded = false ;
    private boolean isTransient = false ;
    
    private BooleanValue insertable = BooleanValue.UNDEFINED; // Added in v 3.3.0
    private BooleanValue updatable  = BooleanValue.UNDEFINED; // Added in v 3.3.0

    // Tags added in v 3.4.0 
    private TagContainer tagContainer = new Tags() ;  // Init with void Tags (never null)
    
    private boolean orphanRemoval = false; // Added in v 4.1.0

    /**
     * Constructor
     * @param fieldName
     */
    public DslModelLink(String fieldName) {
		super();
        this.fieldName = fieldName;
	}

//	@Override
//    public String getId() { // removed in v 3.4.0
//        return id;
//    }
//
//    public void setId(String id) { // removed in v 3.4.0
//        this.id = id;
//    }

//    @Override
//    public List<JoinColumn> getJoinColumns() {
//        return joinColumns;
//    }
//    public void setJoinColumns(List<JoinColumn> joinColumns) {
//        this.joinColumns = joinColumns;
//    }
//    public boolean hasJoinColumns() {
//        if ( this.joinColumns != null ) {
//        	return ! this.joinColumns.isEmpty() ;
//        }
//        return false;
//    }
    @Override
//    public List<JoinAttribute> getJoinAttributes() {
    public List<LinkAttribute> getAttributes() {
        return linkAttributes;
    }
//    public void setJoinAttributes(List<JoinAttribute> joinAttributes) {
    public void setAttributes(List<LinkAttribute> linkAttributes) {
        this.linkAttributes = linkAttributes;
    }
//    public boolean hasJoinAttributes() {
    public boolean hasAttributes() {
        if ( this.linkAttributes != null ) {
        	return ! this.linkAttributes.isEmpty() ;
        }
        return false;
    }

//    @Override
//    public String getTargetTableName() {
//        return targetTableName;
//    }
//    public void setTargetTableName(String targetTableName) {
//        this.targetTableName = targetTableName;
//    }

    @Override
    //public String getTargetEntityClassName() {
    public String getReferencedEntityName() {
        return referencedEntityName;
    }
    //public void setTargetEntityClassName(String targetEntityClassName) {
        public void setReferencedEntityName(String referencedEntityName) {
        this.referencedEntityName = referencedEntityName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean isOwningSide() {
        return owningSide;
    }
    public void setOwningSide(boolean owningSide) {
        this.owningSide = owningSide;
    }

    @Override
    public String getMappedBy() {
        return mappedBy;
    }
    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    @Override
    public boolean isSelected() {
        return true; // Always "TRUE" ( "SELECTED" )
    }

//    @Override
//    public String getSourceTableName() {
//        return sourceTableName;
//    }
//    public void setSourceTableName(String sourceTableName) {
//        this.sourceTableName = sourceTableName;
//    }

    @Override
    public boolean isInverseSide() {
        return inverseSide;
    }
    public void setInverseSide(boolean inverseSide) {
        this.inverseSide = inverseSide;
    }

//    @Override
//    public String getInverseSideLinkId() { // removed in v 3.4.0
//        return inverseSideLinkId;
//    }
//    public void setInverseSideLinkId(String inverseSideLinkId) { // removed in v 3.4.0
//        this.inverseSideLinkId = inverseSideLinkId;
//    }

    @Override
    public Optional getOptional() {
        return optional;
    }
    public void setOptional(Optional optional) {
        this.optional = optional;
    }

    @Override
    public Cardinality getCardinality() {
        return cardinality;
    }
    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public CascadeOptions getCascadeOptions() {
        return cascadeOptions;
    }
    public void setCascadeOptions(CascadeOptions cascadeOptions) {
        this.cascadeOptions = cascadeOptions;
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }
    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    @Override
    public String getForeignKeyName() {
        return foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

//    @Override
//    public boolean isBasedOnJoinTable() {
//        return basedOnJoinTable;
//    }
//    public void setBasedOnJoinTable(boolean basedOnJoinTable) {
//        this.basedOnJoinTable = basedOnJoinTable;
//    }
    @Override // link defined by annotation : "@LinkByJoinEntity(...)"
    public boolean isBasedOnJoinEntity() {
        return basedOnJoinEntity;
    }
    public void setBasedOnJoinEntity(boolean v) {
        this.basedOnJoinEntity = v;
    }
    
    @Override // link defined by annotation : "@LinkByFK(..)"
    public boolean isBasedOnForeignKey() { 
        return basedOnForeignKey;
    }
    public void setBasedOnForeignKey(boolean basedOnForeignKey) {
        this.basedOnForeignKey = basedOnForeignKey;
    }

    // link defined by annotation : "@LinkByAttr(..)"
    public boolean isBasedOnAttributes() { 
        return basedOnAttributes;
    }
    public void setBasedOnAttributes(boolean basedOnAttributes) {
        this.basedOnAttributes = basedOnAttributes;
    }

//    @Override
//    public JoinTable getJoinTable() {
//        return joinTable;
//    }
//    public void setJoinTable(JoinTable joinTable) {
//        this.joinTable = joinTable;
//    }
//    @Override
//    public String getJoinTableName() {
//        return joinTableName;
//    }
//    public void setJoinTableName(String joinTableName) {
//        this.joinTableName = joinTableName;
//    }
// v 3.4.0
    @Override
    public String getJoinEntityName() {
        return joinEntityName;
    }
    public void setJoinEntityName(String joinEntityName) {
        this.joinEntityName = joinEntityName;
    }

//    @Override
//    public String getComparableString() { // removed in v 3.4.0
//        return comparableString;
//    }
//
//    public void setComparableString(String comparableString) { // removed in v 3.4.0
//        this.comparableString = comparableString;
//    }

    @Override
    public boolean isEmbedded() {
        return this.isEmbedded;
    }
    public void setEmbedded(boolean b) {
        this.isEmbedded = b;
    }

    @Override
    public boolean isTransient() { // v 3.3.0
        return this.isTransient;
    }
    public void setTransient(boolean b) { // v 3.3.0
        this.isTransient = b;
    }

    @Override
    public BooleanValue getInsertable() {
        return this.insertable;
    }
    public void setInsertable(BooleanValue b) {
        this.insertable = b;
    }

    @Override
    public BooleanValue getUpdatable() {
        return this.updatable;
    }
    public void setUpdatable(BooleanValue b) {
        this.updatable = b;
    }
    
	//-----------------------------------------------------------------------------------------
	// LINK TAGS  (added in v 3.4.0) 
	//-----------------------------------------------------------------------------------------
	public void setTagContainer(TagContainer tags) { 
		this.tagContainer = tags;
	}
	
	@Override
	public TagContainer getTagContainer() {
		return this.tagContainer;
	}

	
    @Override
    public boolean isOrphanRemoval() {
        return this.orphanRemoval;
    }
    public void setOrphanRemoval(boolean b) {
        this.orphanRemoval = b;
    }
    

}
