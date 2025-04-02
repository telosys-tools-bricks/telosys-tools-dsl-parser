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
package org.telosys.tools.dsl.parser.annotation;


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
	// public static final String SQL_TYPE       = "SqlType" ; // Removed in v 3.3.0

	// Added in ver 3.2.0
	public static final String DEFAULT_VALUE   = "DefaultValue" ; // v 3.2.0
	public static final String INITIAL_VALUE   = "InitialValue" ; // v 3.2.0
	public static final String LABEL           = "Label";     // v 3.2.0
	public static final String INPUT_TYPE      = "InputType"; // v 3.2.0
	public static final String PATTERN         = "Pattern";   // v 3.2.0
	
	public static final String DB_NAME          = "DbName"; // v 3.2.0
	public static final String DB_TYPE          = "DbType"; // v 3.2.0
	public static final String DB_COMMENT       = "DbComment"; // v 3.2.0
	public static final String DB_DEFAULT_VALUE = "DbDefaultValue"; // v 3.2.0

	public static final String DB_SIZE          = "DbSize"; // v 3.2.0 (!) DEPRECATED in v 3.4.0

	// Added in ver 3.3.0 : for attributes
	public static final String FK               = "FK"; // v 3.3.0
	// Added in ver 3.3.0 : for links
	public static final String OPTIONAL         = "Optional"; // v 3.3.0
	public static final String TRANSIENT        = "Transient"; // v 3.3.0
	public static final String FETCH_TYPE_LAZY  = "FetchTypeLazy"; // v 3.3.0
	public static final String FETCH_TYPE_EAGER = "FetchTypeEager"; // v 3.3.0
	public static final String MAPPED_BY        = "MappedBy"; // v 3.3.0
	public static final String LINK_BY_ATTR     = "LinkByAttr"; // v 3.3.0
	public static final String LINK_BY_COL      = "LinkByCol"; // v 3.3.0
	public static final String LINK_BY_FK       = "LinkByFK"; // v 3.3.0
	public static final String MANY_TO_MANY     = "ManyToMany"; // v 3.3.0
	public static final String ONE_TO_ONE       = "OneToOne"; // v 3.3.0
	public static final String INSERTABLE       = "Insertable"; // v 3.3.0
	public static final String UPDATABLE        = "Updatable"; // v 3.3.0
	public static final String LINK_BY_JOIN_ENTITY = "LinkByJoinEntity"; // v 3.3.0

	// Added in ver 3.4.0 : ATTRIBUTE scope
	public static final String SIZE      = "Size";   // v 3.4.0
	public static final String UNIQUE    = "Unique"; // v 3.4.0
	public static final String MIN_LEN   = "MinLen" ; // v 3.4.0
	public static final String MAX_LEN   = "MaxLen" ; // v 3.4.0
	public static final String GENERATED_VALUE   = "GeneratedValue" ; // v 3.4.0

	// Added 2025-04-2
	public static final String SCALE  = "Scale";
	public static final String PRECISION  = "Precision";

	// Added in ver 3.4.0 : ENITY scope
	public static final String DB_TABLE      = "DbTable";      // v 3.4.0
	public static final String DB_VIEW       = "DbView";       // v 3.4.0
	public static final String DB_SCHEMA     = "DbSchema";     // v 3.4.0
	public static final String DB_CATALOG    = "DbCatalog";    // v 3.4.0
	public static final String DB_TABLESPACE = "DbTablespace"; // v 3.4.0
	
	// Added in ver 3.4.0 : ENITY scope / new data
	public static final String EXTENDS   = "Extends"; // v 3.4.0
	public static final String ABSTRACT  = "Abstract"; // v 3.4.0
	public static final String PACKAGE   = "Package"; // v 3.4.0
	public static final String READ_ONLY   = "ReadOnly"; // v 3.4.0
	public static final String IN_MEMORY_REPOSITORY = "InMemoryRepository"; // v 3.4.0

	public static final String AGGREGATE_ROOT = "AggregateRoot";  // v 3.4.0 (DDD)
	public static final String DOMAIN         = "Domain";   // v 3.4.0 (DDD)
	public static final String CONTEXT        = "Context";  // v 3.4.0 (DDD)
	
	public static final String ORPHAN_REMOVAL = "OrphanRemoval"; // v 4.1.0 (ORM)
	public static final String CASCADE        = "Cascade";       // v 4.1.0 (ORM)
	public static final String JOIN_ENTITY    = "JoinEntity";    // v 4.1.0
	
	// NB : don't forget to add each new annotation in "AnnotationDefinitions.java"

	// new annotations (in the future) ?
    // @After(DateISO/TimeISO/TimestampISO)
    // @Before(DateISO/TimeISO/TimestampISO)
    // @Comment(xxx) 
    // @ExternalEntity
}