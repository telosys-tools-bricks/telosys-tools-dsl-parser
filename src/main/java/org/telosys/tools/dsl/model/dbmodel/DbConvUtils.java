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
import org.telosys.tools.generic.model.types.NeutralType;

/**
 * Repository (model) generator
 * 
 * @author Laurent GUERIN
 * 
 */
public class DbConvUtils {

	/**
	 * Constructor
	 */
	private DbConvUtils() {
	}

	/**
	 * Conversion for database types : NUMERIC(precision, scale) and DECIMAL(precision, scale)
	 * @param precision
	 * @param scale
	 * @return
	 * @since 4.2.1
	 */
	protected static String getNumericOrDecimalAttributeType(int precision, int scale) {
		if ( scale == 0 ) {
			if ( precision == 0 ) {
				// No precision and no scale (eg "NUMERIC") => "decimal"
				return NeutralType.DECIMAL;
			}
			// No "scale" = No "fractional digits" => INTEGER : int or long or short 
			else if ( precision > 18 ) { // exceed max value for "long"
				// Max value for "long" in most languages 9,223,372,036,854,775,807
				return NeutralType.DECIMAL;
			}
			else if ( precision > 9 ) { // exceed max value for "int"
				// Max value for "int" in Java/C/C++/C#/Python : 2,147,483,647 ( or 4,294,967,295 if unsigned )
				return NeutralType.LONG;
			}
			else if ( precision > 4 ) { //  9,999
				// Max value for "short" : 32,767 
				return NeutralType.INTEGER;
			}
			else {
				return NeutralType.SHORT;
			}
		}
		else {
			// Scale is specified and != 0 => "decimal"
			return NeutralType.DECIMAL;
		}
	}
	private static String detectSpecificType(int jdbcSqlType, String dbTypeName) {
		if ( "uuid".equalsIgnoreCase(dbTypeName) && jdbcSqlType == Types.OTHER ) {
			// PostgreSQL UUID 
			return NeutralType.UUID;
		}
		if ( "uniqueidentifier".equalsIgnoreCase(dbTypeName) && ( jdbcSqlType == Types.CHAR || jdbcSqlType == Types.BINARY ) ) {
			// SQL Server UUID 
			return NeutralType.UUID;
		}
		return null;
	}
	
	protected static String getAttributeType(int jdbcSqlType, int size, int decimalDigits, String dbTypeName) {

		String specificType = detectSpecificType(jdbcSqlType, dbTypeName);
		if ( specificType != null ) {
			return specificType ;
		}
			
		switch (jdbcSqlType) {

		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.LONGNVARCHAR: // Unicode character data of arbitrary length
			return NeutralType.STRING;

		case Types.BIT:
		case Types.BOOLEAN:
			return NeutralType.BOOLEAN;

		case Types.TINYINT:
			return NeutralType.BYTE;
		case Types.SMALLINT:
			return NeutralType.SHORT;
		case Types.INTEGER:
			return NeutralType.INTEGER;
		case Types.BIGINT:
			return NeutralType.LONG;

		case Types.REAL:
			return NeutralType.FLOAT;
		case Types.FLOAT:
		case Types.DOUBLE:
			return NeutralType.DOUBLE;

		case Types.NUMERIC:
		case Types.DECIMAL:
			// for "numeric" or "decimal" types "size" = "precision" and "decimalDigits" = "scale"
			return getNumericOrDecimalAttributeType(size, decimalDigits); // v 4.2.1 (instead of "DECIMAL" always)

		case Types.DATE:
			return NeutralType.DATE;
			
		case Types.TIME_WITH_TIMEZONE:  // ver 4.3.0
		case Types.TIME:
			// TIME_WITH_TIMEZONE = new SQL type code added in JDBC 4.2 but not returned by PostgreSQL => check type
			return getNeutralTypeForTime(jdbcSqlType, dbTypeName);  // ver 4.3.0
			
		case Types.TIMESTAMP_WITH_TIMEZONE: // ver 4.3.0
		case Types.TIMESTAMP:
			// TIMESTAMP_WITH_TIMEZONE = new SQL type code added in JDBC 4.2 but not returned by PostgreSQL => check type
			return getNeutralTypeForTimestamp(jdbcSqlType, dbTypeName);  // ver 4.3.0

		case Types.CLOB: // Character Large Object
			return NeutralType.STRING;

		case Types.BLOB: // Binary Large Object
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return NeutralType.BINARY;

		default:
			return NeutralType.STRING;
		}
		// --- Non supported types :
		// Types.ARRAY
		// Types.DISTINCT
		// Types.STRUCT
		// Types.REF
		// Types.DATALINK
	}

