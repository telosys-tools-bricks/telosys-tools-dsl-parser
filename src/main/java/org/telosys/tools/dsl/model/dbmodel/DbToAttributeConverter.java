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
import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.generic.model.enums.DateType;
import org.telosys.tools.generic.model.types.NeutralType;

public class DbToAttributeConverter {

	/**
	 * Constructor
	 */
	public DbToAttributeConverter() {
		super();
	}
	
	/**
	 * Creates an attribute from the given database column
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
    	// DB name
		attribute.setDatabaseName(dbCol.getColumnName());
    	// DB type
		attribute.setDatabaseType(buildSqlFullType(dbCol)); 
    	// DB not null
		attribute.setDatabaseNotNull(dbCol.isNotNull());
    	// DB size
    	if ( ! StrUtil.nullOrVoid(size) ) {
    		attribute.setDatabaseSize(size);
    	}
    	// DB default value
    	if ( ! StrUtil.nullOrVoid(dbCol.getDefaultValue()) ) {
    		// default value is returned between single quotes => remove single quotes if any
    		//attribute.setDatabaseDefaultValue( StrUtil.removeQuotes(dbCol.getDefaultValue(), '\'') ); 
    		attribute.setDatabaseDefaultValue( AttributeUtils.cleanDefaultValue(dbCol.getDefaultValue()) ); 
    	}
    	// DB comment
    	if ( ! StrUtil.nullOrVoid(dbCol.getDefaultValue()) ) {
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
