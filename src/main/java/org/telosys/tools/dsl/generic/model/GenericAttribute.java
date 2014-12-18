package org.telosys.tools.dsl.generic.model;

import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;

public class GenericAttribute implements Attribute {
	
	private String name = "";
	private String booleanFalseValue = "";
	private String booleanTrueValue = "";
	private String databaseComment = "";
	private String databaseDefaultValue = "";
	private String databaseName = "";
	private Integer databaseSize = Integer.valueOf(0);
	private String databaseType = "";
	private String dateAfterValue = "";
	private String dateBeforeValue = "";
	private Integer dateType;
	private String defaultValue = "";
	private Entity entity;
	private String fullType = "";
	private String generatedValueGenerator = "";
	private String generatedValueStrategy = "";
	private String initialValue = "";
	private String inputType = "";
	private Integer jdbcTypeCode = Integer.valueOf(0);
	private String jdbcTypeName = "";
	private String label = "";
	private Integer maxLength;
	private Integer maxValue;
	private Integer minLength;
	private Integer minValue;
	private String pattern = "";
	private Integer sequenceGeneratorAllocationSize;
	private String sequenceGeneratorName = "";
	private String sequenceGeneratorSequenceName = "";
	private String simpleType = "";
	private String tableGeneratorName = "";
	private String tableGeneratorPkColumnName = "";
	private String tableGeneratorPkColumnValue = "";
	private String tableGeneratorTable = "";
	private String tableGeneratorValueColumnName = "";
	private String type = "";
	private String wrapperType = "";
	private boolean autoIncremented;
	private boolean databaseNotNull;
	private boolean generatedValue;
	private boolean keyElement;
	private boolean longText;
	private boolean notBlank;
	private boolean notEmpty;
	private boolean notNull;
	private boolean selected;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBooleanFalseValue() {
		return booleanFalseValue;
	}
	public void setBooleanFalseValue(String booleanFalseValue) {
		this.booleanFalseValue = booleanFalseValue;
	}
	public String getBooleanTrueValue() {
		return booleanTrueValue;
	}
	public void setBooleanTrueValue(String booleanTrueValue) {
		this.booleanTrueValue = booleanTrueValue;
	}
	public String getDatabaseComment() {
		return databaseComment;
	}
	public void setDatabaseComment(String databaseComment) {
		this.databaseComment = databaseComment;
	}
	public String getDatabaseDefaultValue() {
		return databaseDefaultValue;
	}
	public void setDatabaseDefaultValue(String databaseDefaultValue) {
		this.databaseDefaultValue = databaseDefaultValue;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public Integer getDatabaseSize() {
		return databaseSize;
	}
	public void setDatabaseSize(Integer databaseSize) {
		this.databaseSize = databaseSize;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getDateAfterValue() {
		return dateAfterValue;
	}
	public void setDateAfterValue(String dateAfterValue) {
		this.dateAfterValue = dateAfterValue;
	}
	public String getDateBeforeValue() {
		return dateBeforeValue;
	}
	public void setDateBeforeValue(String dateBeforeValue) {
		this.dateBeforeValue = dateBeforeValue;
	}
	public Integer getDateType() {
		return dateType;
	}
	public void setDateType(Integer dateType) {
		this.dateType = dateType;
	}
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
	public String getFullType() {
		return fullType;
	}
	public void setFullType(String fullType) {
		this.fullType = fullType;
	}
	public String getGeneratedValueGenerator() {
		return generatedValueGenerator;
	}
	public void setGeneratedValueGenerator(String generatedValueGenerator) {
		this.generatedValueGenerator = generatedValueGenerator;
	}
	public String getGeneratedValueStrategy() {
		return generatedValueStrategy;
	}
	public void setGeneratedValueStrategy(String generatedValueStrategy) {
		this.generatedValueStrategy = generatedValueStrategy;
	}
	public String getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public Integer getJdbcTypeCode() {
		return jdbcTypeCode;
	}
	public void setJdbcTypeCode(Integer jdbcTypeCode) {
		this.jdbcTypeCode = jdbcTypeCode;
	}
	public String getJdbcTypeName() {
		return jdbcTypeName;
	}
	public void setJdbcTypeName(String jdbcTypeName) {
		this.jdbcTypeName = jdbcTypeName;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public Integer getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}
	public Integer getMinLength() {
		return minLength;
	}
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}
	public Integer getMinValue() {
		return minValue;
	}
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public Integer getSequenceGeneratorAllocationSize() {
		return sequenceGeneratorAllocationSize;
	}
	public void setSequenceGeneratorAllocationSize(
			Integer sequenceGeneratorAllocationSize) {
		this.sequenceGeneratorAllocationSize = sequenceGeneratorAllocationSize;
	}
	public String getSequenceGeneratorName() {
		return sequenceGeneratorName;
	}
	public void setSequenceGeneratorName(String sequenceGeneratorName) {
		this.sequenceGeneratorName = sequenceGeneratorName;
	}
	public String getSequenceGeneratorSequenceName() {
		return sequenceGeneratorSequenceName;
	}
	public void setSequenceGeneratorSequenceName(
			String sequenceGeneratorSequenceName) {
		this.sequenceGeneratorSequenceName = sequenceGeneratorSequenceName;
	}
	public String getSimpleType() {
		return simpleType;
	}
	public void setSimpleType(String simpleType) {
		this.simpleType = simpleType;
	}
	public String getTableGeneratorName() {
		return tableGeneratorName;
	}
	public void setTableGeneratorName(String tableGeneratorName) {
		this.tableGeneratorName = tableGeneratorName;
	}
	public String getTableGeneratorPkColumnName() {
		return tableGeneratorPkColumnName;
	}
	public void setTableGeneratorPkColumnName(String tableGeneratorPkColumnName) {
		this.tableGeneratorPkColumnName = tableGeneratorPkColumnName;
	}
	public String getTableGeneratorPkColumnValue() {
		return tableGeneratorPkColumnValue;
	}
	public void setTableGeneratorPkColumnValue(String tableGeneratorPkColumnValue) {
		this.tableGeneratorPkColumnValue = tableGeneratorPkColumnValue;
	}
	public String getTableGeneratorTable() {
		return tableGeneratorTable;
	}
	public void setTableGeneratorTable(String tableGeneratorTable) {
		this.tableGeneratorTable = tableGeneratorTable;
	}
	public String getTableGeneratorValueColumnName() {
		return tableGeneratorValueColumnName;
	}
	public void setTableGeneratorValueColumnName(
			String tableGeneratorValueColumnName) {
		this.tableGeneratorValueColumnName = tableGeneratorValueColumnName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWrapperType() {
		return wrapperType;
	}
	public void setWrapperType(String wrapperType) {
		this.wrapperType = wrapperType;
	}
	public boolean isAutoIncremented() {
		return autoIncremented;
	}
	public void setAutoIncremented(boolean autoIncremented) {
		this.autoIncremented = autoIncremented;
	}
	public boolean isDatabaseNotNull() {
		return databaseNotNull;
	}
	public void setDatabaseNotNull(boolean databaseNotNull) {
		this.databaseNotNull = databaseNotNull;
	}
	public boolean isGeneratedValue() {
		return generatedValue;
	}
	public void setGeneratedValue(boolean generatedValue) {
		this.generatedValue = generatedValue;
	}
	public boolean isKeyElement() {
		return keyElement;
	}
	public void setKeyElement(boolean keyElement) {
		this.keyElement = keyElement;
	}
	public boolean isLongText() {
		return longText;
	}
	public void setLongText(boolean longText) {
		this.longText = longText;
	}
	public boolean isNotBlank() {
		return notBlank;
	}
	public void setNotBlank(boolean notBlank) {
		this.notBlank = notBlank;
	}
	public boolean isNotEmpty() {
		return notEmpty;
	}
	public void setNotEmpty(boolean notEmpty) {
		this.notEmpty = notEmpty;
	}
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
