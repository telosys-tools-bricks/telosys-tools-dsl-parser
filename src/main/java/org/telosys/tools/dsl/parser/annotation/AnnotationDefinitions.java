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
import org.telosys.tools.dsl.parser.annotations.GeneratedValueAnnotation;
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

public class AnnotationDefinitions {

	private static List<AnnotationDefinition> annotationDefinitions = new LinkedList<>();
	static {
		
		annotationDefinitions.add(new IdAnnotation());
		
		annotationDefinitions.add(new AutoIncrementedAnnotation());

		annotationDefinitions.add(new NotNullAnnotation());
		annotationDefinitions.add(new NotEmptyAnnotation());
		annotationDefinitions.add(new NotBlankAnnotation());
		
		annotationDefinitions.add(new MinAnnotation());
		annotationDefinitions.add(new MaxAnnotation());
		
		annotationDefinitions.add(new SizeMinAnnotation());
		annotationDefinitions.add(new SizeMaxAnnotation());

		annotationDefinitions.add(new PastAnnotation());
		annotationDefinitions.add(new FutureAnnotation());

		annotationDefinitions.add(new PrimitiveTypeAnnotation());
		annotationDefinitions.add(new UnsignedTypeAnnotation());
		annotationDefinitions.add(new ObjectTypeAnnotation());
		annotationDefinitions.add(new LongTextAnnotation());
		annotationDefinitions.add(new EmbeddedAnnotation());
	
		//--- Added in ver 3.2.0
		annotationDefinitions.add(new DefaultValueAnnotation());
		annotationDefinitions.add(new InitialValueAnnotation());
		annotationDefinitions.add(new LabelAnnotation());
		annotationDefinitions.add(new InputTypeAnnotation());
		annotationDefinitions.add(new PatternAnnotation());

		annotationDefinitions.add(new DbNameAnnotation());
		annotationDefinitions.add(new DbTypeAnnotation());
		annotationDefinitions.add(new DbDefaultValueAnnotation());
		annotationDefinitions.add(new DbCommentAnnotation());
		annotationDefinitions.add(new DbSizeAnnotation());
		
		//--- Added in ver 3.3.0
		annotationDefinitions.add(new FkAnnotation());
		annotationDefinitions.add(new OptionalAnnotation());
		annotationDefinitions.add(new TransientAnnotation());
		annotationDefinitions.add(new FetchTypeEagerAnnotation());
		annotationDefinitions.add(new FetchTypeLazyAnnotation());
		annotationDefinitions.add(new MappedByAnnotation());
		annotationDefinitions.add(new LinkByAttrAnnotation());
		annotationDefinitions.add(new LinkByColAnnotation());
		annotationDefinitions.add(new LinkByFKAnnotation());
		annotationDefinitions.add(new ManyToManyAnnotation());
		annotationDefinitions.add(new OneToOneAnnotation());
		annotationDefinitions.add(new InsertableAnnotation());
		annotationDefinitions.add(new UpdatableAnnotation());
		annotationDefinitions.add(new LinkByJoinEntityAnnotation());
		
		//--- Added in ver 3.4.0
		annotationDefinitions.add(new SizeAnnotation());
		annotationDefinitions.add(new UniqueAnnotation());
		annotationDefinitions.add(new MaxLenAnnotation());
		annotationDefinitions.add(new MinLenAnnotation());
		annotationDefinitions.add(new GeneratedValueAnnotation());
	}
	
	private AnnotationDefinitions() {
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
