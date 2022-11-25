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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyAttribute;
import org.telosys.tools.generic.model.LinkAttribute;

public class DslModelForeignKey implements ForeignKey {
	
	private static final String CONTRUCTOR_ERROR = "Foreign Key constructor error : ";
	private static int implicitForeignKeyIdentifier = 0 ;
	
    private final String fkName; 
    private final String originEntityName; // entity holding this FK
    private final String referencedEntityName; // entity referenced by this FK
    private final boolean explicitFK; 
    
    private final List<ForeignKeyAttribute> attributes = new LinkedList<>();
    
    private DslModelForeignKey(boolean explicit, String fkName, String originEntityName, String referencedEntityName) {
		super();
		this.explicitFK = explicit;
		
		if ( StrUtil.nullOrVoid(fkName)) {
			throw new IllegalArgumentException(CONTRUCTOR_ERROR + "'name' is null or void");
		}
        this.fkName = fkName;
        
		if ( StrUtil.nullOrVoid(originEntityName)) {
			throw new IllegalArgumentException(CONTRUCTOR_ERROR + "'originEntityName' is null or void");
		}
        this.originEntityName = originEntityName;
        
		if ( StrUtil.nullOrVoid(referencedEntityName)) {
			throw new IllegalArgumentException(CONTRUCTOR_ERROR + "'referencedEntityName' is null or void");
		}
        this.referencedEntityName = referencedEntityName;
    }
    
    /**
     * Constructor for an explicit Foreign Key (with an explicit name)
     * @param fkName
     * @param originEntityName
     * @param referencedEntityName
     */
    public DslModelForeignKey(String fkName, String originEntityName, String referencedEntityName) {
		this(true, fkName, originEntityName, referencedEntityName); 
	}
    
    /**
     * Constructor for an implicit Foreign Key (without explicit name)
     * @param originEntityName
     * @param referencedEntityName
     */
    public DslModelForeignKey(String originEntityName, String referencedEntityName) {
		this(false, createImplicitForeignKeyName(originEntityName, referencedEntityName), 
				originEntityName, referencedEntityName);
    }
	
    private static final String createImplicitForeignKeyName(String originEntityName, String referencedEntityName) {
    	implicitForeignKeyIdentifier++;
		return "FK_IMPLICIT" + implicitForeignKeyIdentifier + "_" + originEntityName + "_" + referencedEntityName ;	
	}
	
	@Override
    public String getName() {
        return fkName;
    }
	
	@Override
    public String getOriginEntityName() {
        return originEntityName;
    }
	
	@Override
    public String getReferencedEntityName() {
        return referencedEntityName;
    }

    @Override
    public List<ForeignKeyAttribute> getAttributes() {
        return attributes;
    }
	
    @Override
    public boolean isComposite() {
		return attributes.size() > 1 ;
	}
	
    //@Override
    public boolean isExplicit() {
		return this.explicitFK ;
	}
	
    public void addAttribute(DslModelForeignKeyAttribute fkAttribute) {
    	// fkAttribute has always valid attributes (not null & not void)
        this.attributes.add(fkAttribute);
    }

    /**
     * Returns all the link attributes defined in this Foreign Key
     * @return
     * @since  3.4.0
     */
	public List<LinkAttribute> getLinkAttributes() {
		List<LinkAttribute> joinAttributes = new LinkedList<>();
		for (ForeignKeyAttribute fka : this.attributes ) {
	    	// each fk attribute has always valid attributes (not null & not void)
			DslModelLinkAttribute linkAttribute = new DslModelLinkAttribute(
					fka.getOriginAttributeName(),
					fka.getReferencedAttributeName());
			joinAttributes.add(linkAttribute);
		}
		return joinAttributes;
	}
}
