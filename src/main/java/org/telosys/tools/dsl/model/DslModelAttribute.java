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

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.tags.Tags;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKeyPart;
import org.telosys.tools.generic.model.TagContainer;
import org.telosys.tools.generic.model.enums.BooleanValue;
import org.telosys.tools.generic.model.enums.GeneratedValueStrategy;

public class DslModelAttribute implements Attribute {
	
	private final String name ;
	private final String neutralType ;
	
	private String booleanFalseValue = "";
	private String booleanTrueValue  = "";
	private String databaseComment = "";
	private String databaseDefaultValue = "";
	private String databaseName = "";
	private String databaseSize = ""; // String for size with comma ( eg "8,2" )
	private String databaseType = "";
	
	private boolean isDateFuture = false; // @Future
	private boolean isDatePast = false; // @Past
	private String dateAfterValue = "";
	private String dateBeforeValue = "";
	
	private String defaultValue = "" ; 
	private Entity entity;
	private GeneratedValueStrategy generatedValueStrategy = GeneratedValueStrategy.UNDEFINED; // v 3.4.0
	private String initialValue = "" ; 
	private String inputType = "" ; 
	private String label = "" ; 
	private Integer maxLength;
	private Integer minLength;
	private BigDecimal maxValue;
	private BigDecimal minValue;
	private String pattern = "" ; // ver 3.2.0
	private Integer generatedValueAllocationSize;
	private Integer generatedValueInitialValue; // v 4.1.0
	private String generatedValueSequenceName;
	private String generatedValueTablePkColumnName;
	private String generatedValueTablePkColumnValue;
	private String generatedValueTableName;
	private String generatedValueTableValueColumnName;
	private boolean keyElement = false;
	private boolean longText = false;
	private boolean notBlank = false;
	private boolean notEmpty = false;
	private boolean notNull = false;
    private boolean isTransient = false ; // v 3.3.0
	
	// An attribute can be involved in many FK, it can be both in a SIMPLE FK and in a COMPOSITE FK 
	private boolean isForeignKeySimple     = false ; // ( false by default )
	private boolean isForeignKeyComposite  = false ; // ( false by default )
	private String  referencedEntityClassName = null ; // no reference by default
	private List<ForeignKeyPart> fkParts = new LinkedList<>(); // Added in ver 3.3.0
	
	// Annotations added for types
	private boolean isPrimitiveTypeExpected = false ;
	private boolean isUnsignedTypeExpected = false ;
	private boolean isObjectTypeExpected = false ;
	
    private TagContainer tagContainer = new Tags() ;  // Init with void Tags (never null) // v 3.4.0
	
    private BooleanValue insertable = BooleanValue.UNDEFINED; // Added in v 3.3.0
    private BooleanValue updatable  = BooleanValue.UNDEFINED; // Added in v 3.3.0

    private String  size; // String for size with comma ( eg "8,2" ) // Added in v 3.4.0
	private boolean isUnique = false ; // Added in v 3.4.0

