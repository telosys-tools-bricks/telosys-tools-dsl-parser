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
package org.telosys.tools.dsl.model.writer;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotations.AbstractAnnotation;
import org.telosys.tools.dsl.parser.annotations.AggregateRootAnnotation;
import org.telosys.tools.dsl.parser.annotations.AutoIncrementedAnnotation;
import org.telosys.tools.dsl.parser.annotations.ContextAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbCatalogAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbCommentAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbDefaultValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbNameAnnotation;
import org.telosys.tools.dsl.parser.annotations.DbSchemaAnnotation;
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
import org.telosys.tools.dsl.parser.annotations.FutureAnnotation;
import org.telosys.tools.dsl.parser.annotations.IdAnnotation;
import org.telosys.tools.dsl.parser.annotations.InMemoryRepositoryAnnotation;
import org.telosys.tools.dsl.parser.annotations.InitialValueAnnotation;
import org.telosys.tools.dsl.parser.annotations.InputTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.InsertableAnnotation;
import org.telosys.tools.dsl.parser.annotations.LabelAnnotation;
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
import org.telosys.tools.dsl.parser.annotations.PackageAnnotation;
import org.telosys.tools.dsl.parser.annotations.PastAnnotation;
import org.telosys.tools.dsl.parser.annotations.PatternAnnotation;
import org.telosys.tools.dsl.parser.annotations.PrimitiveTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.ReadOnlyAnnotation;
import org.telosys.tools.dsl.parser.annotations.SizeAnnotation;
import org.telosys.tools.dsl.parser.annotations.TransientAnnotation;
import org.telosys.tools.dsl.parser.annotations.UniqueAnnotation;
import org.telosys.tools.dsl.parser.annotations.UnsignedTypeAnnotation;
import org.telosys.tools.dsl.parser.annotations.UpdatableAnnotation;
import org.telosys.tools.dsl.tags.Tag;
import org.telosys.tools.dsl.tags.Tags;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.ForeignKeyPart;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.LinkAttribute;
import org.telosys.tools.generic.model.enums.BooleanValue;
import org.telosys.tools.generic.model.enums.Cardinality;
import org.telosys.tools.generic.model.enums.FetchType;
import org.telosys.tools.generic.model.enums.Optional;

public class EntityFileWriter extends AbstractWriter {

	private static final String INDENTATION = "  " ;
	
	public EntityFileWriter(String directory ) {
		super(directory);
	}
	
	public void writeEntity(DslModelEntity entity) {
		String entityFileName = entity.getClassName() + ".entity";
		openFile(entityFileName);
		
		// build and write entity header 
		printLines(buildEntityHeader(entity));
		// write entity opening 
		printLine(entity.getClassName() + " {");
		// build and write all attributes 
		printLine(INDENTATION + "// attributes");
		for ( Attribute attribute : entity.getAttributes() ) {
			printLine(buildAttribute((DslModelAttribute) attribute) ) ;
		}
		// build and write all links 
		printLine(INDENTATION + "// links");
		for ( Link link : entity.getLinks() ) {
			printLine(buildLink((DslModelLink) link) ) ;
		}
		// write entity closing 
		printLine("}");
		
		closeFile();
	}
	
	/**
	 * Builds a list of lines for entity header ( annotations and tags )
	 * @param entity
	 * @return
	 */
	protected List<String> buildEntityHeader(DslModelEntity entity) {
		List<String> lines = new LinkedList<>();
		//--- Entity ANNOTATIONS :
		// Package
		buildAnnotation(lines, new PackageAnnotation(), entity.getPackageName());
		// Database annotations
		buildAnnotation(lines, new DbTableAnnotation(), entity.getDatabaseTable());
		buildAnnotation(lines, new DbCatalogAnnotation(), entity.getDatabaseCatalog());
		buildAnnotation(lines, new DbSchemaAnnotation(), entity.getDatabaseSchema());
		buildAnnotation(lines, new DbCommentAnnotation(), entity.getDatabaseComment());
		buildAnnotation(lines, new DbTablespaceAnnotation(), entity.getDatabaseTablespace());
		buildAnnotationWithoutParam(lines, new DbViewAnnotation(), entity.isDatabaseView());
		// Domain & context annotations
		buildAnnotation(lines, new DomainAnnotation(), entity.getDomain());
		buildAnnotation(lines, new ContextAnnotation(), entity.getContext());
		// AggregateRoot, ReadOnly, 
		buildAnnotationWithoutParam(lines, new AggregateRootAnnotation(), entity.isAggregateRoot());
		buildAnnotationWithoutParam(lines, new ReadOnlyAnnotation(), entity.isReadOnly() );
		buildAnnotationWithoutParam(lines, new InMemoryRepositoryAnnotation(), entity.isInMemoryRepository());
		// Abstract & extends annotations
		buildAnnotationWithoutParam(lines, new AbstractAnnotation(), entity.isAbstract());
		buildAnnotation(lines, new ExtendsAnnotation(), entity.getSuperClass());
		//--- Entity TAGS :
		List<String> tags = buildTags((Tags) entity.getTagContainer());
		for ( String s : tags ) {
			lines.add(s);
		}
		return lines;
	}

