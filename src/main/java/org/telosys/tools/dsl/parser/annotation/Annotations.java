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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.annotations.AutoIncrementedAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbCommentAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbDefaultValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbNameAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbSizeAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.DefaultValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.EmbeddedAnnotation;
import org.telosys.tools.dsl.parser.annotations.FetchTypeEagerAnnotation;
import org.telosys.tools.dsl.parser.annotations.FetchTypeLazyAnnotation;
import org.telosys.tools.dsl.parser.annotations.FkAnnotation;
import org.telosys.tools.dsl.parser.annotations.FutureAnnotation;
import org.telosys.tools.dsl.parser.annotations.IdAnnotation;
import org.telosys.tools.dsl.parser.annotations.InitialValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.InputTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.InsertableAnnotation;
import org.telosys.tools.dsl.parser.annotations.LabelAnnotation;
import org.telosys.tools.dsl.parser.annotations.LinkByAttrAnnotation;
import org.telosys.tools.dsl.parser.annotations.LinkByColAnnotation;
import org.telosys.tools.dsl.parser.annotations.LinkByFKAnnotation;
import org.telosys.tools.dsl.parser.annotations.LinkByJoinEntityAnnotation;
import org.telosys.tools.dsl.parser.annotations.LongTextAnnotation;
import org.telosys.tools.dsl.parser.annotations.ManyToManyAnnotation;
import org.telosys.tools.dsl.parser.annotations.MappedByAnnotation;
import org.telosys.tools.dsl.parser.annotations.MaxAnnotation;
import org.telosys.tools.dsl.parser.annotations.MaxLenAnnotation;
import org.telosys.tools.dsl.parser.annotations.MinAnnotation;
import org.telosys.tools.dsl.parser.annotations.MinLenAnnotation;
import org.telosys.tools.dsl.parser.annotations.NotBlankAnnotation;
import org.telosys.tools.dsl.parser.annotations.NotEmptyAnnotation;
import org.telosys.tools.dsl.parser.annotations.NotNullAnnotation;
import org.telosys.tools.dsl.parser.annotations.ObjectTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.OneToOneAnnotation;
import org.telosys.tools.dsl.parser.annotations.OptionalAnnotation;
import org.telosys.tools.dsl.parser.annotations.PastAnnotation;
import org.telosys.tools.dsl.parser.annotations.PatternAnnotation;
import org.telosys.tools.dsl.parser.annotations.PrimitiveTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeMaxAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeMinAnnotation;
import org.telosys.tools.dsl.parser.annotations.TransientAnnotation;
import org.telosys.tools.dsl.parser.annotations.UniqueAnnotation;
import org.telosys.tools.dsl.parser.annotations.UnsignedTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.UpdatableAnnotation;

public class Annotations {

	private static List<AnnotationDefinition> annotationDefinitions = new LinkedList<>();
	static {
		
		//annotationsList.add(new AnnotationDefinition(AnnotationName.ID        ));
		annotationDefinitions.add(new IdAnnotation());
		
		//annotationsList.add(new AnnotationDefinition(AnnotationName.AUTO_INCREMENTED        ));
		annotationDefinitions.add(new AutoIncrementedAnnotation());

		//annotationsList.add(new AnnotationDefinition(AnnotationName.NOT_NULL  ));
		//annotationsList.add(new AnnotationDefinition(AnnotationName.NOT_EMPTY ));
		//annotationsList.add(new AnnotationDefinition(AnnotationName.NOT_BLANK ));
		annotationDefinitions.add(new NotNullAnnotation());
		annotationDefinitions.add(new NotEmptyAnnotation());
		annotationDefinitions.add(new NotBlankAnnotation());
		
//		annotationsList.add(new AnnotationDefinition(AnnotationName.MIN,       AnnotationParamType.DECIMAL ));
//		annotationsList.add(new AnnotationDefinition(AnnotationName.MAX,       AnnotationParamType.DECIMAL ));
		annotationDefinitions.add(new MinAnnotation());
		annotationDefinitions.add(new MaxAnnotation());
		
//		annotationsList.add(new AnnotationDefinition(AnnotationName.SIZE_MIN,  AnnotationParamType.INTEGER));
//		annotationsList.add(new AnnotationDefinition(AnnotationName.SIZE_MAX,  AnnotationParamType.INTEGER ));
		annotationDefinitions.add(new SizeMinAnnotation());
		annotationDefinitions.add(new SizeMaxAnnotation());

//		annotationsList.add(new AnnotationDefinition(AnnotationName.PAST       ));
//		annotationsList.add(new AnnotationDefinition(AnnotationName.FUTURE     ));
		annotationDefinitions.add(new PastAnnotation());
		annotationDefinitions.add(new FutureAnnotation());

//		annotationsList.add(new AnnotationDefinition(AnnotationName.PRIMITIVE_TYPE ));
		annotationDefinitions.add(new PrimitiveTypeAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.UNSIGNED_TYPE  ));
		annotationDefinitions.add(new UnsignedTypeAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.OBJECT_TYPE    ));
		annotationDefinitions.add(new ObjectTypeAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.LONG_TEXT      ));
		annotationDefinitions.add(new LongTextAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.EMBEDDED       ));
		annotationDefinitions.add(new EmbeddedAnnotation());
	
		//--- Added in ver 3.2.0
