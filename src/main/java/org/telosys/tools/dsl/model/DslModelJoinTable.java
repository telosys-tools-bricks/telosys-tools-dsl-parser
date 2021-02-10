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
package org.telosys.tools.dsl.model;

import java.util.List;

import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;


/**
 * "JoinTable" implementation for DSL model 
 * 
 * @author Laurent Guerin
 * @since 3.3.0
 */
public class DslModelJoinTable implements JoinTable 
{
	private final String name ;

	private final String schema;

	private final String catalog;
	
	private final List<JoinColumn>   joinColumns ;

	private final List<JoinColumn>   inverseJoinColumns;
	
	public DslModelJoinTable(Entity entity, List<JoinColumn> joinColumns, List<JoinColumn> inverseJoinColumns) {
		super();
		this.name    = entity.getDatabaseTable();
		this.schema  = entity.getDatabaseSchema();
		this.catalog = entity.getDatabaseCatalog();
		this.joinColumns = joinColumns;
		this.inverseJoinColumns = inverseJoinColumns;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getName() {
		return name;
	}
//	public void setName(String name) {
//		this.name = name;
//	}

	//--------------------------------------------------------------------------
	@Override
	public String getSchema() {
		return schema;
	}
//	public void setSchema(String schema) {
//		this.schema = schema;
//	}

	//--------------------------------------------------------------------------
	@Override
	public String getCatalog() {
		return catalog;
	}
//	public void setCatalog(String catalog) {
//		this.catalog = catalog;
//	}

	//--------------------------------------------------------------------------
//	public void setJoinColumns( List<JoinColumn> joinColumns ) 	{
//		this.joinColumns = joinColumns ;
//	}
	@Override
	public List<JoinColumn> getJoinColumns() {
		return this.joinColumns;
	}
	
	//--------------------------------------------------------------------------
//	public void setInverseJoinColumns( List<JoinColumn> inverseJoinColumns ) {
//		this.inverseJoinColumns = inverseJoinColumns ;
//	}
	@Override
	public List<JoinColumn> getInverseJoinColumns() {
		return this.inverseJoinColumns;		
	}
	
	//--------------------------------------------------------------------------
//	public String getCheckSum() {
//		return name;
//	}
}