	/**
	 * Builds a line for the attribute definition <br>
	 * Example : <br>
	 *  "  firtName : string { @NotNull  #MyTag };"
	 * @param attribute
	 * @return
	 */
	protected String buildAttribute(DslModelAttribute attribute) {
		List<String> annotations = buildAttributeAnnotations(attribute);
		List<String> tags = buildTags((Tags)attribute.getTagContainer());

		StringBuilder sb = new StringBuilder();
		sb.append(INDENTATION);
		sb.append(attribute.getName());
		sb.append(" : ");
		sb.append(attribute.getNeutralType());
		if ( annotations.size() + tags.size() > 0  ) {
			sb.append(" {");
			for ( String s : annotations ) {
				sb.append(" ").append(s);
			}
			for ( String s : tags ) {
				sb.append(" ").append(s);
			}
			sb.append(" }");
		}
		sb.append(";");
		return sb.toString();
	}

	protected String buildLink(DslModelLink link) {
		List<String> annotations = buildLinkAnnotations(link);
		List<String> tags = buildTags((Tags)link.getTagContainer());

		StringBuilder sb = new StringBuilder();
		sb.append(INDENTATION);
		sb.append(link.getFieldName());
		sb.append(" : ");
		sb.append(link.getReferencedEntityName());
		Cardinality cardinality = link.getCardinality() ;
		if ( cardinality == Cardinality.ONE_TO_MANY || cardinality == Cardinality.MANY_TO_MANY ) {
			sb.append("[]");
		}
		if ( annotations.size() + tags.size() > 0  ) {
			sb.append(" {");
			for ( String s : annotations ) {
				sb.append(" ").append(s);
			}
			for ( String s : tags ) {
				sb.append(" ").append(s);
			}
			sb.append(" }");
		}
		sb.append(";");
		return sb.toString();
	}

