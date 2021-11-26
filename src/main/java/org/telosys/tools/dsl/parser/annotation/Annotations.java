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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.AnnotationName;

public class Annotations {

	private static List<AnnotationDefinition> annotationsList = new LinkedList<>();
	static {
		annotationsList.add(new AnnotationDefinition(AnnotationName.ID        ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.AUTO_INCREMENTED        ));

		annotationsList.add(new AnnotationDefinition(AnnotationName.NOT_NULL  ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.NOT_EMPTY ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.NOT_BLANK ));
		
		annotationsList.add(new AnnotationDefinition(AnnotationName.MIN,       AnnotationParamType.DECIMAL ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.MAX,       AnnotationParamType.DECIMAL ));
		
		annotationsList.add(new AnnotationDefinition(AnnotationName.SIZE_MIN,  AnnotationParamType.INTEGER));
		annotationsList.add(new AnnotationDefinition(AnnotationName.SIZE_MAX,  AnnotationParamType.INTEGER ));

		annotationsList.add(new AnnotationDefinition(AnnotationName.PAST       ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.FUTURE     ));

		annotationsList.add(new AnnotationDefinition(AnnotationName.PRIMITIVE_TYPE ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.UNSIGNED_TYPE  ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.OBJECT_TYPE    ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.LONG_TEXT      ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.EMBEDDED       ));
	
		//--- Added in ver 3.2.0
		annotationsList.add(new AnnotationDefinition(AnnotationName.DEFAULT_VALUE , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.INITIAL_VALUE , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.LABEL         , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.INPUT_TYPE    , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.PATTERN       , AnnotationParamType.STRING ));

		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_NAME          , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_TYPE          , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_DEFAULT_VALUE , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_COMMENT       , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_SIZE          , AnnotationParamType.STRING ));
		
		//--- Added in ver 3.3.0
		annotationsList.add(new AnnotationDefinition(AnnotationName.FK                  , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.OPTIONAL            ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.TRANSIENT           ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.FETCH_TYPE_EAGER    ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.FETCH_TYPE_LAZY     ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.MAPPED_BY           , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_ATTR        , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_COL         , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_FK          , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.MANY_TO_MANY        , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.ONE_TO_ONE          , AnnotationParamType.STRING ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.INSERTABLE          , AnnotationParamType.BOOLEAN ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.UPDATABLE           , AnnotationParamType.BOOLEAN ));
		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_JOIN_ENTITY , AnnotationParamType.STRING ));
		
		//--- Added in ver 3.4.0
		annotationsList.add(new AnnotationDefinition(AnnotationName.SIZE ,      AnnotationParamType.STRING ));
	}
	
	private Annotations() {
	}

	/**
	 * Returns all annotations definitions
	 * @return
	 */
	public static List<AnnotationDefinition> getAll() {
		return annotationsList;
	}

	/**
	 * Return annotation definition for the given annotation name (or null if none)
	 * @param annotationName
	 * @return
	 */
	public static AnnotationDefinition get(String annotationName) {
		for ( AnnotationDefinition ad : annotationsList ) {
			if ( ad.getName().equals(annotationName) ) {
				return ad;
			}
		}
		return null ;
	}
}