	/**
	 * Constructor 
	 * @param name
	 * @param type
	 */
	public DslModelAttribute(String name, String type) {
		super();
		this.name = name;
		this.neutralType = type;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNeutralType() {
		return neutralType;
	}

	@Override
	public String getBooleanFalseValue() {
		return booleanFalseValue;
	}
	public void setBooleanFalseValue(String booleanFalseValue) {
		this.booleanFalseValue = booleanFalseValue;
	}

	@Override
	public String getBooleanTrueValue() {
		return booleanTrueValue;
	}
	public void setBooleanTrueValue(String booleanTrueValue) {
		this.booleanTrueValue = booleanTrueValue;
	}

	@Override
	public String getDatabaseComment() {
		return databaseComment;
	}
	public void setDatabaseComment(String databaseComment) {
		this.databaseComment = databaseComment;
	}

	@Override
	public String getDatabaseDefaultValue() {
		return databaseDefaultValue;
	}
	public void setDatabaseDefaultValue(String databaseDefaultValue) {
		this.databaseDefaultValue = databaseDefaultValue;
	}
	
	@Override
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getDatabaseSize() {
		return databaseSize;
	}
	public void setDatabaseSize(String databaseSize) {
		this.databaseSize = databaseSize;
	}

	@Override
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	@Override
	public String getDateAfterValue() {
		return dateAfterValue;
	}
	public void setDateAfterValue(String dateAfterValue) {
		this.dateAfterValue = dateAfterValue;
	}

	@Override
	public String getDateBeforeValue() {
		return dateBeforeValue;
	}
	public void setDateBeforeValue(String dateBeforeValue) {
		this.dateBeforeValue = dateBeforeValue;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	//----------------------------------------------------------------------------------------
	// Generated value
	//----------------------------------------------------------------------------------------

	@Override
	public GeneratedValueStrategy getGeneratedValueStrategy() {
		return generatedValueStrategy;
	}
	public void setGeneratedValueStrategy(GeneratedValueStrategy strategy) {
		this.generatedValueStrategy = strategy;
	}

	@Override
	public String getGeneratedValueSequenceName() {
		return generatedValueSequenceName;
	}
	public void setGeneratedValueSequenceName(String sequenceName) {
		this.generatedValueSequenceName = sequenceName;
	}

	@Override  // Allocation size (used by 'sequence' and 'table' )
	public Integer getGeneratedValueAllocationSize() {
		return generatedValueAllocationSize;
	}
	public void setGeneratedValueAllocationSize(int v) {
		this.generatedValueAllocationSize = v;
	}

	@Override  // InitialValue (used by 'sequence' and 'table' )
	public Integer getGeneratedValueInitialValue() { // v 4.1.0
		return generatedValueInitialValue;
	}
	public void setGeneratedValueInitialValue(int v) { // v 4.1.0
		this.generatedValueInitialValue = v;
	}
	
	//----------------------------------------------------------------------------------------
	
	@Override
	public String getGeneratedValueTableName() {
		return generatedValueTableName;
	}
	public void setGeneratedValueTableName(String tableName) {
		this.generatedValueTableName = tableName;
	}

	//@Override // TODO : remove or rename 
	public String getGeneratedValueTablePkColumnName() {
		return generatedValueTablePkColumnName;
	}
	public void setGeneratedValueTablePkColumnName(String pkColumnName) {
		this.generatedValueTablePkColumnName = pkColumnName;
	}

	@Override
	public String getGeneratedValueTablePkColumnValue() {
		return generatedValueTablePkColumnValue;
	}
	public void setGeneratedValueTablePkColumnValue(String pkColumnValue) {
		this.generatedValueTablePkColumnValue = pkColumnValue;
	}

	// @Override // TODO : remove or rename 
	public String getGeneratedValueTableValueColumnName() {
		return generatedValueTableValueColumnName;
	}
	public void setGeneratedValueTableValueColumnName(String valueColumnName) {
		this.generatedValueTableValueColumnName = valueColumnName;
	}
	
	//----------------------------------------------------------------------------------------

	@Override
	public String getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	@Override
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	//--- Min / Max Length ( int )
	@Override
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	@Override
	public Integer getMinLength() {
		return minLength;
	}
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}
	
	//--- Min / Max value ( BigDecimal )
	@Override
	public BigDecimal getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}
	@Override
	public BigDecimal getMinValue() {
		return minValue;
	}
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}
	
	@Override
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

// isAutoIncremented - removed in v 4.1