	protected static String getNeutralTypeForTimestamp(int jdbcSqlType, String dbTypeName) {
		if ( jdbcSqlType == Types.TIMESTAMP_WITH_TIMEZONE ) {
			// TIMESTAMP_WITH_TIMEZONE (value=2014) = new SQL type code added in JDBC 4.2 but not returned by PostgreSQL 
			// if supported by the JDBC driver => it's a TIMESTAMP_WITH_TIMEZONE
			return NeutralType.DATETIMETZ;
		}
		else if ( jdbcSqlType == Types.TIMESTAMP ) {
			// TIMESTAMP (value=93) => can be return by the JDBC driver even for a TIMESTAMP_WITH_TIMEZONE !
			if ( endsWithTimeZoneString(dbTypeName) ) {
				// Can be considered to have a time zone
				return NeutralType.DATETIMETZ;
			}
			else {
				// Supposed to have no time zone
				return NeutralType.DATETIME;
			}
		}
		// By default keep the standard conversion without time zone
		return NeutralType.DATETIME;
	}
	
	protected static String getNeutralTypeForTime(int jdbcSqlType, String dbTypeName) {
		if ( jdbcSqlType == Types.TIME_WITH_TIMEZONE ) {
			// TIME_WITH_TIMEZONE (value=2013) = new SQL type code added in JDBC 4.2 but not returned by PostgreSQL 
			// if returned by the JDBC driver => it's a TIME_WITH_TIMEZONE
			return NeutralType.TIMETZ;
		}
		else if ( jdbcSqlType == Types.TIME ) {
			// TIME (value=92) => can be return by the JDBC driver even if WITH_TIMEZONE !
			if ( endsWithTimeZoneString(dbTypeName) ) {
				// Can be considered to have a time zone
				return NeutralType.TIMETZ;
			}
			else {
				// Supposed to have no time zone
				return NeutralType.TIME;
			}
		}
		// By default keep the standard conversion without time zone
		return NeutralType.TIME;
	}
	
	private static boolean endsWithTimeZoneString(String dbTypeName) {
		if (dbTypeName != null) {
			String dbTypeNameLC = dbTypeName.toLowerCase();
			// PostgreSQL : dbTypeName = "timestamptz" (even if created with "timestamp with time zone")
			// SQLserver  : dbTypeName = "datetimeoffset" ??? (to be verified)
			return dbTypeNameLC.endsWith("tz") || dbTypeNameLC.endsWith("zone") || dbTypeNameLC.endsWith("offset") ;
		}
		return false;
	}
	
	/**
	 * Clean the raw default value <br>
	 * With some databases (eg Oracle) the value may contain special characters
	 * like CR/LF <br>
	 * The value is sometimes surrounded by quotes
	 * 
	 * @param value
	 * @return
	 */
	protected static String cleanDefaultValue(String value) {
		String v2 = removeSpecialCharacters(value);
		String v3 = v2.trim();
		if ( v3.startsWith("'") ) {
			return StrUtil.removeQuotes(v3, '\''); // remove surrounded quotes if any
		}
		else if ( v3.startsWith("\"") ) {
			return StrUtil.removeQuotes(v3, '"'); // remove surrounded quotes if any
		}
		else {
			return v3;
		}
	}

	/**
	 * Clean the raw comment value (for TABLE comment and COLUMN comment)
	 * @param value
	 * @return
	 * @since 4.1.1
	 */
	protected static String cleanComment(String value) {
		return removeSpecialCharacters(value);
	}
	
	/**
	 * Replaces all 'control-char' by a blank (eg CR, LF, etc)
	 * @param value
	 * @return
	 */
	protected static String removeSpecialCharacters(String value) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c >= ' ') {
				// Not a special character => keep it
				sb.append(c);
			} 
			else {
				// Replace Ctrl-char by a blank
				sb.append(' ');
			}
		}
		return sb.toString();
	}

}