//		annotationsList.add(new AnnotationDefinition(AnnotationName.DEFAULT_VALUE , AnnotationParamType.STRING ));
		annotationDefinitions.add(new DefaultValueAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.INITIAL_VALUE , AnnotationParamType.STRING ));
		annotationDefinitions.add(new InitialValueAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.LABEL         , AnnotationParamType.STRING ));
		annotationDefinitions.add(new LabelAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.INPUT_TYPE    , AnnotationParamType.STRING ));
		annotationDefinitions.add(new InputTypeAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.PATTERN       , AnnotationParamType.STRING ));
		annotationDefinitions.add(new PatternAnnotation());

//		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_NAME          , AnnotationParamType.STRING ));
		annotationDefinitions.add(new DbNameAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_TYPE          , AnnotationParamType.STRING ));
		annotationDefinitions.add(new DbTypeAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_DEFAULT_VALUE , AnnotationParamType.STRING ));
		annotationDefinitions.add(new DbDefaultValueAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_COMMENT       , AnnotationParamType.STRING ));
		annotationDefinitions.add(new DbCommentAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.DB_SIZE          , AnnotationParamType.STRING ));
		annotationDefinitions.add(new DbSizeAnnotation());
		
		//--- Added in ver 3.3.0
//		annotationsList.add(new AnnotationDefinition(AnnotationName.FK                  , AnnotationParamType.STRING ));
		annotationDefinitions.add(new FkAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.OPTIONAL            ));
		annotationDefinitions.add(new OptionalAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.TRANSIENT           ));
		annotationDefinitions.add(new TransientAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.FETCH_TYPE_EAGER    ));
		annotationDefinitions.add(new FetchTypeEagerAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.FETCH_TYPE_LAZY     ));
		annotationDefinitions.add(new FetchTypeLazyAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.MAPPED_BY           , AnnotationParamType.STRING ));
		annotationDefinitions.add(new MappedByAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_ATTR        , AnnotationParamType.STRING ));
		annotationDefinitions.add(new LinkByAttrAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_COL         , AnnotationParamType.STRING ));
		annotationDefinitions.add(new LinkByColAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_FK          , AnnotationParamType.STRING ));
		annotationDefinitions.add(new LinkByFKAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.MANY_TO_MANY        , AnnotationParamType.STRING ));
		annotationDefinitions.add(new ManyToManyAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.ONE_TO_ONE          , AnnotationParamType.STRING ));
		annotationDefinitions.add(new OneToOneAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.INSERTABLE          , AnnotationParamType.BOOLEAN ));
		annotationDefinitions.add(new InsertableAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.UPDATABLE           , AnnotationParamType.BOOLEAN ));
		annotationDefinitions.add(new UpdatableAnnotation());
//		annotationsList.add(new AnnotationDefinition(AnnotationName.LINK_BY_JOIN_ENTITY , AnnotationParamType.STRING ));
		annotationDefinitions.add(new LinkByJoinEntityAnnotation());
		
		//--- Added in ver 3.4.0
		annotationDefinitions.add(new SizeAnnotation());
		annotationDefinitions.add(new UniqueAnnotation());
		annotationDefinitions.add(new MaxLenAnnotation());
		annotationDefinitions.add(new MinLenAnnotation());
	}
	
	private Annotations() {
	}

	/**
	 * Returns all annotations definitions
	 * @return
	 */
	public static List<AnnotationDefinition> getAll() {
		return annotationDefinitions;
	}

	/**
	 * Return annotation definition for the given annotation name (or null if none)
	 * @param annotationName
	 * @return
	 */
	public static AnnotationDefinition get(String annotationName) {
		for ( AnnotationDefinition ad : annotationDefinitions ) {
			if ( ad.getName().equals(annotationName) ) {
				return ad;
			}
		}
		return null ;
	}
	
	// Lists of annotations names for Eclipse  ( cf plugin )
	/**
	 * Returns all annotations names with @ prefix ( @Id, @Size, @OneToMany, etc )
	 * @return
	 */
	public static List<String> getAllAnnotationsWithPrefix() {
		return buildAnnotationsList(false);
	}
	/**
	 * Returns all annotations names with @ prefix 
	 * and parentheses if annotation has a parameter ( @Id, @Size(), @Label(), etc )
	 * @return
	 */
	public static List<String> getAllAnnotationsWithPrefixAndParentheses() {
		return buildAnnotationsList(true);
	}
	/**
	 * @param withParentheses
	 * @return
	 */
	private static List<String> buildAnnotationsList(boolean withParentheses) {
		List<String> list = new LinkedList<>();
		for ( AnnotationDefinition ad : annotationDefinitions ) {
			String annotation = "@" + ad.getName() ;
			if( withParentheses && ad.hasParam() ) {
				annotation = annotation + "()" ;
			}
			list.add(annotation);
		}
		Collections.sort(list);
		return list;
	}
}
