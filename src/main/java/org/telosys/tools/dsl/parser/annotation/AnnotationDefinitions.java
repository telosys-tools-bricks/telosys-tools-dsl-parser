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

import org.telosys.tools.dsl.parser.annotations.AbstractAnnotation;
import org.telosys.tools.dsl.parser.annotations.AggregateRootAnnotation;
import org.telosys.tools.dsl.parser.annotations.AutoIncrementedAnnotation;
import org.telosys.tools.dsl.parser.annotations.CascadeAnnotation;
import org.telosys.tools.dsl.parser.annotations.ContextAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbCatalogAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbCommentAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbDefaultValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbNameAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbSchemaAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbSizeAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbTableAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbTablespaceAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbViewAnnotation;
import org.telosys.tools.dsl.parser.annotations.DefaultValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.DomainAnnotation;
import org.telosys.tools.dsl.parser.annotations.EmbeddedAnnotation;
import org.telosys.tools.dsl.parser.annotations.ExtendsAnnotation;
import org.telosys.tools.dsl.parser.annotations.FetchTypeEagerAnnotation;
import org.telosys.tools.dsl.parser.annotations.FetchTypeLazyAnnotation;
import org.telosys.tools.dsl.parser.annotations.FkAnnotation;
import org.telosys.tools.dsl.parser.annotations.FutureAnnotation;
import org.telosys.tools.dsl.parser.annotations.GeneratedValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.IdAnnotation;
import org.telosys.tools.dsl.parser.annotations.InMemoryRepositoryAnnotation;
import org.telosys.tools.dsl.parser.annotations.InitialValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.InputTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.InsertableAnnotation;
import org.telosys.tools.dsl.parser.annotations.JoinEntityAnnotation;
import org.telosys.tools.dsl.parser.annotations.LabelAnnotation;
import org.telosys.tools.dsl.parser.annotations.LinkByAttrAnnotation;
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
import org.telosys.tools.dsl.parser.annotations.OrphanRemovalAnnotation;
import org.telosys.tools.dsl.parser.annotations.PackageAnnotation;
import org.telosys.tools.dsl.parser.annotations.PastAnnotation;
import org.telosys.tools.dsl.parser.annotations.PatternAnnotation;
import org.telosys.tools.dsl.parser.annotations.PrimitiveTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.ReadOnlyAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeMaxAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeMinAnnotation;
import org.telosys.tools.dsl.parser.annotations.TransientAnnotation;
import org.telosys.tools.dsl.parser.annotations.UniqueAnnotation;
import org.telosys.tools.dsl.parser.annotations.UnsignedTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.UpdatableAnnotation;

public class AnnotationDefinitions {

	private static List<AnnotationDefinition> annotations = new LinkedList<>();
	static {
		
		annotations.add(new IdAnnotation());
		
		annotations.add(new AutoIncrementedAnnotation());

		annotations.add(new NotNullAnnotation());
		annotations.add(new NotEmptyAnnotation());
		annotations.add(new NotBlankAnnotation());
		
		annotations.add(new MinAnnotation());
		annotations.add(new MaxAnnotation());
		
		annotations.add(new SizeMinAnnotation());
		annotations.add(new SizeMaxAnnotation());

		annotations.add(new PastAnnotation());
		annotations.add(new FutureAnnotation());

		annotations.add(new PrimitiveTypeAnnotation());
		annotations.add(new UnsignedTypeAnnotation());
		annotations.add(new ObjectTypeAnnotation());
		annotations.add(new LongTextAnnotation());
		annotations.add(new EmbeddedAnnotation());
	
		//--- Added in ver 3.2.0
		annotations.add(new DefaultValueAnnotation());
		annotations.add(new InitialValueAnnotation());
		annotations.add(new LabelAnnotation());
		annotations.add(new InputTypeAnnotation());
		annotations.add(new PatternAnnotation());

		annotations.add(new DbNameAnnotation());
		annotations.add(new DbTypeAnnotation());
		annotations.add(new DbDefaultValueAnnotation());
		annotations.add(new DbCommentAnnotation());
		annotations.add(new DbSizeAnnotation());
		
		//--- Added in ver 3.3.0
		annotations.add(new FkAnnotation());
		annotations.add(new OptionalAnnotation());
		annotations.add(new TransientAnnotation());
		annotations.add(new FetchTypeEagerAnnotation());
		annotations.add(new FetchTypeLazyAnnotation());
		annotations.add(new MappedByAnnotation());
		annotations.add(new LinkByAttrAnnotation());
		annotations.add(new LinkByFKAnnotation());
		annotations.add(new ManyToManyAnnotation());
		annotations.add(new OneToOneAnnotation());
		annotations.add(new InsertableAnnotation());
		annotations.add(new UpdatableAnnotation());
		annotations.add(new LinkByJoinEntityAnnotation());
		
		//--- Added in ver 3.4.0 
		annotations.add(new SizeAnnotation());
		annotations.add(new UniqueAnnotation());
		annotations.add(new MaxLenAnnotation());
		annotations.add(new MinLenAnnotation());
		annotations.add(new GeneratedValueAnnotation());
		//--- Added in ver 3.4.0 - entity scope
		annotations.add(new DbTableAnnotation());
		annotations.add(new DbViewAnnotation());
		annotations.add(new DbSchemaAnnotation());
		annotations.add(new DbCatalogAnnotation());
		annotations.add(new DbTablespaceAnnotation());
		
		annotations.add(new AbstractAnnotation());
		annotations.add(new ExtendsAnnotation());
		annotations.add(new PackageAnnotation());
		annotations.add(new ReadOnlyAnnotation());
		annotations.add(new InMemoryRepositoryAnnotation());
		
		annotations.add(new AggregateRootAnnotation());
		annotations.add(new DomainAnnotation());
		annotations.add(new ContextAnnotation());
		
		//--- Added in ver 4.1.0
		annotations.add(new OrphanRemovalAnnotation()); 
		annotations.add(new CascadeAnnotation()); 
		annotations.add(new JoinEntityAnnotation()); 
		
	}
	
	private AnnotationDefinitions() {
	}

	/**
	 * Returns all annotations definitions
	 * @return
	 */
	public static List<AnnotationDefinition> getAll() {
		return annotations;
	}

	/**
	 * Return annotation definition for the given annotation name (or null if none)
	 * @param annotationName
	 * @return
	 */
	public static AnnotationDefinition get(String annotationName) {
		for ( AnnotationDefinition ad : annotations ) {
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
		for ( AnnotationDefinition ad : annotations ) {
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
