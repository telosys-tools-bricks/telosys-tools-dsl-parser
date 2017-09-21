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

	public final static String ID = "Id" ;
	
	public final static String NOT_NULL  = "NotNull" ;
	public final static String NOT_EMPTY = "NotEmpty" ;
	public final static String NOT_BLANK = "NotBlank" ;
	
	public final static String MIN = "Min" ;
	public final static String MAX = "Max" ;
	
	public final static String SIZE_MIN = "SizeMin" ;
	public final static String SIZE_MAX = "SizeMax" ;
	
	public final static String PAST   = "Past" ;
	public final static String FUTURE = "Future" ;

	public final static String EMBEDDED = "Embedded" ;

	public final static String AUTO_INCREMENTED = "AutoIncremented" ;
	
	public final static String LONG_TEXT = "LongText" ;

	// Types 
	public final static String PRIMITIVE_TYPE = "PrimitiveType" ;
	public final static String OBJECT_TYPE    = "ObjectType" ;
	public final static String UNSIGNED_TYPE  = "UnsignedType" ;
	public final static String SQL_TYPE       = "SqlType" ;

	// TODO 
    // @DefaultValue(xxx) or  @InitValue(xxx)
    // @Comment(xxx) --> used as DbComment ?
    // @After(DateISO)
    // @Before(DateISO)
    //
    // @Label(xxx)
    // @InputType(xxx) or config ???
    // @Pattern(xxx) or @RegExp(xxx) ???
    //
    // @DbColumn(xxx)
    // @DbType(xxx)
    // @DbDefaultValue(xxx)
	
}