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
package org.telosys.tools.dsl;


public class AnnotationName {

	private AnnotationName() {
	}
	
	public static final String ID = "Id" ;
	
	public static final String NOT_NULL  = "NotNull" ;
	public static final String NOT_EMPTY = "NotEmpty" ;
	public static final String NOT_BLANK = "NotBlank" ;
	
	public static final String MIN = "Min" ;
	public static final String MAX = "Max" ;
	
	public static final String SIZE_MIN = "SizeMin" ;
	public static final String SIZE_MAX = "SizeMax" ;
	

	public static final String PAST   = "Past" ;
	public static final String FUTURE = "Future" ;

	public static final String EMBEDDED = "Embedded" ;

	public static final String AUTO_INCREMENTED = "AutoIncremented" ;
	
	public static final String LONG_TEXT = "LongText" ;

	// Types 
	public static final String PRIMITIVE_TYPE = "PrimitiveType" ;
	public static final String OBJECT_TYPE    = "ObjectType" ;
	public static final String UNSIGNED_TYPE  = "UnsignedType" ;
	public static final String SQL_TYPE       = "SqlType" ;

	// Added in ver 3.2.0
	public static final String DEFAULT_VALUE   = "DefaultValue" ; // v 3.2.0
	public static final String INITIAL_VALUE   = "InitialValue" ; // v 3.2.0
	public static final String LABEL           = "Label";     // v 3.2.0
	public static final String INPUT_TYPE      = "InputType"; // v 3.2.0
	public static final String PATTERN         = "Pattern";   // v 3.2.0
	
	// TODO 
    // @Comment(xxx) --> used as DbComment ?
    // @After(DateISO)
    // @Before(DateISO)
    //
    // @Pattern(xxx) or @RegExp(xxx) ???
    //
    // @DbColumn(xxx)
    // @DbType(xxx)
    // @DbDefaultValue(xxx)
	
}