	protected List<String> buildAttributeAnnotations(DslModelAttribute attribute) {
		List<String> list = new LinkedList<>();
		// @Id
		buildAnnotationWithoutParam(list, new IdAnnotation(), attribute.isKeyElement());
		// @AutoIncremented, @GeneratedValue
		buildAnnotationWithoutParam(list, new AutoIncrementedAnnotation(), attribute.isAutoIncremented());
		buildGeneratedValueAnnotationIfAny(list, attribute);
		// @Dbxxxx (database annotations)
		buildAnnotation(list, new DbNameAnnotation(), attribute.getDatabaseName() );
		buildAnnotation(list, new DbTypeAnnotation(), attribute.getDatabaseType() );
		buildAnnotation(list, new DbDefaultValueAnnotation(), attribute.getDatabaseDefaultValue());
		buildAnnotation(list, new DbCommentAnnotation(), attribute.getDatabaseComment());
		// @NotNull, @NotBlank, @NotEmpty
		buildAnnotationWithoutParam(list, new NotNullAnnotation(), attribute.isNotNull());
		buildAnnotationWithoutParam(list, new NotBlankAnnotation(), attribute.isNotBlank());
		buildAnnotationWithoutParam(list, new NotEmptyAnnotation(), attribute.isNotEmpty());
		// @Unique, @Transient
		buildAnnotationWithoutParam(list, new UniqueAnnotation(), attribute.isUnique());
		buildAnnotationWithoutParam(list, new TransientAnnotation(), attribute.isTransient());
		// @Max / @Min 
		buildAnnotation(list, new MaxAnnotation(), attribute.getMaxValue());
		buildAnnotation(list, new MinAnnotation(), attribute.getMinValue());
		// @Size /  @MaxLen / @MinLen 
		buildAnnotation(list, new SizeAnnotation(), attribute.getSize());
		buildAnnotation(list, new MaxLenAnnotation(), attribute.getMaxLength());
		buildAnnotation(list, new MinLenAnnotation(), attribute.getMinLength());
		// OLD : SizeMaxAnnotation() => attribute.getMaxLength()
		// OLD : SizeMinAnnotation() => attribute.getMinLength()
		// @Label, @InputType, @InitialValue, @DefaultValue, @Pattern, @LongText
		buildAnnotation(list, new LabelAnnotation(), attribute.getLabel());
		buildAnnotation(list, new InputTypeAnnotation(), attribute.getInputType());
		buildAnnotation(list, new InitialValueAnnotation(), attribute.getInitialValue());
		buildAnnotation(list, new DefaultValueAnnotation(), attribute.getDefaultValue());
		buildAnnotation(list, new PatternAnnotation(), attribute.getPattern());
		buildAnnotationWithoutParam(list, new LongTextAnnotation(), attribute.isLongText());
		// Generated type : @ObjectType / @PrimitiveType / @UnsignedType
		buildAnnotationWithoutParam(list, new ObjectTypeAnnotation(), attribute.isObjectTypeExpected());
		buildAnnotationWithoutParam(list, new PrimitiveTypeAnnotation(), attribute.isPrimitiveTypeExpected());
		buildAnnotationWithoutParam(list, new UnsignedTypeAnnotation(), attribute.isUnsignedTypeExpected());
		// @Past / @Future
		buildAnnotationWithoutParam(list, new PastAnnotation(), attribute.isDatePast());
		buildAnnotationWithoutParam(list, new FutureAnnotation(), attribute.isDateFuture());
		// @FK : Foreign Key part annotation(s)
		buildFkAnnotationsIfAny(list, attribute);
		// 
		return list;
	}
	protected List<String> buildLinkAnnotations(DslModelLink link) {
		List<String> list = new LinkedList<>();
		// @Embedded / @Transient
		buildAnnotationWithoutParam(list, new EmbeddedAnnotation(), link.isEmbedded());
		buildAnnotationWithoutParam(list, new TransientAnnotation(), link.isTransient());
		// @Optional
		buildAnnotationWithoutParam(list, new OptionalAnnotation(), link.getOptional() == Optional.TRUE); 
		// @FetchTypeEager / @FetchTypeLazy
		buildAnnotationWithoutParam(list, new FetchTypeEagerAnnotation(),  link.getFetchType() == FetchType.EAGER);
		buildAnnotationWithoutParam(list, new FetchTypeLazyAnnotation(),   link.getFetchType() == FetchType.LAZY );
		// @ManyToMany / @OneToOne
		buildAnnotationWithoutParam(list, new ManyToManyAnnotation(), link.getCardinality() == Cardinality.MANY_TO_MANY );
		buildAnnotationWithoutParam(list, new OneToOneAnnotation(), link.getCardinality() == Cardinality.ONE_TO_ONE );
		// @MappedBy
		buildAnnotation(list, new MappedByAnnotation(), link.getMappedBy() ); 
		// @Insertable(boolean) / @Updatable(boolean) 
		buildAnnotation(list, new InsertableAnnotation(), link.getInsertable());
		buildAnnotation(list, new UpdatableAnnotation(),  link.getUpdatable());
		//--- @LinkByxxxx
		if ( link.isBasedOnForeignKey() ) { // @LinkByFK(FOREIGN_KEY_NAME)
			buildAnnotation(list, new LinkByFKAnnotation(), link.getForeignKeyName());
		}
		else if ( link.isBasedOnJoinEntity() ) { // @LinkByJoinEntity(EntityName)
			buildAnnotation(list, new LinkByJoinEntityAnnotation(), link.getJoinEntityName());
		}
		else if ( link.isBasedOnAttributes() ) { // @LinkByAttr(attr1, attr2, ... )
			buildLinkByAttrAnnotation(list, link);
		}
		// else : link by Foreign Key inference => no annotation 
		return list;
	}	

	protected List<String> buildTags(Tags tags) {
		List<String> list = new LinkedList<>();
		for ( Tag tag : tags.getAllTags() ) {
			String tagString = "";
			if ( tag.hasParameter() ) {
				tagString = "#"+tag.getName()+"("+tag.getParameter()+")";
			}
			else {
				tagString = "#"+tag.getName();
			}
			list.add(tagString);
		}
		return list ;
	}
	
	//-----------------------------------------------------------------------------------------
	
