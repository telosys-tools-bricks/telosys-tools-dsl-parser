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
package org.telosys.tools.dsl.model.dbmodel;

import java.sql.Types;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.generic.model.enums.DateType;
import org.telosys.tools.generic.model.types.NeutralType;

/**
 * Converter : Database column to DSL model attribute
 * @author Laurent Guerin
 *
 */
public class DbToAttributeConverter {

	private final DatabaseDefinition databaseDefinition;
	
	/**
	 * Constructor
	 */
	public DbToAttributeConverter(DatabaseDefinition databaseDefinition) {
		super();
		this.databaseDefinition = databaseDefinition;
	}

	/**
	 * Creates a model attribute from the given database column
	 * @param dbCol
	 * @return
	 */
	public DslModelAttribute createAttribute(DatabaseColumn dbCol ) {

		//--- Create a new "attribute" for this "column"
		String attributeName = NameConverter.columnNameToAttributeName(dbCol.getColumnName());
		String attributeType = AttributeUtils.getAttributeType(dbCol.getJdbcTypeCode());
		DslModelAttribute attribute = new DslModelAttribute(attributeName, attributeType);
		
		String size = builSize(dbCol); // usable as attribute size and database size 
		//--- Database info (@DbXxx)
		if ( databaseDefinition.isDbName() ) { // v 4.1.0
			attribute.setDatabaseName(dbCol.getColumnName());
		}
		if ( databaseDefinition.isDbType() ) { // v 4.1.0
			attribute.setDatabaseType(buildSqlFullType(dbCol));
		}
    	// DB not null
		// attribute.setDatabaseNotNull(dbCol.isNotNull()); // removed in v 4.1
		
    	// DB size
//    	if ( ! StrUtil.nullOrVoid(size) ) {
//    		attribute.setDatabaseSize(size); // removed in v 4.1
//    	}
    	// DB default value (if option is TRUE in config file) // v 4.1.0
    	if ( databaseDefinition.isDbDefaultValue() && ( ! StrUtil.nullOrVoid(dbCol.getDefaultValue()) ) ) {
    		// default value is returned between single quotes => remove single quotes if any
    		attribute.setDatabaseDefaultValue( AttributeUtils.cleanDefaultValue(dbCol.getDefaultValue()) ); 
    	}
    	// DB comment (if option is TRUE in config file) // v 4.1.0
    	if ( databaseDefinition.isDbComment() && ( ! StrUtil.nullOrVoid(dbCol.getComment()) ) ) { // Bug Fix ver 4.1.0 : dbCol.getDefaultValue() --> dbCol.getComment()
    		attribute.setDatabaseComment( dbCol.getComment() ); 
    	}

		//--- Other info 
		// Is this attribute/column in the Table Primary Key ?
		attribute.setKeyElement( dbCol.isInPrimaryKey()); 
		// Is this column auto-incremented ?
		attribute.setAutoIncremented(dbCol.isAutoIncremented());
		// Attribute size
    	if ( ! StrUtil.nullOrVoid(size) ) {
    		attribute.setSize(size);
    	}
		// Is it a LONG TEXT ? ( BLOB, CLOB, etc )
		if ( isAttributeLongText (dbCol) ) {
			attribute.setLongText(true);
		}
		// Is it Not Null and Not Empty ?
		if ( dbCol.isNotNull()  ) {
			attribute.setNotNull(true);
			attribute.setNotEmpty(true);
		}
		// Set a "Max Length" if any 
		if ( NeutralType.STRING.equals(attribute.getNeutralType()) ) {
			if ( dbCol.getSize() > 0 ) { // 0 if size is not defined or not applicable
				attribute.setMaxLength(dbCol.getSize());
			}
		}

		// Date type ??? To be removed ???
		attribute.setDateType(getAttributeDateType(dbCol.getJdbcTypeCode()));
		
		return attribute ;
	}
	
    private DateType getAttributeDateType(int jdbcColumnType ) {
    	switch ( jdbcColumnType ) {
    		//--- Type of Date :
    		case Types.DATE : 
    			return DateType.DATE_ONLY ;
    		case Types.TIME : 
    			return DateType.TIME_ONLY ;
    		case Types.TIMESTAMP : 
    			return DateType.DATE_AND_TIME ;
    		default:
    			return DateType.UNDEFINED ;
    	}
    }
	
    private String builSize(DatabaseColumn dbCol) {
    	switch(dbCol.getJdbcTypeCode()) {
	    	case Types.CHAR:
	    	case Types.VARCHAR:
	    	case Types.LONGVARCHAR:
	    	case Types.LONGNVARCHAR: // Unicode character data of arbitrary length
	    	case Types.BINARY : 
	    	case Types.VARBINARY : 
	    	case Types.LONGVARBINARY : 
	    		if ( dbCol.getSize() > 0 ) {
	    			return "" + dbCol.getSize() ;
	    		}
	    		break;
	    	case Types.NUMERIC : 
	    	case Types.DECIMAL : 
	    		if ( dbCol.getSize() > 0 ) {
	    			if ( dbCol.getDecimalDigits() > 0 ) {
	    				return dbCol.getSize() + "," + dbCol.getDecimalDigits() ;
	    			}
	    			else {
		    			return "" + dbCol.getSize() ;
	    			}
	    		}
	    		break;
    	}
    	return "";
    }
    
    private String buildSqlFullType(DatabaseColumn dbCol) {
    	String size = builSize(dbCol);
    	if ( StrUtil.nullOrVoid(size) ) {
    		return dbCol.getDbTypeName() ;
    	}
    	else {
    		return dbCol.getDbTypeName() + "(" + size + ")";
    	}
    }
    
    private boolean isAttributeLongText (DatabaseColumn dbCol) {
    	switch(dbCol.getJdbcTypeCode()) {
    	case Types.LONGVARCHAR :
    	case Types.CLOB :
    	case Types.BLOB :
    		return true ; // Considered as a "Long Text"
    	default:
        	return false ;
    	}
    }
		
}
