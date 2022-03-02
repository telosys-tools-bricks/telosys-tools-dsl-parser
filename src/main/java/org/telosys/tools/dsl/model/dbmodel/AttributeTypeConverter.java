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

import org.telosys.tools.generic.model.types.NeutralType;

/**
 * Repository (model) generator 
 * 
 * @author Laurent GUERIN
 * 
 */
public class AttributeTypeConverter {
	
	/**
	 * Constructor
	 */
	private AttributeTypeConverter() { 
	}

    public static String getAttributeType(int jdbcSqlType) {
    	
    	switch(jdbcSqlType) {
    	
    	case Types.CHAR:
    	case Types.VARCHAR:
    	case Types.LONGVARCHAR:
    	case Types.LONGNVARCHAR: // Unicode character data of arbitrary length
    		return NeutralType.STRING;

    	case Types.BIT:
    	case Types.BOOLEAN:
    		return NeutralType.BOOLEAN;
    		
    	case Types.TINYINT : 
    		return NeutralType.BYTE ;
    	case Types.SMALLINT : 
    		return NeutralType.SHORT ;
    	case Types.INTEGER : 
    		return NeutralType.INTEGER ;
    	case Types.BIGINT : 
    		return NeutralType.LONG ;

    	case Types.REAL : 
    		return NeutralType.FLOAT ;
    	case Types.FLOAT : 
    	case Types.DOUBLE : 
    		return NeutralType.DOUBLE ;

    	case Types.NUMERIC : 
    	case Types.DECIMAL : 
    		return NeutralType.DECIMAL ;

    	case Types.DATE : 
    		return NeutralType.DATE ;
    	case Types.TIME : 
    		return NeutralType.TIME ;
    	case Types.TIMESTAMP : 
    		return NeutralType.TIMESTAMP ;
    		
    	case Types.CLOB : // Character Large Object 
    		return NeutralType.STRING ;
    		
    	case Types.BLOB : // Binary Large Object
    	case Types.BINARY : 
    	case Types.VARBINARY : 
    	case Types.LONGVARBINARY : 
    		return NeutralType.BINARY ;

    	default : 
    		return NeutralType.STRING ;
    	}
		//--- Non supported types :
		// Types.ARRAY
		// Types.DISTINCT
		// Types.STRUCT
		// Types.REF
		// Types.DATALINK
    }

}