// isDatabaseNotNull - removed in v 4.1

	@Override
	public boolean isDateFuture() { // @Future
		return isDateFuture;
	}
	public void setDateFuture(boolean isDateFuture) { // @Future
		this.isDateFuture = isDateFuture;
	}

	@Override
	public boolean isDatePast() { // @Past
		return isDatePast;
	}
	public void setDatePast(boolean isDatePast) { // @Past
		this.isDatePast = isDatePast;
	}

	@Override
	public boolean isGeneratedValue() {
		return generatedValueStrategy != null 
				&& generatedValueStrategy != GeneratedValueStrategy.UNDEFINED ; // v 3.4.0
		
	}
	
	@Override
	public boolean isKeyElement() {
		return keyElement;
	}
	public void setKeyElement(boolean keyElement) {
		this.keyElement = keyElement;
	}

	@Override
	public boolean isLongText() {
		return longText;
	}
	public void setLongText(boolean longText) {
		this.longText = longText;
	}
	
	@Override
	public boolean isNotBlank() {
		return notBlank;
	}
	public void setNotBlank(boolean v) {
		this.notBlank = v;
	}
	
	@Override
	public boolean isNotEmpty() {
		return notEmpty;
	}
	public void setNotEmpty(boolean v) {
		this.notEmpty = v;
	}
	
	@Override
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean v) {
		this.notNull = v;
	}

	//----------------------------------------------------------------------------------
	// FOREIGN KEY (simple or composite)
	//----------------------------------------------------------------------------------
	@Override
	public boolean isFK() {
		return isForeignKeySimple || isForeignKeyComposite ;
	}

	public void setFKSimple(boolean flag) {
		isForeignKeySimple = flag ;
	}
	@Override
	public boolean isFKSimple() {
		return isForeignKeySimple;
	}

	public void setFKComposite(boolean flag) {
		isForeignKeyComposite = flag ;
	}
	@Override
	public boolean isFKComposite() {
		return isForeignKeyComposite;
	}

	public void setReferencedEntityClassName(String entityClassName) {
		referencedEntityClassName = entityClassName ;
	}
	@Override
	public String getReferencedEntityClassName() {
		if ( isFK() ) {
			return referencedEntityClassName ;
		}
		else {
			return null ;
		}
	}
	
	//----------------------------------------------------------------------------------
	@Override
	public boolean isObjectTypeExpected() {
		return isObjectTypeExpected ;
	}
	public void setObjectTypeExpected(boolean v) {
		this.isObjectTypeExpected = v ;
	}
	
	@Override
	public boolean isPrimitiveTypeExpected() {
		return isPrimitiveTypeExpected ;
	}
	public void setPrimitiveTypeExpected(boolean v) {
		this.isPrimitiveTypeExpected = v ;
	}
	
	@Override
	public boolean isUnsignedTypeExpected() {
		return isUnsignedTypeExpected;
	}
	public void setUnsignedTypeExpected(boolean v) {
		this.isUnsignedTypeExpected = v ;
	}
	
	@Override
	public boolean isUsedInLinks() {
		// In this model an attribute is "used in links" if it's a FK (or pseudo FK)
		return isFK();
	}
	
	@Override
	public boolean isUsedInSelectedLinks() {
		// No "link selection" in this model 
		// Then all links are considered as "selected" => same as "isUsedInLinks()"
		return isUsedInLinks();
	}
	
	//-----------------------------------------------------------------------------------------
	// ATTRIBUTE TAGS  (added in v 3.3.0) 
	//-----------------------------------------------------------------------------------------
	public void setTagContainer(TagContainer tags) { 
		this.tagContainer = tags;
	}
	
	@Override
	public TagContainer getTagContainer() {
		return this.tagContainer;
	}

	//-----------------------------------------------------------------------------------------
	// FOREIGN KEYS in which the attribute is involved ( ver 3.3.0 )
	//-----------------------------------------------------------------------------------------	
	public void addFKPart(ForeignKeyPart fkPart) {
		fkParts.add(fkPart);
	}

	@Override
	public List<ForeignKeyPart> getFKParts() {
		return fkParts;
	}

	@Override
	public boolean hasFKParts() {
		return  ! fkParts.isEmpty() ;
	}
	
    @Override
    public BooleanValue getInsertable() {  // v 3.3.0
        return this.insertable;
    }
    public void setInsertable(BooleanValue b) {  // v 3.3.0
        this.insertable = b;
    }

    @Override
    public BooleanValue getUpdatable() {  // v 3.3.0
        return this.updatable;
    }
    public void setUpdatable(BooleanValue b) {  // v 3.3.0
        this.updatable = b;
    }
    
    @Override
    public boolean isTransient() { // v 3.3.0
        return this.isTransient;
    }
    public void setTransient(boolean b) { // v 3.3.0
        this.isTransient = b;
    }

	@Override 
	public String getSize() { // v 3.4.0
		return size;
	}
	public void setSize(String v) { // v 3.4.0
		this.size = v;
	}

    @Override
    public boolean isUnique() { // v 3.4.0
        return this.isUnique;
    }
    public void setUnique(boolean b) { // v 3.4.0
        this.isUnique = b;
    }

}
