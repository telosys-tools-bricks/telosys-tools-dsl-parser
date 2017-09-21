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
package org.telosys.tools.dsl.generic.model;

import org.telosys.tools.generic.model.*;

import java.util.List;
import org.telosys.tools.generic.model.Optional;

public class GenericLink implements Link {
    private String id;
    private List<JoinColumn> joinColumns;
    private String targetTableName;
    private String targetEntityClassName;
    private String fieldName;
    private String fieldType;
    private boolean owningSide;
    private String mappedBy;
    private final boolean selected = true ; // Always considered as "selected" 
    private String sourceTableName;
    private boolean inverseSide;
    private String inverseSideLinkId;
    private Optional optional;
    private Cardinality cardinality;
    private CascadeOptions cascadeOptions;
    private FetchType fetchType;
    private boolean basedOnForeignKey;
    private String foreignKeyName;
    private boolean basedOnJoinTable;
    private JoinTable joinTable;
    private String joinTableName;
    private String comparableString;
    private boolean isEmbedded;

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

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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
        return selected; // Always "TRUE" ( "SELECTED" )
    }

//    public void setSelected(boolean selected) {
//        this.selected = selected;
//    }

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

    public void setIsEmbedded(boolean isEmbedded) {
        this.isEmbedded = isEmbedded;
    }

    @Override
    public boolean isEmbedded() {
        return this.isEmbedded;
    }
}
