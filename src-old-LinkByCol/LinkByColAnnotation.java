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
package org.telosys.tools.dsl.parser.annotations;

import java.util.List;

import org.telosys.tools.dsl.commons.JoinAttributesBuilder;
import org.telosys.tools.dsl.commons.ReferenceDefinitions;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.generic.model.JoinAttribute;

/**
 * "LinkByCol" annotation 
 * . LinkByCol(Column1 [, Column2 [, Column3] ] )
 * 
 * Added in v 3.3.0
 * 
 * @author Laurent Guerin
 *
 */
public class LinkByColAnnotation extends LinkByAnnotation {

	public LinkByColAnnotation() {
		super(AnnotationName.LINK_BY_COL, AnnotationParamType.STRING);
	}

	@Override
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(entity, link, paramValue);
//		List<JoinColumn> joinColumns = getJoinColumns(entity, link, (String)paramValue);
		List<JoinAttribute> joinAttributes = getJoinAttributes(entity, link, (String)paramValue);
//		link.setJoinColumns(joinColumns);
		link.setJoinAttributes(joinAttributes);
	}
	
//	protected List<JoinColumn> getJoinColumns(DslModelEntity entity, DslModelLink link, String paramValue) throws ParamError {
	protected List<JoinAttribute> getJoinAttributes(DslModelEntity entity, DslModelLink link, String paramValue) throws ParamError {
		ReferenceDefinitions columnsRefDef = buildReferenceDefinitions(paramValue);
		checkNotVoid(entity, link, columnsRefDef);
//		JoinColumnsBuilder jcb = getJoinColumnsBuilder();
		JoinAttributesBuilder jcb = getJoinAttributesBuilder();

//		return jcb.buildJoinColumnsFromRefDef(columnsRefDef);
		return jcb.buildJoinAttributesFromRefDef(columnsRefDef);
	}
}