	protected void buildAnnotation(List<String> list, AnnotationDefinition ad, String param) {
		if ( ! StrUtil.nullOrVoid(param) ) {
			list.add( ad.literal(param) );
		}
	}
	protected void buildAnnotation(List<String> list, AnnotationDefinition ad, BigDecimal param) {
		if ( param != null ) {
			list.add( ad.literal(param) );
		}
	}
	protected void buildAnnotation(List<String> list, AnnotationDefinition ad, Integer param) {
		if ( param != null ) {
			list.add( ad.literal(param) );
		}
	}
	protected void buildAnnotation(List<String> list, AnnotationDefinition ad, BooleanValue param) {
		if ( param != null ) {
			if ( param == BooleanValue.TRUE ) {
				list.add( ad.literal("true") );
			}
			else if  ( param == BooleanValue.FALSE ) {
				list.add( ad.literal("false") );
			}
		}
	}
	protected void buildAnnotationWithoutParam(List<String> list, AnnotationDefinition ad, boolean flag ) {
		if ( flag ) {
			list.add( ad.literal() );
		}
	}
	protected void buildFkAnnotationsIfAny(List<String> list, DslModelAttribute attribute) {
		if ( attribute.isFK() ) {
			for ( ForeignKeyPart fkPart : attribute.getFKParts() ) {
				list.add(buildFkAnnotation(fkPart));
			}
		}
	}
	protected String buildFkAnnotation(ForeignKeyPart fkPart) {
		StringBuilder sb = new StringBuilder();
		sb.append("@FK(") ;
		// FK name (optional)
		if ( ! StrUtil.nullOrVoid( fkPart.getFkName() ) ) {
			sb.append(fkPart.getFkName()) ;
			sb.append(", ") ;
		}
		// Referenced entity (mandatory)
		if ( StrUtil.nullOrVoid(fkPart.getReferencedEntityName() ) ) {
			throw new IllegalStateException("ForeignKeyPart has no referenced entity");
		}
		sb.append(fkPart.getReferencedEntityName()) ;
		// Referenced attribute (optional if FK not composite)
		if ( ! StrUtil.nullOrVoid( fkPart.getReferencedAttributeName() ) ) {
			sb.append(".") ;
			sb.append(fkPart.getReferencedAttributeName());
		}
		sb.append(")") ;
		return sb.toString();
	}
	
	protected void buildLinkByAttrAnnotation(List<String> list, DslModelLink link) {
		StringBuilder sb = new StringBuilder();
		// @LinkByAttr(attribute1, attribute2, ... )
		sb.append("@").append(AnnotationName.LINK_BY_ATTR).append("(") ;
		List<LinkAttribute> linkAttributes = link.getAttributes();
		if ( linkAttributes.isEmpty() ) {
			throw new IllegalStateException("@LinkByAttr : link has no attribute");
		}
		int n = 0 ;
		for ( LinkAttribute linkAttribute : linkAttributes) {
			n++;
			if ( n > 1 ) {
				sb.append(", ") ;
			}
			sb.append( linkAttribute.getOriginAttributeName() );
		}
		sb.append(")") ;
		list.add( sb.toString() );
	}
	
	protected void buildGeneratedValueAnnotationIfAny(List<String> list, DslModelAttribute attribute) {
		if ( attribute.getGeneratedValueStrategy() == null ) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("@GeneratedValue(");
		switch ( attribute.getGeneratedValueStrategy() ) {
		case AUTO :
			sb.append("AUTO");
			break;
		case IDENTITY :
			sb.append("IDENTITY");
			break;
		case SEQUENCE :
			completeGeneratedValueSequence(sb, attribute);
			break;
		case TABLE :
			completeGeneratedValueTable(sb, attribute); 
			break;
		case UNDEFINED :
			return;
		}
		sb.append(")") ;
		list.add(sb.toString());
	}
	private void completeGeneratedValueSequence(StringBuilder sb, DslModelAttribute attribute) {
		sb.append("SEQUENCE");
		String generator = attribute.getGeneratedValueGeneratorName();
		String sequence  = attribute.getGeneratedValueSequenceName();
		if ( ( ! StrUtil.nullOrVoid(generator) ) && ( ! StrUtil.nullOrVoid(sequence) ) ) {
			sb.append(", ").append(generator);
			sb.append(", ").append(sequence);
			if ( attribute.getGeneratedValueAllocationSize() != null ) {
				sb.append(", ").append(attribute.getGeneratedValueAllocationSize());
			}
		}
	}
	private void completeGeneratedValueTable(StringBuilder sb, DslModelAttribute attribute) {
		sb.append("TABLE");
		String generator = attribute.getGeneratedValueGeneratorName();
		String table = attribute.getGeneratedValueTableName();
		if ( ( ! StrUtil.nullOrVoid(generator) ) && ( ! StrUtil.nullOrVoid(table) ) ) {
			sb.append(", ").append(generator);
			sb.append(", ").append(table);
			String pkColName = attribute.getGeneratedValueTablePkColumnName();
			String pkColVal  = attribute.getGeneratedValueTablePkColumnValue();
			String tableValColName = attribute.getGeneratedValueTableValueColumnName();
			if ( ( ! StrUtil.nullOrVoid(pkColName) ) 
					&& ( ! StrUtil.nullOrVoid(pkColVal) ) 
					&& ( ! StrUtil.nullOrVoid(tableValColName) ) ) {
				sb.append(", ").append(pkColName);
				sb.append(", ").append(pkColVal);
				sb.append(", ").append(tableValColName);
				if ( attribute.getGeneratedValueAllocationSize() != null ) {
					sb.append(", ").append(attribute.getGeneratedValueAllocationSize());
				}
			}
		}
	}
}
