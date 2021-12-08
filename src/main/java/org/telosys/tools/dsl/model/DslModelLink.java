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
import java.util.Map;

import org.telosys.tools.generic.model.BooleanValue;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.Optional;

public class DslModelLink implements Link {
    private String id;
    
    private final String fieldName; 

    private List<JoinColumn> joinColumns;
    
    private String sourceTableName; // table associated with the entity holding the link
    private String targetEntityClassName;
    private String targetTableName; // table associated with the target entity 
    
    private boolean owningSide;
    private String mappedBy;
    
    private boolean inverseSide;
    private String  inverseSideLinkId;
    
    private Optional       optional    = Optional.UNDEFINED;
    private Cardinality    cardinality = Cardinality.UNDEFINED;
    private FetchType      fetchType   = FetchType.UNDEFINED;
    private CascadeOptions cascadeOptions;
    
    private boolean basedOnForeignKey;
    private String foreignKeyName;
    
    private boolean basedOnJoinTable;
    private JoinTable joinTable;
    private String joinTableName;
    
    private String comparableString;
    private boolean isEmbedded = false ;
    private boolean isTransient = false ;
    
    private BooleanValue insertable = BooleanValue.UNDEFINED; // Added in v 3.3.0
    private BooleanValue updatable  = BooleanValue.UNDEFINED; // Added in v 3.3.0

	private Map<String, String> tagsMap = null ; // Tags added in v 3.4.0

    /**
     * Constructor
     * @param fieldName
     */
    public DslModelLink(String fieldName) {
		super();
        this.fieldName = fieldName;
	}

	@Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<JoinColumn> getJoinColumns() {
        return joinColumns;
    }
    public void setJoinColumns(List<JoinColumn> joinColumns) {
        this.joinColumns = joinColumns;
    }
    public boolean hasJoinColumns() {
        if ( this.joinColumns != null ) {
        	return ! this.joinColumns.isEmpty() ;
        }
        return false;
    }

    @Override
    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    @Override
    public String getTargetEntityClassName() {
        return targetEntityClassName;
    }

    public void setTargetEntityClassName(String targetEntityClassName) {
        this.targetEntityClassName = targetEntityClassName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

//    public void setFieldName(String fieldName) {
//        this.fieldName = fieldName;
//    }

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

    @Override
    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    @Override
    public boolean isInverseSide() {
        return inverseSide;
    }
    public void setInverseSide(boolean inverseSide) {
        this.inverseSide = inverseSide;
    }

    @Override
    public String getInverseSideLinkId() {
        return inverseSideLinkId;
    }
    public void setInverseSideLinkId(String inverseSideLinkId) {
        this.inverseSideLinkId = inverseSideLinkId;
    }

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
    public boolean isBasedOnForeignKey() {
        return basedOnForeignKey;
    }

    public void setBasedOnForeignKey(boolean basedOnForeignKey) {
        this.basedOnForeignKey = basedOnForeignKey;
    }

    @Override
    public String getForeignKeyName() {
        return foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }

    @Override
    public boolean isBasedOnJoinTable() {
        return basedOnJoinTable;
    }

    public void setBasedOnJoinTable(boolean basedOnJoinTable) {
        this.basedOnJoinTable = basedOnJoinTable;
    }

    @Override
    public JoinTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
    }

    @Override
    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    @Override
    public String getComparableString() {
        return comparableString;
    }

    public void setComparableString(String comparableString) {
        this.comparableString = comparableString;
    }

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
	public void setTags(Map<String,String> tags) { 
		this.tagsMap = tags;
	}
	
	@Override
	public Map<String, String> getTagsMap() {
		return this.tagsMap;
	}

}
